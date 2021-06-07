/**
 * Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and
 * the authors indicated in the @author tags
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package demo.list;

import bftsmart.reconfiguration.ServerViewController;
import bftsmart.statemanagement.ApplicationState;
import bftsmart.statemanagement.StateManager;
import bftsmart.statemanagement.strategy.StandardStateManager;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ReplicaContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.leaderchange.CertifiedDecision;
import bftsmart.tom.server.Recoverable;
import bftsmart.tom.server.SingleExecutable;
import bftsmart.tom.server.defaultservices.DefaultApplicationState;
import bftsmart.tom.util.Storage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import parallelism.BusyWaitParallelServiceReplica;
import parallelism.late.CBASEServiceReplica;
import parallelism.late.ConflictDefinition;
import parallelism.MessageContextPair;
import parallelism.ParallelMapping;
import parallelism.ParallelServiceReplica;
import parallelism.SequentialServiceReplica;
import workstealing.WorkStealingServiceReplica;
import workstealing.WorkStealingServiceReplica_v1;
import workstealing.WorkStealingServiceReplica_v2;


public final class ListServer implements SingleExecutable {

    private List<Integer> l = new LinkedList<Integer>();

    
    public ListServer(
            int id, int initThreads, int entries, boolean late, String gType, boolean busyWait, boolean ws, int stealSize, String stealerType, boolean distExpo,
            int duration, int warmup, int wsversion
    ) {

        if (initThreads <= 0) {
            System.out.println("Replica in sequential execution model.");

            //new ServiceReplica(id, this, null);
            new SequentialServiceReplica(id, this, null, duration, warmup);
            
        } else if (late) {
            System.out.println("Replica in parallel execution model (late scheduling).");
            ConflictDefinition cd = new ConflictDefinition() {
                @Override
                public boolean isDependent(MessageContextPair r1, MessageContextPair r2) {
                    if(r1.classId == ParallelMapping.SYNC_ALL ||
                            r2.classId == ParallelMapping.SYNC_ALL){
                        return true;
                    }
                    return false;
                }
            };
            
            new CBASEServiceReplica(id, this, null, initThreads, cd, gType, duration, warmup);
        } else {
            
            if(ws){
                // wsversion:
                // 1 - steal on empty queues
                // 2 - phaser
                // 3 - non blocking
                // 4 - optimistic
                
                System.out.println("Replica in parallel execution model - work stealing v"+wsversion);
                
                if(wsversion == 1){
                    new WorkStealingServiceReplica_v1(id, this, null, initThreads, stealSize, "", distExpo, duration, warmup);
                } else if(wsversion == 2){
                    new WorkStealingServiceReplica_v2(id, this, null, initThreads, stealSize, "", distExpo, duration, warmup);
                }else if(wsversion == 3){
                    new WorkStealingServiceReplica(id, this, null, initThreads, stealSize, "smartStealer", distExpo, duration, warmup);
                } else {
                    new WorkStealingServiceReplica(id, this, null, initThreads, stealSize, "smartOptStealer", distExpo, duration, warmup);
                }
            }else {
                System.out.println("Replica in parallel execution model (early scheduling)");
                if(busyWait){
                    System.out.println("\n\nBUSY WAIT !!\n\n");
                    new BusyWaitParallelServiceReplica(id, this, null, initThreads, duration, warmup);
                } else {
                    new ParallelServiceReplica(id, this, null, initThreads, duration, warmup);
                }
            }
        }
        
        System.out.println("Single shard.");
        
        for (int i = 0; i < entries; i++) {
            l.add(i);
        }

        System.out.println("Server initialization complete!");
    }

    public byte[] executeOrdered(byte[] command, MessageContext msgCtx) {
        return execute(command, msgCtx);
    }

    public byte[] executeUnordered(byte[] command, MessageContext msgCtx) {
        return execute(command, msgCtx);
    }

    public byte[] execute(byte[] command, MessageContext msgCtx) {

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(command);
            ByteArrayOutputStream out = null;
            byte[] reply = null;
            int cmd = new DataInputStream(in).readInt();

            switch (cmd) {
                case BFTList.ADD:
                    Integer value = (Integer) new ObjectInputStream(in).readObject();
                    boolean ret = false;
                    if (!l.contains(value)) {
                        ret = l.add(value);
                    }
                    out = new ByteArrayOutputStream();
                    ObjectOutputStream out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case BFTList.REMOVE:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    ret = l.remove(value);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case BFTList.SIZE:
                    out = new ByteArrayOutputStream();
                    new DataOutputStream(out).writeInt(l.size());
                    reply = out.toByteArray();
                    break;
                case BFTList.CONTAINS:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(l.contains(value));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case BFTList.GET:
                    int index = new DataInputStream(in).readInt();
                    Integer r = null;
                    if (index > l.size()) {
                        r = new Integer(-1);
                    } else {
                        r = l.get(index);
                    }
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeObject(r);
                    reply = out.toByteArray();
                    break;
            }
            return reply;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ListServer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
   
    public static void main(String[] args) {

        System.out.println(args.length);
        
        try{
        
            String [] aargs = args[0].split("-");

            int processId = Integer.parseInt(aargs[0]);
            int initialNT = Integer.parseInt(aargs[1]);
            int entries = Integer.parseInt(aargs[2]);
            boolean late = Boolean.parseBoolean(aargs[3]);
            String gType = "coarseLock";
            boolean busyWait = Boolean.parseBoolean(aargs[4]);
            boolean ws = Boolean.parseBoolean(aargs[5]);
            String stealerType = "smartOptStealer";//aargs[6];
            int stealSize = 100;//Integer.parseInt(aargs[7]);
            boolean distExpo = true;//Boolean.parseBoolean(aargs[8]);
            int duration = Integer.parseInt(aargs[6]);
            int warmup = Integer.parseInt(aargs[7]);
            int wsversion = Integer.parseInt(aargs[8]);
            // 1 - steal on empty queues
            // 2 - semi blocking
            // 3 - barrier free
            // 4 - optimistic

            new ListServer(processId, initialNT, entries, late, gType, busyWait, ws, stealSize, stealerType, distExpo, duration, warmup, wsversion);
        }
        catch(Exception e){
            System.out.println(
                "Usage: ... ListServer '<procId>-<numThreads>-<initEntries>-<CBASE?>-"
                + "<busyWait?>-<workstealing?>-<duration>-<warmup>-<wsversion>'"
            );
            System.exit(-1);
        }
        
    }

}
