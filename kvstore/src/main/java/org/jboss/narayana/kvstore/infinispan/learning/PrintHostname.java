package org.jboss.narayana.kvstore.infinispan.learning;

class PrintHostname
{
  public static void main(String args[]) throws java.net.UnknownHostException
  {
        System.out.println(java.net.InetAddress.getLocalHost().getHostName());
  }
}