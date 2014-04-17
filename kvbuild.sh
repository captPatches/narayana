#!/bin/sh

export NARAYANA_BUILD=1 AS_BUILD=0 NARAYANA_TESTS=0 JTA_AS_TESTS=0 RTS_AS_TESTS=0 RTS_TESTS=0 JTA_CDI_TESTS=0 TXF_TESTS=0 XTS_TESTS=0 XTS_AS_TESTS=0 txbridge=0 QA_TESTS=0 BLACKTIE=0 SUN_ORB=0 JAC_ORB=1


export ANT_HOME=/home/hudson/apache-ant-1.8.2
export PATH=$ANT_HOME/bin:$PATH

./scripts/hudson/narayana.sh

./kvstore/etc/infinispan-server-6.0.2/bin/standalone.sh &

./build.sh -f kvstore/pom.xml clean test
