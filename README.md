# Early Scheduling on steroids: Boosting Parallel State Machine Replication

This package contains the source code that implements two different techniques for boosting the performance of Early Scheduling in parallel SMR built on top of BFT-SMaRt (src/), the binary file (dist/), the libraries needed (lib/), the running script (p_smartrun.sh) and the configuration files (config/).
For a detailed explanation about how to configure the system, please check the BFT-SMaRt github page (https://github.com/bft-smart/library).

<br>

## Overview

State machine replication (SMR) is a standard approach to fault tolerance in which replicas must execute requests deterministically and thus serially.
For performance, some techniques allow concurrent execution of requests in SMR while keeping determinism.
Such techniques exploit the fact that independent requests can execute concurrently.
A promising category of early scheduling solutions trade scheduling freedom for simplicity, allowing expedite decisions during scheduling.
This repository contains the implementation of early scheduling which proposes an automated mechanism to schedule requests to threads, restricting its overhead (O(1)).
Moreover, it contains the implementation of techniques that explore further improvements to the basic mechanism, namely the use of alternative **synchronization techniques** and **work-stealing adaptation**.

In the following we explain how to implement and execute an application using these techiques.
For this, we use the linked list demo used in the experiments.

**Implementation:**

Basically, to implement a replicated service it is necessary to follow the same steps used in BFT-SMaRt (https://github.com/bft-smart/library/wiki/Getting-Started-with-BFT-SMaRt).
Additionally, it is necessary to inform the requests conflicts by providing a conflict definition and class-to-threads mappings.
The linked list operations used in the experiments was the following: add -- to include (write) an element in the list; and contains -- to check if some element is in the list (read).
Afterwards, it is necessary to create a ServiceReplica object providing the conflict definition, the number of worker threads, the version of the workstealing algorithm (when using workstealing) among some other parameters already used in BFT-SMaRt.

<br>

## Execution

It is necessary to execute the server replicas and the clients using the [p_smartrun.sh](p_smartrun.sh) script, as follows. More information on how to execute the system in a distributed environment can be found at https://github.com/bft-smart/library/wiki/Getting-Started-with-BFT-SMaRt. You may need to compile the code first, using [ant](https://ant.apache.org/).

1) To execute a single shard server replica, it is necessary to use the following command.

```
./p_smartrun.sh demo.list.ListServer '<procId>-<numThreads>-<initEntries>-<CBASE>-<busyWait>-<workstealing>-<duration>-<warmup>-<wsversion>'

<procId> = the process identifier
<numThreads> = number of worker threads; use 0 for the traditional sequential SMR
<initEntries> = the initial list size (correspond to operation cost)
<CBASE> = true to use the late scheduling technique, false otherwise *
<busyWait> = true to use the late busy-wait technique, false otherwise *
<workstealing> = true to use the workstealing technique, false otherwise * 
<duration> = the total duration of collecting throughput data from the experiment (in seconds)
<warmup> = the duration of the warming up fase of the experiment, prior to start collecting throughput data (in seconds)
<wsversion> = when using workstealing, the algorithm version number:
    1 - steal on empty queues
    2 - semi blocking
    3 - barrier free
    4 - optimistic

* to use Early Scheduling technique use false in all others (CBASE, busyWait and workstealing)
```

For example, you should use the following commands to execute three replicas (to tolerate up to one crash failure) using the optimistic workstealing technique, 8 threads and 1k entries in the list.

```
./p_smartrun.sh demo.list.ListServer "0-8-1000-false-false-true-60-20-4"
./p_smartrun.sh demo.list.ListServer "1-8-1000-false-false-true-60-20-4"
./p_smartrun.sh demo.list.ListServer "2-8-1000-false-false-true-60-20-4"

```

To execute the early scheduling technique:

```
./p_smartrun.sh demo.list.ListServer "0-8-1000-false-false-false-60-20-0"
./p_smartrun.sh demo.list.ListServer "1-8-1000-false-false-false-60-20-0"
./p_smartrun.sh demo.list.ListServer "2-8-1000-false-false-false-60-20-0"

```

2) To execute the clients, it is necessary to use the following command.


```
./p_smartrun.sh demo.list.ListClientMO <num clients> <client id> <maxIndex> <conflict percentage>

num clients = number of threads clients to be created in the process, each thread represents one client
client id = the client identifier
maxIndex = the list size, clients will use in the requests a random value ranging from 0 to maxIndex-1
conflict percent = the percentage of write requests in the workload
```
For example, you should use the following commands to execute 200 clients distributed in four machines/processes, using a workload with 5% of writes.

```
./p_smartrun.sh demo.list.ListClientMO 50 4001 1000 5
./p_smartrun.sh demo.list.ListClientMO 50 5001 1000 5
./p_smartrun.sh demo.list.ListClientMO 50 6001 1000 5
./p_smartrun.sh demo.list.ListClientMO 50 7001 1000 5

```

Notice you can try different parameters and exercise different configurations for both replicas and clients.


**Important tip:** BFT-SMaRt uses two configuration files (see the ```config``` folder) to define the protocol behavior (```config/system.config```) and the location of each replica (```config/hosts.config```). After some modification in these files, you need to delete the file ```config/currentView```. See the BFT-SMaRt page for a detailed explanation about system configuration (https://github.com/bft-smart/library).

<br>

### Early Scheduling

This technique uses the notion of classes of requests used by a programmer to express the concurrency in an application.
In brief, the idea is to group service requests in classes and then specify how classes must be synchronized.
For example, we can model the previously mentioned linked list application with a class of read requests and a class of write requests. The class of write requests conflicts with itself and with the class of read requests.
This ensures that a write is serialized with reads and with other writes. It is also possible to consider more elaborate concurrency models that assume sharded application state with read and write operations within and across shards.
Afterwards, these requests classes are statically mapped to working threads. The client needs to inform the class its request belongs and the scheduler dispatches it according with this information. A detailed description about this scheduling technique can be found in the following paper:

- **Eduardo Alchieri, Fernando Dotti and Fernando Pedone. Early Scheduling in Parallel State Machine Replication. ACM Symposium on Cloud Computing, 2018.** (https://dl.acm.org/citation.cfm?id=3267825)

<br>

### Early Scheduling Analysis 

Early scheduling has been shown to provide significant performance improvements in state machine replication. However, in the paper bellow we evaluate the impact of the restrictions imposed by the early scheduling technique. In particular, it shows that threads may be idle while pending independent requests are available to be executed, leading to poor processor utilization. We characterize resource underutilization for workloads with different rates of conflicting requests, number of threads, and number of request classes. This was the paper which gave insights for new opportunities to further enhance the early scheduling technique, leading to the improvements implemented in this repository. A detailed description about the analysis can be found in the following paper:

- **Elia Batista, Eduardo Alchieri, Fernando Dotti and Fernando Pedone. Resource Utilization Analysis of Early Scheduling in Parallel State Machine Replication. 9th Latin-American Symposium on Dependable Computing (LADC), 2019.** (https://ieeexplore.ieee.org/document/8995730)
