/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism;

import bftsmart.consensus.messages.MessageFactory;
import bftsmart.consensus.roles.Acceptor;
import bftsmart.consensus.roles.Proposer;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ReplicaContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.core.ExecutionManager;
import bftsmart.tom.core.ParallelTOMLayer;
import bftsmart.tom.core.messages.TOMMessage;

import bftsmart.tom.core.messages.TOMMessageType;
import bftsmart.tom.leaderchange.CertifiedDecision;
import bftsmart.tom.server.Executable;
import bftsmart.tom.server.Recoverable;
import bftsmart.tom.server.SingleExecutable;
import bftsmart.tom.util.ShutdownHookThread;
import bftsmart.tom.util.TOMUtil;
import bftsmart.util.MultiOperationRequest;
import bftsmart.util.ThroughputStatistics;
import demo.list.ListClientMO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;
import parallelism.scheduler.DefaultScheduler;
import parallelism.scheduler.Scheduler;

/**
 *
 * @author alchieri
 */
public class SequentialServiceReplica extends ServiceReplica {

    protected Scheduler scheduler;
    public ThroughputStatistics statistics;

    protected Map<String, MultiOperationCtx> ctxs = new Hashtable<>();

    protected int localTotal = 0;
    protected int localTotalMedidos = 0;
    boolean continuaMedindoTempos = true;
    public int TOTAL_REQS_MEDIDOS_TEMPO_EXEC = 1000;
    long tempos [][] = new long[5][TOTAL_REQS_MEDIDOS_TEMPO_EXEC];
    int listSize;

    public SequentialServiceReplica(int id, Executable executor, Recoverable recoverer, int duration, int warmup, int listSize) {
        //this(id, executor, recoverer, new DefaultScheduler(initialWorkers));
        super(id, executor, recoverer);
        this.listSize=listSize;
        statistics = new ThroughputStatistics(id, 1, "results_seq_" + id + ".txt", "SEQ", duration, warmup);

    }

    private void generateFile(){
        if(id>0) return;
        System.out.println("Criando arquivo ...");
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File("seq_"+listSize+"_rep"+id+".txt")));

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

    @Override
    public void receiveMessages(int consId[], int regencies[], int leaders[], CertifiedDecision[] cDecs, TOMMessage[][] requests) {

        Iterator<String> it = ctxs.keySet().iterator();

        while (it.hasNext()) {
            String next = it.next();
            MultiOperationCtx cx = ctxs.get(next);

            if (cx.finished) {
                it.remove();
            }

        }

        //int numRequests = 0;
        int consensusCount = 0;
        boolean noop = true;

        long recMessageTime = System.nanoTime();

        for (TOMMessage[] requestsFromConsensus : requests) {
            TOMMessage firstRequest = requestsFromConsensus[0];
            //int requestCount = 0;
            noop = true;
            for (TOMMessage request : requestsFromConsensus) {

                bftsmart.tom.util.Logger.println("(ServiceReplica.receiveMessages) Processing TOMMessage from client " + request.getSender() + " with sequence number " + request.getSequence() + " for session " + request.getSession() + " decided in consensus " + consId[consensusCount]);

                if (request.getViewID() == SVController.getCurrentViewId()) {
                    if (request.getReqType() == TOMMessageType.ORDERED_REQUEST) {
                        noop = false;
                        //numRequests++;
                        /*MessageContext msgCtx = new MessageContext(request.getSender(), request.getViewID(),
                                request.getReqType(), request.getSession(), request.getSequence(), request.getOperationId(),
                                request.getReplyServer(), request.serializedMessageSignature, firstRequest.timestamp,
                                request.numOfNonces, request.seed, regencies[consensusCount], leaders[consensusCount],
                                consId[consensusCount], cDecs[consensusCount].getConsMessages(), firstRequest, false);

                        if (requestCount + 1 == requestsFromConsensus.length) {

                            msgCtx.setLastInBatch();
                        }
                        request.deliveryTime = System.nanoTime();
                         */

                        MultiOperationRequest reqs = new MultiOperationRequest(request.getContent());

                        MultiOperationCtx ctx = new MultiOperationCtx(reqs.operations.length, request);

                        //this.ctxs.put(request.toString(), ctx);
                        statistics.start();

                        for (int i = 0; i < reqs.operations.length; i++) {
                            MessageContextPair msg = new MessageContextPair(request, reqs.operations[i].classId, i, reqs.operations[i].data, firstRequest.decisionTime, recMessageTime);
                            
                            long iniExec = System.nanoTime();
                            msg.resp = ((SingleExecutable) executor).executeOrdered(msg.operation, null);
                            long endExec = System.nanoTime();

                            if(continuaMedindoTempos && localTotal > 10000){
                
                                // TIMES ARE CAPTURED IN THE FOLLOWING SEQUENCE:
                                // --- decision ---- recMsgs ---- scheduled ------ iniExec ------ endexec

                                // recMsg   = recMsgs - decision
                                // sched    = scheduled - recMsgs
                                // waitExec = iniExec - scheduled
                                // exec     = endExec - iniExec
                                // total    = endexec - decision
                                long tempoTotal = endExec - msg.decisionTime;
                                long tempoRecMsgs = msg.recMsgTime - msg.decisionTime;
                                long tempoSchedule = 0;
                                long tempoWaitForExec = 0;
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

                            ctx.add(msg.index, msg.resp);
                            statistics.computeStatistics(0, 1);

                            localTotal++;

                        }
                        ctx.request.reply = new TOMMessage(id, ctx.request.getSession(),
                                ctx.request.getSequence(), ctx.response.serialize(), SVController.getCurrentViewId());
                        //bftsmart.tom.util.Logger.println("(ParallelServiceReplica.receiveMessages) sending reply to "+ msg.message.getSender());
                        replier.manageReply(ctx.request, null);

                    } else if (request.getReqType() == TOMMessageType.RECONFIG) {

                        SVController.enqueueUpdate(request);
                    } else {
                        throw new RuntimeException("Should never reach here! ");
                    }

                } else if (request.getViewID() < SVController.getCurrentViewId()) {
                    // message sender had an old view, resend the message to
                    // him (but only if it came from consensus an not state transfer)
                    tomLayer.getCommunication().send(new int[]{request.getSender()}, new TOMMessage(SVController.getStaticConf().getProcessId(),
                            request.getSession(), request.getSequence(), TOMUtil.getBytes(SVController.getCurrentView()), SVController.getCurrentViewId()));

                }
                //requestCount++;
            }

            //System.out.println("BATCH SIZE: "+requestCount);
            // This happens when a consensus finishes but there are no requests to deliver
            // to the application. This can happen if a reconfiguration is issued and is the only
            // operation contained in the batch. The recoverer must be notified about this,
            // hence the invocation of "noop"
            if (noop && this.recoverer != null) {

                bftsmart.tom.util.Logger.println("(ServiceReplica.receiveMessages) Delivering a no-op to the recoverer");

                System.out.println(" --- A consensus instance finished, but there were no commands to deliver to the application.");
                System.out.println(" --- Notifying recoverable about a blank consensus.");

                byte[][] batch = null;
                MessageContext[] msgCtx = null;
                if (requestsFromConsensus.length > 0) {
                    //Make new batch to deliver
                    batch = new byte[requestsFromConsensus.length][];
                    msgCtx = new MessageContext[requestsFromConsensus.length];

                    //Put messages in the batch
                    int line = 0;
                    for (TOMMessage m : requestsFromConsensus) {
                        batch[line] = m.getContent();

                        msgCtx[line] = new MessageContext(m.getSender(), m.getViewID(),
                                m.getReqType(), m.getSession(), m.getSequence(), m.getOperationId(),
                                m.getReplyServer(), m.serializedMessageSignature, firstRequest.timestamp,
                                m.numOfNonces, m.seed, regencies[consensusCount], leaders[consensusCount],
                                consId[consensusCount], cDecs[consensusCount].getConsMessages(), firstRequest, true);
                        msgCtx[line].setLastInBatch();

                        line++;
                    }
                }

                this.recoverer.noOp(consId[consensusCount], batch, msgCtx);

                //MessageContext msgCtx = new MessageContext(-1, -1, null, -1, -1, -1, -1, null, // Since it is a noop, there is no need to pass info about the client...
                //        -1, 0, 0, regencies[consensusCount], leaders[consensusCount], consId[consensusCount], cDecs[consensusCount].getConsMessages(), //... but there is still need to pass info about the consensus
                //        null, true); // there is no command that is the first of the batch, since it is a noop
                //msgCtx.setLastInBatch();
                //this.recoverer.noOp(msgCtx.getConsensusId(), msgCtx);
            }

            consensusCount++;
        }
        if (SVController.hasUpdates()) {

            this.scheduler.scheduleReplicaReconfiguration();

        }
    }

    /**
     * This method initializes the object
     *
     * @param cs Server side communication System
     * @param conf Total order messaging configuration
     */
    /* private void initTOMLayer() {
        if (tomStackCreated) { // if this object was already initialized, don't do it again
            return;
        }

        if (!SVController.isInCurrentView()) {
            throw new RuntimeException("I'm not an acceptor!");
        }

        // Assemble the total order messaging layer
        MessageFactory messageFactory = new MessageFactory(id);

        Acceptor acceptor = new Acceptor(cs, messageFactory, SVController);
        cs.setAcceptor(acceptor);

        Proposer proposer = new Proposer(cs, messageFactory, SVController);

        ExecutionManager executionManager = new ExecutionManager(SVController, acceptor, proposer, id);

        acceptor.setExecutionManager(executionManager);

        tomLayer = new ParallelTOMLayer(executionManager, this, recoverer, acceptor, cs, SVController, verifier);

        executionManager.setTOMLayer(tomLayer);

        SVController.setTomLayer(tomLayer);

        cs.setTOMLayer(tomLayer);
        cs.setRequestReceiver(tomLayer);

        acceptor.setTOMLayer(tomLayer);

        if (SVController.getStaticConf().isShutdownHookEnabled()) {
            Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(tomLayer));
        }
        tomLayer.start(); // start the layer execution
        tomStackCreated = true;

        replicaCtx = new ReplicaContext(cs, SVController);
    }*/
  

}
