/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism;

import bftsmart.tom.server.Executable;
import bftsmart.tom.server.Recoverable;
import bftsmart.util.ThroughputStatistics;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author elbatista
 */
public class BusyWaitParallelServiceReplica extends ParallelServiceReplica {
    
    private final AtomicInteger[][] sync_marker;

    public BusyWaitParallelServiceReplica(int id, Executable executor, Recoverable recoverer, int initialWorkers, int duration, int warmup) {
        super(id, executor, recoverer, initialWorkers, duration, warmup);
        this.sync_marker = new AtomicInteger[100][2];   // sparse matrix, 'cause the class ids does not start at zero
        for(ClassToThreads cc : scheduler.getMapping().getClasses()){
            if(cc != null){
                this.sync_marker[cc.classId][0] = new AtomicInteger();
                this.sync_marker[cc.classId][1] = new AtomicInteger();
            }
        }
        
    }
    
    public BusyWaitParallelServiceReplica(int id, Executable executor, Recoverable recoverer, int initialWorkers, ClassToThreads[] cts, int duration, int warmup) {
        super(id, executor, recoverer, initialWorkers, cts, duration, warmup);
        this.sync_marker = new AtomicInteger[100][2];   // sparse matrix, 'cause the class ids does not start at zero
        for(ClassToThreads cc : scheduler.getMapping().getClasses()){
            if(cc != null){
                this.sync_marker[cc.classId][0] = new AtomicInteger();
                this.sync_marker[cc.classId][1] = new AtomicInteger();
            }
        }
        //statistics = new ThroughputStatistics(id, initialWorkers, "results_busyWait_" + id + ".txt", "EARLY BUSY WAIT", duration, warmup);
    }

    @Override
    protected void initWorkers(int n, int id, int duration, int warmup) {
        statistics = new ThroughputStatistics(id, n, "results_busyWait_" + id + ".txt", "EARLY BUSY WAIT", duration, warmup);
        int tid = 0;
        for (int i = 0; i < n; i++) {
            new SyncWaitFreeWorker((FIFOQueue) this.scheduler.getMapping().getAllQueues()[i], tid).start();
            tid++;
        }
    }

    private class SyncWaitFreeWorker extends ParallelServiceReplica.ServiceReplicaWorker {
        
        protected final int[] cycles;
        
        public SyncWaitFreeWorker(FIFOQueue<parallelism.MessageContextPair> requests, int id) {
            super(requests, id);
            this.cycles = new int[100]; // sparse array, 'cause the class ids does not start at zero
        }
        
        @Override
        public void run() {
            MessageContextPair msg = null;
            ExecutionFIFOQueue<MessageContextPair> execQueue = new ExecutionFIFOQueue();
            while (true) {
                try {
                    this.requests.drainToQueue(execQueue);
                    localTotal = localTotal + execQueue.getSize();
                    do {
                        msg = execQueue.getNext();
                        ClassToThreads ct = scheduler.getMapping().getClass(msg.classId);
                        if (ct.type == ClassToThreads.CONC) {
                            exec(msg);
                            localConc++;
                        } else if (ct.type == ClassToThreads.SYNC && ct.tIds.length == 1) {
                            //SYNC mas só com 1 thread, não precisa usar barreira
                            exec(msg);
                            localSync++;
                        } else if (ct.type == ClassToThreads.SYNC) {
                            localSync++;
//                            if (thread_id == scheduler.getMapping().getExecutorThread(msg.classId)) {
//                                scheduler.getMapping().getBarrier(msg.classId).await();
//                                exec(msg);
//                                scheduler.getMapping().getBarrier(msg.classId).await();
//                            } else {
//                                scheduler.getMapping().getBarrier(msg.classId).await();
//                                scheduler.getMapping().getBarrier(msg.classId).await();
//                            }
                            
                            // calculate current synchronization cycle of this thread in this class
                            this.cycles[msg.classId] = 1 - this.cycles[msg.classId];
                            
                            // signal that arrives to this cycle
                            if(sync_marker[msg.classId][cycles[msg.classId]].incrementAndGet() == ct.tIds.length){
                                // if was the last to arrive, execute
                                exec(msg);
                                
                                sync_marker[msg.classId][cycles[msg.classId]].set(0); // signal the end of this cycle - resets variable for next use
                            }
                            else {
                                // while this cycle is not finished...
                                while(sync_marker[msg.classId][cycles[msg.classId]].get() != 0){
                                    // ... busy waits
                                }
                            }
                        } else if (msg.classId == ParallelMapping.CONFLICT_RECONFIGURATION) {
                            scheduler.getMapping().getReconfBarrier().await();
                            scheduler.getMapping().getReconfBarrier().await();
                        }
                    }
                    while (execQueue.goToNext());
                } catch (Exception ie) {
                    ie.printStackTrace();
                    continue;
                }
            }
        }
    }
}
