package workstealing;

import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.server.Executable;
import bftsmart.tom.server.Recoverable;
import bftsmart.tom.server.SingleExecutable;
import bftsmart.util.ThroughputStatistics;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import parallelism.ClassToThreads;
import parallelism.ExecutionFIFOQueue;
import parallelism.MessageContextPair;
import parallelism.MultiOperationCtx;
import parallelism.ParallelMapping;
import parallelism.ParallelServiceReplica;

/**
 *
 * @author elbatista
 */
public final class WorkStealingServiceReplica_v2 extends ParallelServiceReplica {
    private final boolean[] exec_flags;
    private final AtomicInteger[][] markers;
    private final AtomicInteger[] sync_marker;
    private final Semaphore[][] semaphores;
    
    public WorkStealingServiceReplica_v2(
            int id, Executable executor, Recoverable recoverer, int initialWorkers, int stealSize, String stealerType, boolean distExpo,
            int duration, int warmup
    ) {
        super(id, executor, recoverer);
        this.exec_flags = new boolean[initialWorkers];
        this.markers = new AtomicInteger[initialWorkers][initialWorkers];
        this.sync_marker = new AtomicInteger[100];                       // sparse matrix, 'cause the class ids does not start at zero
        this.semaphores = new Semaphore[initialWorkers][initialWorkers];
        
        int[] ids = new int[initialWorkers];
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = i;
        }
        
        int[] conflicts = new int[1];
        conflicts[0] = ParallelMapping.SYNC_ALL;
        cts[ParallelMapping.CONC_ALL] = new WorkStealingClassToThreads(ParallelMapping.CONC_ALL, ClassToThreads.CONC, ids, conflicts);
        
        conflicts = new int[2];
        conflicts[0] = ParallelMapping.CONC_ALL;
        conflicts[1] = ParallelMapping.SYNC_ALL;
        cts[ParallelMapping.SYNC_ALL] = new WorkStealingClassToThreads(ParallelMapping.SYNC_ALL, ClassToThreads.SYNC, ids, conflicts);
        
        
        this.scheduler = new WorkStealingScheduler(initialWorkers, cts, id, distExpo);
        initFlags(this.scheduler.getNumWorkers(), cts);
        initWorkers(this.scheduler.getNumWorkers(), id, duration, warmup);
    }
    
    public WorkStealingServiceReplica_v2(
            int id, Executable executor, Recoverable recoverer, int initialWorkers, ClassToThreads[] cts, int stealSize, 
            String stealerType, boolean distExpo, int duration, int warmup
    ) {
        super(id, executor, recoverer, cts);
        this.exec_flags = new boolean[initialWorkers];
        this.markers = new AtomicInteger[initialWorkers][initialWorkers];
        this.sync_marker = new AtomicInteger[100];
        this.semaphores = new Semaphore[initialWorkers][initialWorkers];
        
        this.scheduler = new WorkStealingScheduler(initialWorkers, cts, id, distExpo);
        initFlags(this.scheduler.getNumWorkers(), cts);
        
        initWorkers(this.scheduler.getNumWorkers(), id, duration, warmup);
    }
    
    private void initFlags(int initialWorkers, ClassToThreads[] cts){
        for (int i=0; i < initialWorkers; i++) {
            this.exec_flags[i] = false;
            for(int j=0; j < initialWorkers; j++){
                if(i != j){
                    this.markers[i][j] = new AtomicInteger();
                    this.semaphores[i][j] = new Semaphore(0);
                }
            }
        }
        for(ClassToThreads cc : cts){
            if(cc != null){
                this.sync_marker[cc.classId] = new AtomicInteger();
            }
        }
    }

    @Override
    protected void initWorkers(int n, int id, int duration, int warmup) {
        statistics = new ThroughputStatistics(id, n, "results_defaultStealer_v2_" + id + ".txt", "defaultStealer_v2", duration, warmup);
        System.out.println("Starting " + n + " semi-blocking workstealing threads");
        for (int i = 0; i < n; i++) new ServiceReplicaDefaultStealer(this.scheduler.getMapping().getAllQueues(), i, n).start();
    }
    
    private class ServiceReplicaDefaultStealer extends Thread {
        protected final WorkStealingFIFOQueue requests;
        protected final int thread_id, numWorkers;
        protected final BlockingQueue[] requests_queues;
        
        protected int localConc = 0;
        protected int localSync = 0;
        protected int localTotal = 0;

        public ServiceReplicaDefaultStealer(BlockingQueue[] requests_queues, int thread_id, int numWorkers) {
            this.thread_id = thread_id;
            this.requests = (WorkStealingFIFOQueue) requests_queues[thread_id];
            this.requests_queues = requests_queues;
            this.numWorkers = numWorkers;
        }
        
        protected void execute(MessageContextPair msg){
            msg.resp = ((SingleExecutable) executor).executeOrdered(msg.operation, null);
            MultiOperationCtx ctx = ctxs.get(msg.request.toString());
            ctx.add(msg.index, msg.resp);
            if (ctx.response.isComplete() && !ctx.finished && (ctx.interger.getAndIncrement() == 0)) {
                ctx.finished = true;
                ctx.request.reply = new TOMMessage(
                    id, ctx.request.getSession(),
                    ctx.request.getSequence(), ctx.response.serialize(), SVController.getCurrentViewId()
                );
                replier.manageReply(ctx.request, null);
            }
            statistics.computeStatistics(thread_id, 1);
        }

        protected void execute(MessageContextPair msg, int victim){
            msg.resp = ((SingleExecutable) executor).executeOrdered(msg.operation, null);
            MultiOperationCtx ctx = ctxs.get(msg.request.toString());
            ctx.add(msg.index, msg.resp);
            if (ctx.response.isComplete() && !ctx.finished && (ctx.interger.getAndIncrement() == 0)) {
                ctx.finished = true;
                ctx.request.reply = new TOMMessage(
                    id, ctx.request.getSession(),
                    ctx.request.getSequence(), ctx.response.serialize(), SVController.getCurrentViewId()
                );
                replier.manageReply(ctx.request, null);
            }
            
            statistics.computeStatistics(thread_id, 1, victim);
        }
        
        protected void steal(ExecutionFIFOQueue<MessageContextPair> stealQueue, int classId){
            MessageContextPair msg;
            try{
                for(int i = 0; i < this.numWorkers; i++){
                    if(i != this.thread_id){    // not gonna steal from my self
                        if(((WorkStealingFIFOQueue)this.requests_queues[i]).stealToQueue(stealQueue, exec_flags, markers, classId, this.thread_id, i)){
                            do {
                                msg = stealQueue.getNext();
                                execute(msg, i);
                            }
                            while (stealQueue.goToNext());
                            markers[this.thread_id][i].compareAndSet(1, 0);     // try to reset, if couldnt then it was already 2 (nothing changes)
                            if(markers[this.thread_id][i].compareAndSet(2, 0)){ // if it was already 2, then h is waiting, wakes it up
                                semaphores[this.thread_id][i].release();        // post
                            }
                            break; // steals and executes once, then tries its own again
                        }
                    }
                }
            }
            catch (Exception ie) {
                java.util.logging.Logger.getLogger(ServiceReplicaDefaultStealer.class.getName()).log(Level.SEVERE, null, ie);
                System.exit(0);
            }
        }
        
        @Override
        public void run() {
            MessageContextPair msg;
            ExecutionFIFOQueue<MessageContextPair> execQueue = new ExecutionFIFOQueue<>();
            ExecutionFIFOQueue<MessageContextPair> stealQueue = new ExecutionFIFOQueue<>();
            
            while (true) {
                try {
                    if(this.requests.drainToQueue(execQueue, exec_flags, this.thread_id)){
                        do {
                            msg = execQueue.getNext();
                            ClassToThreads ct = scheduler.getMapping().getClass(msg.classId);
                            
                            if (ct.type == ClassToThreads.CONC) {
                                localConc++;
                                execute(msg);
                            } else if (ct.type == ClassToThreads.SYNC && ct.tIds.length == 1) {//SYNC mas só com 1 thread, não precisa usar barreira
                                localSync++;
                                execute(msg);
                            } else if (ct.type == ClassToThreads.SYNC) {
                                localSync++;
                                // there is sequential command to execute, no thread is stealing from this one
                                for (int i = 0; i < this.numWorkers; i++) {
                                    if(i != this.thread_id){ // no need to wait for my self, since im not stealing from my self
                                        // 1= x stole from t; 2= and t is waiting
                                        if(markers[i][this.thread_id].compareAndSet(1, 2)){
                                            semaphores[i][this.thread_id].acquire(); // t waits x to finish stolen work
                                        }
                                    }
                                }
                                
                                if(sync_marker[msg.classId].incrementAndGet() == ct.tIds.length){
                                    execute(msg);
                                    sync_marker[msg.classId].set(0);
                                    scheduler.getMapping().getBarrier(msg.classId).await();
                                }
                                else {
                                    while(sync_marker[msg.classId].get() != 0){
                                        steal(stealQueue, msg.classId);
                                    }
                                    scheduler.getMapping().getBarrier(msg.classId).await();
                                }
                            }
                            else if (ct.type == ClassToThreads.SYNC && ct.tIds.length == 1) {
                                System.out.println("SYNC com só 1 thread no mapeamento, nao precisa esperar !!!");
                                execute(msg);
                            }
                        }
                        while (execQueue.goToNext());
                    }
                    else {
                        steal(stealQueue, WorkStealingParallelMapping.NO_CLASS);
                    }
                }
                catch (Exception ie) {
                    java.util.logging.Logger.getLogger(ServiceReplicaDefaultStealer.class.getName()).log(Level.SEVERE, null, ie);
                }
            }
        }

    }
    
}
