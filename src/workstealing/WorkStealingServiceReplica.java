package workstealing;

import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.server.Executable;
import bftsmart.tom.server.Recoverable;
import bftsmart.tom.server.SingleExecutable;
import bftsmart.util.ThroughputStatistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
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
public final class WorkStealingServiceReplica extends ParallelServiceReplica {
    private final boolean[] exec_flags;
    private final AtomicInteger[][] markers;
    private final AtomicInteger[][] sync_marker;
    private final Semaphore[][] semaphores;
    private final int stealSize;
    private String stealerType;

    int listSize;
    
    public WorkStealingServiceReplica(
            int id, Executable executor, Recoverable recoverer, int initialWorkers, int stealSize, String stealerType, boolean distExpo,
            int duration, int warmup
    ) {
        super(id, executor, recoverer);
        this.exec_flags = new boolean[initialWorkers];
        this.markers = new AtomicInteger[initialWorkers][initialWorkers];
        this.sync_marker = new AtomicInteger[100][2];                       // sparse matrix, 'cause the class ids does not start at zero
        this.semaphores = new Semaphore[initialWorkers][initialWorkers];
        this.stealSize = stealSize;
        this.stealerType = stealerType;
        
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
    
    public WorkStealingServiceReplica(
            int id, Executable executor, Recoverable recoverer, int initialWorkers, ClassToThreads[] cts, int stealSize, 
            String stealerType, boolean distExpo, int duration, int warmup, int listSize
    ) {
        super(id, executor, recoverer, cts);
        this.exec_flags = new boolean[initialWorkers];
        this.markers = new AtomicInteger[initialWorkers][initialWorkers];
        this.sync_marker = new AtomicInteger[100][2];                       // sparse matrix, 'cause the class ids does not start at zero
        this.semaphores = new Semaphore[initialWorkers][initialWorkers];
        this.listSize = listSize;
        
        this.scheduler = new WorkStealingScheduler(initialWorkers, cts, id, distExpo);
        this.stealSize = stealSize;
        this.stealerType = stealerType;
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
                this.sync_marker[cc.classId][0] = new AtomicInteger();
                this.sync_marker[cc.classId][1] = new AtomicInteger();
            }
        }
    }

    private String stealerDesc(String s){
        String ret="ws theads";
        if(s.equals("smartStealer")){
            ret="barrier-free workstealing threads";
        }
        if(s.equals("smartOptStealer")){
            ret="optimistic workstealing threads";
        }
        return ret;
    }

    @Override
    protected void initWorkers(int n, int id, int duration, int warmup) {
        statistics = new ThroughputStatistics(id, n, "results_"+stealerType+"_" + id + ".txt", stealerType, duration, warmup);
        //for (int i = 0; i < n; i++) {
        System.out.println("Starting " + n + " " + stealerDesc(stealerType) );
        switch(this.stealerType){
            // SMART STEALERS
            case "smartStealer":
                for (int i = 0; i < n; i++) new ServiceReplicaSmartStealer(this.scheduler.getMapping().getAllQueues(), i, n).start();
                break;
            case "smartBoundedStealer":
                for (int i = 0; i < n; i++) new ServiceReplicaSmartBoundedStealer(this.scheduler.getMapping().getAllQueues(), i, n).start();
                break;
            case "smartOptStealer":
                for (int i = 0; i < n; i++) new ServiceReplicaSmartOptStealer(this.scheduler.getMapping().getAllQueues(), i, n).start();
                break;
            case "smartOptBoundedStealer":
                for (int i = 0; i < n; i++) new ServiceReplicaSmartOptBoundedStealer(this.scheduler.getMapping().getAllQueues(), i, n).start();
                break;


            // DEFAULT STEALERS
            case "defaultStealer":
                for (int i = 0; i < n; i++) new ServiceReplicaStealer(this.scheduler.getMapping().getAllQueues(), i, n).start();
                break;
            case "defaultBoundedStealer":
                for (int i = 0; i < n; i++) new ServiceReplicaBoundedStealer(this.scheduler.getMapping().getAllQueues(), i, n).start();
                break;
            case "defaultOptStealer":
                for (int i = 0; i < n; i++) new ServiceReplicaOptStealer(this.scheduler.getMapping().getAllQueues(), i, n).start();
                break;
            case "defaultOptBoundedStealer":
                for (int i = 0; i < n; i++) new ServiceReplicaOptBoundedStealer(this.scheduler.getMapping().getAllQueues(), i, n).start();
                break;
            
            
            // RANDOMIC STEALERS
            case "randStealer":
                for (int i = 0; i < n; i++) new ServiceReplicaRandStealer(this.scheduler.getMapping().getAllQueues(), i, n).start();
                break;
            case "randBoundedStealer":
                for (int i = 0; i < n; i++) new ServiceReplicaRandBoundedStealer(this.scheduler.getMapping().getAllQueues(), i, n).start();
                break;
            case "randOptStealer":
                for (int i = 0; i < n; i++) new ServiceReplicaRandOptStealer(this.scheduler.getMapping().getAllQueues(), i, n).start();
                break;
            case "randOptBoundedStealer":
                for (int i = 0; i < n; i++) new ServiceReplicaRandOptBoundedStealer(this.scheduler.getMapping().getAllQueues(), i, n).start();
                break;
                
            default:
                for (int i = 0; i < n; i++) new ServiceReplicaSmartStealer(this.scheduler.getMapping().getAllQueues(), i, n).start();
        }
        //}
    }
    
    
    
    

    // SMART STEALERS
    private class ServiceReplicaSmartStealer extends Thread {
        protected final WorkStealingFIFOQueue requests;
        protected final int thread_id, numWorkers;
        protected final BlockingQueue[] requests_queues;
        protected final int[] cycles;
        
        protected int localConc = 0;
        protected int localSync = 0;
        protected int localTotal = 0;
        protected int localTotalMedidos = 0;
        boolean continuaMedindoTempos = true;

        long tempos [][] = new long[5][TOTAL_REQS_MEDIDOS_TEMPO_EXEC];

        public ServiceReplicaSmartStealer(BlockingQueue[] requests_queues, int thread_id, int numWorkers) {
            this.thread_id = thread_id;
            this.requests = (WorkStealingFIFOQueue) requests_queues[thread_id];
            this.requests_queues = requests_queues;
            this.numWorkers = numWorkers;
            this.cycles = new int[100]; // sparse array, 'cause the class ids does not start at zero
            
//            (new Timer()).scheduleAtFixedRate(new TimerTask() {
//                public void run() {
//                   if(localTotal > 0){
//                        int media = (100 * localSync) / localTotal;
//                        System.out.println("Thread "+thread_id+": "+media);
//                   }else{
//                       System.out.println("Thread "+thread_id+": "+0);
//                   }
//                }
//            }, 5000, 1000);
        }
        
        protected void execute(MessageContextPair msg){

            long iniExec = System.nanoTime();

            msg.resp = ((SingleExecutable) executor).executeOrdered(msg.operation, null);
            
            long endExec = System.nanoTime();

            if(continuaMedindoTempos && localTotal > (TOTAL_REQS_MEDIDOS_TEMPO_EXEC)){

                // TIMES ARE CAPTURED IN THE FOLLOWING SEQUENCE:
                // --- decision ---- recMsgs ---- scheduled ------ iniExec ------ endexec

                // recMsg   = recMsgs - decision
                // sched    = scheduled - recMsgs
                // waitExec = iniExec - scheduled
                // exec     = endExec - iniExec
                // total    = endexec - decision
                long tempoTotal = endExec - msg.decisionTime;
                long tempoRecMsgs = msg.recMsgTime - msg.decisionTime;
                long tempoSchedule = msg.scheduledTime - msg.recMsgTime;
                long tempoWaitForExec = iniExec - msg.scheduledTime;
                long tempoExec = endExec - iniExec;

                tempos[0][localTotalMedidos] = tempoRecMsgs;
                tempos[1][localTotalMedidos] = tempoSchedule;
                tempos[2][localTotalMedidos] = tempoWaitForExec;
                tempos[3][localTotalMedidos] = tempoExec;
                tempos[4][localTotalMedidos] = tempoTotal;

                localTotalMedidos++;
                if(localTotalMedidos == TOTAL_REQS_MEDIDOS_TEMPO_EXEC ){
                    continuaMedindoTempos = false;
                    generateFile();
                }
            }

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

            localTotal++;

        }

        private void generateFile(){
            if(id > 0) return;
            if(thread_id > 0) return;
            System.out.println(thread_id + " - Criando arquivo ...");
            try {
                PrintWriter pw = new PrintWriter(new FileWriter(new File("ws_"+listSize+"_rep"+id+"_thread_"+thread_id+".txt")));

                for (int i=0; i < localTotalMedidos; i++ ){
                    pw.println(
                        tempos[0][i]+"\t"+
                        tempos[1][i]+"\t"+
                        tempos[2][i]+"\t"+
                        tempos[3][i]+"\t"+
                        tempos[4][i]
                    );
                }
                pw.flush();

            } catch (IOException e) {}
        }

        protected void execute(MessageContextPair msg, int victim){

            long iniExec = System.nanoTime();
            msg.resp = ((SingleExecutable) executor).executeOrdered(msg.operation, null);
            long endExec = System.nanoTime();

            if(continuaMedindoTempos && localTotal > TOTAL_REQS_MEDIDOS_TEMPO_EXEC){
                
                // TIMES ARE CAPTURED IN THE FOLLOWING SEQUENCE:
                // --- decision ---- recMsgs ---- scheduled ------ iniExec ------ endexec

                // recMsg   = recMsgs - decision
                // sched    = scheduled - recMsgs
                // waitExec = iniExec - scheduled
                // exec     = endExec - iniExec
                // total    = endexec - decision
                long tempoTotal = endExec - msg.decisionTime;
                long tempoRecMsgs = msg.recMsgTime - msg.decisionTime;
                long tempoSchedule = msg.scheduledTime - msg.recMsgTime;
                long tempoWaitForExec = iniExec - msg.scheduledTime;
                long tempoExec = endExec - iniExec;

                tempos[0][localTotalMedidos] = tempoRecMsgs;
                tempos[1][localTotalMedidos] = tempoSchedule;
                tempos[2][localTotalMedidos] = tempoWaitForExec;
                tempos[3][localTotalMedidos] = tempoExec;
                tempos[4][localTotalMedidos] = tempoTotal;

                localTotalMedidos++;
                if(localTotalMedidos == TOTAL_REQS_MEDIDOS_TEMPO_EXEC ){
                    continuaMedindoTempos = false;
                    generateFile();
                }
            }

            //  

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

            localTotal++;

        }
        
        protected void steal(ExecutionFIFOQueue<MessageContextPair> stealQueue, int classId){
            MessageContextPair msg;
            try{
                int i = this.thread_id;
                for(int j = 0; j < this.numWorkers; j++){
                    
                    if(i == (this.numWorkers-1)) i = 0;
                    else i++;
                    
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
                java.util.logging.Logger.getLogger(ServiceReplicaSmartStealer.class.getName()).log(Level.SEVERE, null, ie);
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
                                
                                // calculate current synchronization cycle of this thread in this class
                                this.cycles[msg.classId] = 1 - this.cycles[msg.classId];
                                
                                // signal that arrives to this cycle
                                if(sync_marker[msg.classId][cycles[msg.classId]].incrementAndGet() == ct.tIds.length){
                                    // if was the last to arrive, execute
                                    execute(msg);
                                    sync_marker[msg.classId][cycles[msg.classId]].set(0); // signal the end of this cycle - resets variable for next use
                                }
                                else {
                                    // while this cycle is not finished, steal
                                    while(sync_marker[msg.classId][cycles[msg.classId]].get() != 0){
                                        steal(stealQueue, msg.classId);    // only steal commands from classes non conflicting with C.class
                                    }
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
                    java.util.logging.Logger.getLogger(ServiceReplicaSmartStealer.class.getName()).log(Level.SEVERE, null, ie);
                }
            }
        }

    }
    
    private class ServiceReplicaSmartBoundedStealer extends ServiceReplicaSmartStealer {

        public ServiceReplicaSmartBoundedStealer(BlockingQueue[] requests_queues, int id, int numWorkers) {
            super(requests_queues, id, numWorkers);
        }

        @Override
        protected void steal(ExecutionFIFOQueue<MessageContextPair> stealQueue, int classId){
            MessageContextPair msg;
            try{
                int i = this.thread_id;
                for(int j = 0; j < this.numWorkers; j++){
                    
                    if(i == (this.numWorkers-1)) i = 0;
                    else i++;
                    
                    if(i != this.thread_id){    // not gonna steal from my self
                        if(((WorkStealingFIFOQueue)this.requests_queues[i]).stealSomeToQueue(stealQueue, exec_flags, markers, classId, this.thread_id, i, stealSize)){
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
                java.util.logging.Logger.getLogger(ServiceReplicaSmartBoundedStealer.class.getName()).log(Level.SEVERE, null, ie);
                System.exit(0);
            }
        }
    }
    
    private class ServiceReplicaSmartOptStealer extends ServiceReplicaSmartStealer {

        public ServiceReplicaSmartOptStealer(BlockingQueue[] requests_queues, int id, int numWorkers) {
            super(requests_queues, id, numWorkers);
        }

        @Override
        protected void steal(ExecutionFIFOQueue<MessageContextPair> stealQueue, int classId){
            MessageContextPair msg;
            try{
                int i = this.thread_id;
                for(int j = 0; j < this.numWorkers; j++){
                    
                    if(i == (this.numWorkers-1)) i = 0;
                    else i++;
                    
                    if(i != this.thread_id){    // not gonna steal from my self
                        if(((WorkStealingFIFOQueue)this.requests_queues[i]).optimisticSteal(stealQueue, exec_flags, markers, classId, this.thread_id, i)){
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
                java.util.logging.Logger.getLogger(ServiceReplicaSmartOptStealer.class.getName()).log(Level.SEVERE, null, ie);
                System.exit(0);
            }
        }
    }
    
    private class ServiceReplicaSmartOptBoundedStealer extends ServiceReplicaSmartStealer {

        public ServiceReplicaSmartOptBoundedStealer(BlockingQueue[] requests_queues, int id, int numWorkers) {
            super(requests_queues, id, numWorkers);
        }

        @Override
        protected void steal(ExecutionFIFOQueue<MessageContextPair> stealQueue, int classId){
            MessageContextPair msg;
            try{
                int i = this.thread_id;
                for(int j = 0; j < this.numWorkers; j++){
                    
                    if(i == (this.numWorkers-1)) i = 0;
                    else i++;
                    
                    if(i != this.thread_id){    // not gonna steal from my self
                        if(((WorkStealingFIFOQueue)this.requests_queues[i]).optimisticStealSome(stealQueue, exec_flags, markers, classId, this.thread_id, i, stealSize)){
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
                java.util.logging.Logger.getLogger(ServiceReplicaSmartOptBoundedStealer.class.getName()).log(Level.SEVERE, null, ie);
                System.exit(0);
            }
        }
    }
    
    
    
    
    
    // DEFAULT STEALERS
    private class ServiceReplicaStealer extends ServiceReplicaSmartStealer {

        public ServiceReplicaStealer(BlockingQueue[] requests_queues, int id, int numWorkers) {
            super(requests_queues, id, numWorkers);
        }

        @Override
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
                java.util.logging.Logger.getLogger(ServiceReplicaStealer.class.getName()).log(Level.SEVERE, null, ie);
                System.exit(0);
            }
        }
    }
    
    private class ServiceReplicaBoundedStealer extends ServiceReplicaSmartStealer {

        public ServiceReplicaBoundedStealer(BlockingQueue[] requests_queues, int id, int numWorkers) {
            super(requests_queues, id, numWorkers);
        }

        @Override
        protected void steal(ExecutionFIFOQueue<MessageContextPair> stealQueue, int classId){
            MessageContextPair msg;
            try{
                for(int i = 0; i < this.numWorkers; i++){
                    if(i != this.thread_id){    // not gonna steal from my self
                        if(((WorkStealingFIFOQueue)this.requests_queues[i]).stealSomeToQueue(stealQueue, exec_flags, markers, classId, this.thread_id, i, stealSize)){
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
                java.util.logging.Logger.getLogger(ServiceReplicaBoundedStealer.class.getName()).log(Level.SEVERE, null, ie);
                System.exit(0);
            }
        }
    }
    
    private class ServiceReplicaOptStealer extends ServiceReplicaSmartStealer {

        public ServiceReplicaOptStealer(BlockingQueue[] requests_queues, int id, int numWorkers) {
            super(requests_queues, id, numWorkers);
        }

        @Override
        protected void steal(ExecutionFIFOQueue<MessageContextPair> stealQueue, int classId){
            MessageContextPair msg;
            try{
                for(int i = 0; i < this.numWorkers; i++){
                    if(i != this.thread_id){    // not gonna steal from my self
                        if(((WorkStealingFIFOQueue)this.requests_queues[i]).optimisticSteal(stealQueue, exec_flags, markers, classId, this.thread_id, i)){
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
                java.util.logging.Logger.getLogger(ServiceReplicaOptStealer.class.getName()).log(Level.SEVERE, null, ie);
                System.exit(0);
            }
        }
    }
    
    private class ServiceReplicaOptBoundedStealer extends ServiceReplicaSmartStealer {

        public ServiceReplicaOptBoundedStealer(BlockingQueue[] requests_queues, int id, int numWorkers) {
            super(requests_queues, id, numWorkers);
        }

        @Override
        protected void steal(ExecutionFIFOQueue<MessageContextPair> stealQueue, int classId){
            MessageContextPair msg;
            try{
                for(int i = 0; i < this.numWorkers; i++){
                    if(i != this.thread_id){    // not gonna steal from my self
                        if(((WorkStealingFIFOQueue)this.requests_queues[i]).optimisticStealSome(stealQueue, exec_flags, markers, classId, this.thread_id, i, stealSize)){
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
                java.util.logging.Logger.getLogger(ServiceReplicaOptBoundedStealer.class.getName()).log(Level.SEVERE, null, ie);
                System.exit(0);
            }
        }
    }
    
    
    
    
    
    // RANDOMIC STEALERS
    private class ServiceReplicaRandStealer extends ServiceReplicaSmartStealer {

        public ServiceReplicaRandStealer(BlockingQueue[] requests_queues, int id, int numWorkers) {
            super(requests_queues, id, numWorkers);
        }

        @Override
        protected void steal(ExecutionFIFOQueue<MessageContextPair> stealQueue, int classId){
            MessageContextPair msg;
            try{
                for(int j = 0; j < this.numWorkers; j++){
                    int i = new Random().nextInt(this.numWorkers);
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
                java.util.logging.Logger.getLogger(ServiceReplicaRandStealer.class.getName()).log(Level.SEVERE, null, ie);
                System.exit(0);
            }
        }
    }
    
    private class ServiceReplicaRandBoundedStealer extends ServiceReplicaSmartStealer {

        public ServiceReplicaRandBoundedStealer(BlockingQueue[] requests_queues, int id, int numWorkers) {
            super(requests_queues, id, numWorkers);
        }

        @Override
        protected void steal(ExecutionFIFOQueue<MessageContextPair> stealQueue, int classId){
            MessageContextPair msg;
            try{
                for(int j = 0; j < this.numWorkers; j++){
                    int i = new Random().nextInt(this.numWorkers);
                    if(i != this.thread_id){    // not gonna steal from my self
                        if(((WorkStealingFIFOQueue)this.requests_queues[i]).stealSomeToQueue(stealQueue, exec_flags, markers, classId, this.thread_id, i, 50)){
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
                java.util.logging.Logger.getLogger(ServiceReplicaRandBoundedStealer.class.getName()).log(Level.SEVERE, null, ie);
                System.exit(0);
            }
        }
    }
    
    private class ServiceReplicaRandOptStealer extends ServiceReplicaSmartStealer {

        public ServiceReplicaRandOptStealer(BlockingQueue[] requests_queues, int id, int numWorkers) {
            super(requests_queues, id, numWorkers);
        }

        @Override
        protected void steal(ExecutionFIFOQueue<MessageContextPair> stealQueue, int classId){
            MessageContextPair msg;
            try{
                for(int j = 0; j < this.numWorkers; j++){
                    int i = new Random().nextInt(this.numWorkers);
                    if(i != this.thread_id){    // not gonna steal from my self
                        if(((WorkStealingFIFOQueue)this.requests_queues[i]).optimisticSteal(stealQueue, exec_flags, markers, classId, this.thread_id, i)){
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
                java.util.logging.Logger.getLogger(ServiceReplicaRandOptStealer.class.getName()).log(Level.SEVERE, null, ie);
                System.exit(0);
            }
        }
    }
    
    private class ServiceReplicaRandOptBoundedStealer extends ServiceReplicaSmartStealer {

        public ServiceReplicaRandOptBoundedStealer(BlockingQueue[] requests_queues, int id, int numWorkers) {
            super(requests_queues, id, numWorkers);
        }

        @Override
        protected void steal(ExecutionFIFOQueue<MessageContextPair> stealQueue, int classId){
            MessageContextPair msg;
            try{
                for(int j = 0; j < this.numWorkers; j++){
                    int i = new Random().nextInt(this.numWorkers);
                    if(i != this.thread_id){    // not gonna steal from my self
                        if(((WorkStealingFIFOQueue)this.requests_queues[i]).optimisticStealSome(stealQueue, exec_flags, markers, classId, this.thread_id, i, stealSize)){
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
                java.util.logging.Logger.getLogger(ServiceReplicaRandOptBoundedStealer.class.getName()).log(Level.SEVERE, null, ie);
                System.exit(0);
            }
        }
    }
    
}
