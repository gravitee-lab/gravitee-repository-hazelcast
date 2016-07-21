[![Build Status](http://build.gravitee.io/jenkins/buildStatus/icon?job=gravitee-repository-hazelcast)](http://build.gravitee.io/jenkins/view/Tous/job/gravitee-repository-hazelcast/)

# Gravitee Hazelcast Repository

Key value and cache repositories provided by Hazelcast.

This repository implementation is made to connect to a remote hazelcast cluster. 
If you do not specify addresses then a local embeded hazelcast with default configuration will be launched.  


## Requirement

The minimum requirement is :
 * Maven3 
 * Jdk8

For user gravitee snapshot, You need the declare the following repository in you maven settings :

https://oss.sonatype.org/content/repositories/snapshots


## Building

```
$ git clone https://github.com/gravitee-io/gravitee-repository-hazelcast.git
$ cd gravitee-repository-hazelcast
$ mvn clean package
```

## Installing

Unzip the gravitee-repository-hazelcast-0.1.1-SNAPSHOT.zip in the gravitee home directory.
 


## Configuration

repository.hazelcast options : 

| Parameter                           |   default  | example                         |
| ----------------------------------- | ---------- | ------------------------------- |
|  **Setup**                          |||
| mode								  | node       | mode: "client" or "node"        |
| configFile                          |            | /opt/config/hazelcast-cache.xml |
|  **Or (client mode)**               |||  
| addresses                           |            | - server1:5701<br /> - server2:5701   |
| username  _optional_                |            | myLogin                         |
| password  _optional_                |            | myPassword                      |
| connectionTimeout                   | 60000      | -1 (no timeout)                 |
| connectionAttemptPeriod             | 3000       | 10000 (10 seconds)              |
| connectionAttemptLimit              | 2          | 100                             |
| socketLingerSeconds                 | 3          |                                 |
| socketBufferSize                    | 32         |                                 |
| **Parameter for KV repository**     |||
| map                                 | key_value  | myCache                         |
