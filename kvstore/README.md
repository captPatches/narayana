
KVStore prototype
=================

If you are reading this, chances are you're on an experimental branch which contains my prototype work for a Key/Value backed ObjectStore implementation. Congratulations and Beware.

jonathan.halliday@redhat.com, 2014-03

What's going on here? Part one: Ancient History
-----------------------------------------------

Narayana (formerly JBossTS and before that ArjunaTS, hence the weird package naming) is a transaction manager ('TM'). Transaction managers coordinate multiple Resource managers (RMs) using a two phase commit protocol (2PC). During execution of this protocol, they need to record certain information to stable storage, such that it can survive a machine failure. This transaction log storage in generally a drive array (RAID).

The part of Narayana which deals with this is termed the ObjectStore. Narayana is an RM in its own right, as well as a TM, hence the general name - more than just the transaction log can be held in the store. The internal APIs that define the store are in package (ArjunaCore/arjuna/classes/)com.arjuna.ats.arjuna.objectstore

TxLog is the basic API for recording transaction log data. The critical method is:

    write_committed (Uid uid, String typename, OutputObjectState buffer)

where Uid is an internal identifier for the object instance, typename is an internal identifier for the type of the instance and the OutputObjectState is a serialized representation of the Object instance. Once upon a time this was C++ code, then Java code interoperable with C++, hence the custom serialization mechanism.

For each transaction, during the 2PC the transaction engine will invoke its ObjectStore to write the transaction log using this method. The transaction is blocked whilst the write takes place, so performance is critical.

If a crash occurs, the TM needs some way to get information back out of the log for recovery purposes:

RecoveryStore provides this API. The important methods are:

    allObjUids (String typename, InputObjectState buffer, int match)
    allTypes (InputObjectState buffer)
    InputObjectState read_committed (Uid uid, String typename)

The first two are index scanning methods that populate the provided buffer with the identities of all known typenames and instances of a given type. The last one is the means by which the individual serialized state of a given object is read.

The ObjectStore may contain instances of many types of object, which may require different recovery mechanisms. The recovery manager has plugins that deal with each known type. The recovery system will periodically scan the store to find items requiring recovery and hand them off to the relevant plugin for action. These methods are not performance critical, since recovery is a background task.

In the default case, the ObjectStore is implemented on a regular filesystem. Directory hierarchies handle the types, with individual files, named according to the Uid value, containing the serialized state. So, one file per object i.e. per transaction.  This mechanism is relatively simple, in that it delegates much of the work to the filesystem. A write involves creating a new file in the relevant directory and writing the serialized object state into it. Recovery scanning involves iterating the filenames in the directory. However, this design is relatively slow, since it's doing random access to the disk for writes.

Random access on a traditional spinning disk requires a head seek operation, which makes it take longer than a sequential write. There is additional overhead related to file creation and opening. Hence it's more performant to write the transaction log data to a single append-only file. This technique is called journaling.  The cost of the performance gain is complexity: we now need to coordinate multiple threads (transactions) accessing the shared log concurrently. We also need to work harder to implement recovery, since we can no longer just delegate the scanning work to the filesystem.

Narayana cheats somewhat by borrowing an existing high speed journal implementation from HornetQ, JBoss's enterprise messaging (JMS) implementation. That solves some of the problems for us. What remains is adapting our ObjectStore to work in terms of the functionality provided by the HornetQ journal rather than the filesystem. The code for this is in package (ArjunaCore/arjuna/classes/)com.arjuna.ats.internal.arjuna.objectstore  This code is concerned mainly with maintaining the index that relates the ObjectStore's notion of identity (typename+uid) to the Journal's (a Long sequence number for the record). Its got a larger RAM footprint than the filesystem store because it has to hold this index in memory. It's also got somewhat higher latency on writes, since the journal batches operations and hence threads may have a wait a little while for the next batch. It's got very good throughput though, since the number of fsync operations that disk can perform often limits the number of transaction per second with the filesystem store, whilst the hornetq store can write multiple transactions per fsync with a batch operation.

ref: [http://jbossts.blogspot.co.uk/2011/03/more-speed.html](http://jbossts.blogspot.co.uk/2011/03/more-speed.html)

Sidenote: That article mentions an in-memory store based on a ConcurrentHashMap. It's called VolatileStore and is included for testing purposes only. Since it's not durable it's entirely unsuited for production deployments.

This is all well and good if the machine running the transaction manager has a nice fast RAID array. In some environments this is not the case. So we also have an ObjectStore implementation that uses a JDBC database to store the ObjectStore. This has the advantage that the store does not have to be on the same machine as the transaction manager process, since JDBC driver handle the networking for you.  A remote mounted filesystem would have some of the same advantages, but typically these offer less robust persistence and atomicity guarantees and are less performant. Note that a good database will also batch writes, so throughput with the JDBC store can be higher than with the filesystemstore even when the db is running on the local machine. The JDBC store is mostly for administrative convenience rather than performance though - it allows the machines running transaction managers to be treated (more or less) as stateless.



What's going on here? Part two: Modern History
----------------------------------------------

Cloud computing brings a new deployment model, in which we have a large number of nodes and scale up or down according to need. There is typically a lot of virtualisation abstraction between us and the storage hardware, making it difficult to reason about disk reliability and performance. So, we prefer to avoid the filesystem or hornetq journals in such cases. Likewise we don't want to run a local db server. The JDBC ObjectStore still provides some hope though, since we can run a single db server on reliable disks and have all our TM nodes share it.  But it's got the potential to become a bottleneck, not to mention a single point of failure. So, time for plan B.

We have a lot of nodes. The odds of all of them failing at the same time are smallish, especially if we span over multiple failure domains e.g. different datacentres. Memory is very fast compared to disk, even against SSDs.  Datacentre local networks are not too shabby either. Therefore, we want an Objectstore implementation that is backed by replicated in-memory copies of the data on a group of machines.

Accomplishing this involves a number of challenges.

Firstly the ObjectStore API must be adapted to work in terms of the primitives exposed by the underlying store, much as with the hornetq journal. Indeed the prototype code borrows a lot of its design from the hornetq journal store implementation. (Long term todo item: figure out what commonality there is and refactor it out into a shared abstraction)  The code for this is in (ArjunaCore/arjuna/classes/)com.arjuna.ats.internal.arjuna.objectstore.kvstore  The package name derives from Key/Value, the Map like API that's the lowest common denominator interface for the underlying storage systems we want to use. We assume a get(id)->Object and put(id,Object)->void API, but critically don't assume any indexing i.e. no 'allKeys()->Set' method being available, since some distributed stores don't provide this. If the underlying storage does, then the job is simpler, since we can delegate the recovery scanning operations to it.  In the absence of such functionality we need to cheat...

 If we use the Uid value (or better Uid+typename, since Uid on its own is only coincidentally unique thanks to the Uid impl, it's not guaranteed such) then we can't recover, since we don't know what keys to ask for. We need key names we know a-priori, so we can get them at recovery time without local state. So, dirty trick time: take a globally unique node identifier prefix and append a slot id e.g. array subscript, such that the key names are 'mynodename-slot0', 'mynodename-slot1' ... 'mynodename-slotN' and the value stored under these keys is a record that contains the uid value, typename and objectstate. Recovery then involves building a local index by iterating all the key names and using any records returned to build a map from (type+uid)->serialisedObject.  This looks broadly similar to the hornetq journal's recovery code, save that with the journal we get the records replayed in write order rather than having to ask for them by name one at a time. The node prefix allows the store to be shared by multiple transaction managers. This could also be accomplished by a namespacing mechanism if the underlying store provides one. A write on the ObjectStore therefore requires that we locate and allocate a free 'slot' i.e. key name. Maintaining the free list (which may not necessarily involve using an actual List) whilst allowing good concurrency is likely to be a challenge. If we could use the native objectstore key (i.e. typename+uid) we would not have that problem, but we wouldn't have recovery either. todo item: investigate the performance advantage of direct mapping i.e. how much better off are we if we go find an underlying store that supports key iteration, or to put it another way: how much slower can such a store be than an alternative that does not, whilst still allowing us to come out ahead overall.

 The MapStore class provides a simple array backed storage implementation for the kvstore abstraction layer, allowing single node in-memory testing. Unlike the VolatileStore this combination does actually (almost...) implement the RecoveryStore API methods, but like the VolatileStore it's not actually recoverable in the event of a machine failure.

 Next up, moving the backing state out of the JVM RAM, so it will survive a JVM crash. For this we need a K/V store implementation we can plug in. Fortunately there are several of those lying around.  [Infinispan](http://infinispan.org/) is an in-memory data grid layered on [JGroups](http://www.jgroups.org/). Both are JBoss projects.  Infinispan provides a rich API, as well as functionality for replication. JGroups provides a much lower level API, meaning more direct control and less overhead at the cost of having to write more code.  [memcached](http://memcached.org/)  provides something in-between - a higher level but less functional API and no replication. Then we have the less obvious candidates like [RAMCloud](https://ramcloud.stanford.edu) which may provide for interesting comparisons.

Sidenote: speaking of interesting comparisons, [Gluster](http://www.gluster.org/) could provide a distributed, replicated filesystem layered on RAM disks, which would allow us to use the filesystem (or hornetq) code. Probably has most of the drawbacks of NFS though?

Code in the (kvstore/)org.jboss.narayana.kvstore package provides clients that use the previously discussed kvstore API with various underlying storage mechanisms. This code is outside the main source tree (and hence not built by the Narayana's build scripts) since it introduces additional dependencies. As with the hornetq code we may eventually move it into (ArjunaCore/arjuna/classes/)com.arjuna.ats.internal.arjuna.objectstore and update the main build's maven deps accordingly, but for now it's kept out to allow easier merging of changes from master.

What's going on here? Part three: The Future
--------------------------------------------

The existing prototype code provides some preliminary implementation work. The code inside the Narayana tree is, in general, more complete and robust than the bit outside, since the former is largely borrowed from the existing hornetq store code. It's still nowhere near complete or production ready though, just a little better thought out.

First up, the remaining required functionality needs to be implemented, since without that there is nothing to test.

Once we've got a suitable store plugged in to the API, we may or may not be done with writing code. If the store supports replication (e.g. infinispan) then we're good and only need to worry about configuration. If it doesn't, we need another abstraction layer to deal with that - have e.g. RAID1 semantics where a single logical KVStore operation is fanned out to 2 (or more) KVStore implementations e.g. two different memcached servers. The tricky bit is reconciling divergent state where e.g. one replica's write succeeds whilst the other fails.  In general this work qualifies as reinventing the wheel and should probably be avoided if possible.

Next we need to plug in various backends (infinispan, jgroups, memcached, ramcloud) and experiment with various tradeoffs and configurations to see what works and performs well.

For functional testing, the ObjectStore API is exercised by existing Narayana unit tests and qa tests. Unit tests run as part of the build scripts and use the default objectstore by default. So using those to exercise a new store implementation would involve putting the code into the main build then changing the existing tests to use the new store, or subclassing existing tests to override the config.  The qa tests are run by separate scripts that will accept a config file and classpath override, so wiring a new store into those runs would be possible too.

For performance testing, the prototype code includes a primitive multi-threaded JTA transaction runner that uses a dummy XAResource implementation. It's crude, but should allow us to benchmark alternative ObjectStore implementations for side by side comparison.  Preliminary results indicate that the memcached has potential throughput on a par with an SSD, but only up to the limits of the network bandwidth. The serialized state for a transaction log is ~600bytes, to which you need to add the key, typename, uid and protocol overheads.  A 1 GBit/s network saturates a lot faster than than a 6 GBit/s SATA-3 bus...  Perhaps we need to look at batching and compressing entries (you won't get much compression benefit on something as small as a single 600 byte record) or changing the tx engine to log less info in a record, though that latter approach is definitely out of scope for this work and is not specific to any one ObjectStore implementation.

For performance tuning, use the team's JProfiler licence or the flight recorder functionality in Oracle's JDK.