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
import java.util.logging.Logger;
import parallelism.BusyWaitParallelServiceReplica;
import parallelism.late.CBASEServiceReplica;
import parallelism.late.ConflictDefinition;
import parallelism.MessageContextPair;
import parallelism.ParallelMapping;
import parallelism.ParallelServiceReplica;
import parallelism.ParallelServiceReplica;
import parallelism.SequentialServiceReplica;
import workstealing.WorkStealingMultiPartitionMapping;
import workstealing.WorkStealingServiceReplica;


public final class ListServerMP implements SingleExecutable {

    //private int interval;
    //private float maxTp = -1;
    // private boolean context;
    private int iterations = 0;
    private long throughputMeasurementStartTime = System.currentTimeMillis();

    private long start = 0;

    private ServiceReplica replica;
    //private StateManager stateManager;
    //private ReplicaContext replicaContext;

    private List<Integer> l1 = new LinkedList<Integer>();
    private List<Integer> l2 = new LinkedList<Integer>();
    private List<Integer> l3 = new LinkedList<Integer>();
    private List<Integer> l4 = new LinkedList<Integer>();
    private List<Integer> l5 = new LinkedList<Integer>();
    private List<Integer> l6 = new LinkedList<Integer>();
    private List<Integer> l7 = new LinkedList<Integer>();
    private List<Integer> l8 = new LinkedList<Integer>();

    //private int myId;
    private PrintWriter pw;

    private boolean closed = false;

    //int exec = BFTList.ADD;
    int numberpartitions = 2;

    public ListServerMP(
            int id, int initThreads, int entries, int numberPartitions, boolean cbase, boolean bwait, 
            boolean ws, boolean skewed, int stealSize, String stealerType, boolean distExpo, 
            int duration, int warmup
    ) {

        this.numberpartitions = numberPartitions;

        if (initThreads <= 0) {
            System.out.println("Replica in sequential execution model.");

            replica = new SequentialServiceReplica(id, this, null, duration, warmup);
        } else if (cbase) {
            System.out.println("Replica in parallel execution model (CBASE).");
            ConflictDefinition cd = new ConflictDefinition() {
                @Override
                public boolean isDependent(MessageContextPair r1, MessageContextPair r2) {
                                 
                    switch (r1.classId) {
                        case MultipartitionMapping.GR:
                            if (r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.W1
                                    || r2.classId == MultipartitionMapping.W2
                                    || r2.classId == MultipartitionMapping.W3
                                    || r2.classId == MultipartitionMapping.W4
                                    || r2.classId == MultipartitionMapping.W5
                                    || r2.classId == MultipartitionMapping.W6
                                    || r2.classId == MultipartitionMapping.W7
                                    || r2.classId == MultipartitionMapping.W8) {
                                return true;
                            }   break;
                        case MultipartitionMapping.R1:
                            if (r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.W1) {
                                return true;
                            }   break;
                        case MultipartitionMapping.R2:
                            if (r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.W2) {
                                return true;
                            }   break;
                        case MultipartitionMapping.R3:
                            if (r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.W3) {
                                return true;
                            }   break;
                        case MultipartitionMapping.R4:
                            if (r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.W4) {
                                return true;
                            }   break;
                        case MultipartitionMapping.R5:
                            if (r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.W5) {
                                return true;
                            }   break;
                        case MultipartitionMapping.R6:
                            if (r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.W6) {
                                return true;
                            }   break;
                        case MultipartitionMapping.R7:
                            if (r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.W7) {
                                return true;
                            }   break;
                        case MultipartitionMapping.R8:
                            if (r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.W8) {
                                return true;
                            }   break;
                        case MultipartitionMapping.GW:
                            return true;
                        case MultipartitionMapping.W1:
                            if (r2.classId == MultipartitionMapping.GR
                                    || r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.R1
                                    || r2.classId == MultipartitionMapping.W1) {
                                return true;
                            }   break;
                        case MultipartitionMapping.W2:
                            if (r2.classId == MultipartitionMapping.GR
                                    || r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.R2
                                    || r2.classId == MultipartitionMapping.W2) {
                                return true;
                            }   break;
                        case MultipartitionMapping.W3:
                            if (r2.classId == MultipartitionMapping.GR
                                    || r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.R3
                                    || r2.classId == MultipartitionMapping.W3) {
                                return true;
                            }   break;
                        case MultipartitionMapping.W4:
                            if (r2.classId == MultipartitionMapping.GR
                                    || r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.R4
                                    || r2.classId == MultipartitionMapping.W4) {
                                return true;
                            }   break;
                        case MultipartitionMapping.W5:
                            if (r2.classId == MultipartitionMapping.GR
                                    || r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.R5
                                    || r2.classId == MultipartitionMapping.W5) {
                                return true;
                            }   break;
                        case MultipartitionMapping.W6:
                            if (r2.classId == MultipartitionMapping.GR
                                    || r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.R6
                                    || r2.classId == MultipartitionMapping.W6) {
                                return true;
                            }   break;
                        case MultipartitionMapping.W7:
                            if (r2.classId == MultipartitionMapping.GR
                                    || r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.R7
                                    || r2.classId == MultipartitionMapping.W7) {
                                return true;
                            }   break;
                        case MultipartitionMapping.W8:
                            if (r2.classId == MultipartitionMapping.GR
                                    || r2.classId == MultipartitionMapping.GW
                                    || r2.classId == MultipartitionMapping.R8
                                    || r2.classId == MultipartitionMapping.W8) {
                                return true;
                            }   break;
                        default:
                            break;
                    }

                    return false;
                }
            };
            replica = new CBASEServiceReplica(id, this, null, initThreads, cd, "coarseLock", duration, warmup);
        } else {
            System.out.println("Replica in parallel execution model.");
            System.out.println(initThreads+"T"+numberPartitions+"P");

            if (numberPartitions == 1) {
                
            /*************************************************
                2 SHARDS 
            *************************************************/
            } else if (numberPartitions == 2) {
                if (initThreads == 2) {
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSM2P2T2(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T2(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T2(), duration, warmup);
                        }
                    }
                } else if (initThreads == 4) {
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSM2P2T4(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T4(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T4(), duration, warmup);
                        }
                    }
                }  else if (initThreads == 6) {
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSM2P2T6(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T6(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T6(), duration, warmup);
                        }
                    }
                } else if (initThreads == 8) {
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSM2P2T8(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T8(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T8(), duration, warmup);
                        }
                    }
                } else if (initThreads == 10) {
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSP2T10(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getP2T10(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getP2T10(), duration, warmup);
                        }
                    }
                } else if (initThreads == 12) {
                    
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSM2P2T12(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T12(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T12(), duration, warmup);
                        }
                    }
                } else if (initThreads == 16) {
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSM2P2T16(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T16(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T16(), duration, warmup);
                        }
                    }
                } else if (initThreads == 32) {
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSM2P2T32(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T32(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T32(), duration, warmup);
                        }
                    }
                } else if (initThreads == 40) {
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSM2P2T40(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T40(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T40(), duration, warmup);
                        }
                    }
                } else if (initThreads == 48) {
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSM2P2T48(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T48(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T48(), duration, warmup);
                        }
                    }
                } else if (initThreads == 56) {
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSM2P2T56(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T56(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P2T56(), duration, warmup);
                        }
                    }
                }
                
            /*************************************************
                4 SHARDS 
            *************************************************/
            } else if (numberPartitions == 4) {
                if (initThreads == 2) {
                    replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P4T2(), duration, warmup);
                } else if (initThreads == 4) {
                    replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P4T4(), duration, warmup);
                } else if (initThreads == 8) {
                    //System.out.println("Naive 8T-4S");
                    
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSM2P4T8(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P4T8(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P4T8(), duration, warmup);
                        }
                    }
                    
                    //replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getNaiveP4T8());
                }else if (initThreads == 10) {
                    replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getP4T10(), duration, warmup);
                } else if (initThreads == 12){
                    //initThreads = 12;
                    replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P4T12(), duration, warmup);
                } else if (initThreads == 16){
                    
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSM2P4T16(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P4T16(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P4T16(), duration, warmup);
                        }
                    }
                    
                } else if (initThreads == 32){
                                        
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSM2P4T32(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P4T32(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P4T32(), duration, warmup);
                        }
                    }
                    
                } else if (initThreads == 40){
                                        
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSM2P4T40(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P4T40(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getM2P4T40(), duration, warmup);
                        }
                    }
                    
                }
                
            /*************************************************
                6 SHARDS 
            *************************************************/
            } else if (numberPartitions == 6) {
                if (initThreads == 6) {
                    replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getP6T6(), duration, warmup);
                } else if (initThreads == 12) {
                    if(ws){
                        replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSP6T12(), stealSize, stealerType, distExpo, duration, warmup);
                    } else {
                        if(bwait){
                            replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getP6T12(), duration, warmup);
                        } else {
                            replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getP6T12(), duration, warmup);
                        }
                    }
                } else if (initThreads == 10) {
                    replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getP6T10(), duration, warmup);
                }else {
                    System.exit(0);
                }
                
            /*************************************************
                8 SHARDS 
            *************************************************/
            } else if (initThreads == 8) {
                replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getP8T8(), duration, warmup);
            } else if (initThreads == 10) {
                replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getP8T10(), duration, warmup);
            } else if (initThreads == 16) {
                if(ws){
                    replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSP8T16(), stealSize, stealerType, distExpo, duration, warmup);
                } else {
                    if(bwait){
                        replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getP8T16(), duration, warmup);
                    } else {
                        replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getP8T16(), duration, warmup);
                    }
                }
            } else if (initThreads == 32) {
                
                if(ws){
                    replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSP8T32(), stealSize, stealerType, distExpo, duration, warmup);
                } else {
                    if(bwait){
                        replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getP8T32(), duration, warmup);
                    } else {
                        replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getP8T32(), duration, warmup);
                    }
                }
            }  else if (initThreads == 40) {
                
                if(ws){
                    replica = new WorkStealingServiceReplica(id, this, null, initThreads, WorkStealingMultiPartitionMapping.getWSP8T40(), stealSize, stealerType, distExpo, duration, warmup);
                } else {
                    if(bwait){
                        replica = new BusyWaitParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getP8T40(), duration, warmup);
                    } else {
                        replica = new ParallelServiceReplica(id, this, null, initThreads, MultipartitionMapping.getP8T40(), duration, warmup);
                    }
                }
            } else {
                System.exit(0);
            }

        }

//        this.interval = interval;
        //this.context = context;
        //this.myId = id;
        for (int i = 0; i < entries; i++) {
            l1.add(i);
            l2.add(i);
            l3.add(i);
            l4.add(i);
            l5.add(i);
            l6.add(i);
            l7.add(i);
            l8.add(i);
            //System.out.println("adicionando key: "+i);
        }

        /*try {
            File f = new File("resultado_" + id + ".txt");
            FileWriter fw = new FileWriter(f);
            pw = new PrintWriter(fw);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }*/
        System.out.println("Server initialization complete!");
        System.out.println(initThreads+"T"+numberPartitions+"P");
    }

    public byte[] executeOrdered(byte[] command, MessageContext msgCtx) {
        return execute(command, msgCtx);
    }

    public byte[] executeUnordered(byte[] command, MessageContext msgCtx) {
        return execute(command, msgCtx);
    }

    //long lastChange = 0;
    //int c = 0;
    public boolean add(Integer value, int pId) {
        boolean ret = false;
        if (pId == MultipartitionMapping.W1) {
            if (!l1.contains(value)) {
                ret = l1.add(value);
            }
            return ret;

        } else if (pId == MultipartitionMapping.W2) {
            if (!l2.contains(value)) {
                ret = l2.add(value);
            }
            return ret;
        } else if (pId == MultipartitionMapping.W3) {
            if (!l3.contains(value)) {
                ret = l3.add(value);
            }
            return ret;

        } else if (pId == MultipartitionMapping.W4) {
            if (!l4.contains(value)) {
                ret = l4.add(value);
            }
            return ret;

        } else if (pId == MultipartitionMapping.W5) {
            if (!l5.contains(value)) {
                ret = l5.add(value);
            }
            return ret;
        } else if (pId == MultipartitionMapping.W6) {
            if (!l6.contains(value)) {
                ret = l6.add(value);
            }
            return ret;
        } else if (pId == MultipartitionMapping.W7) {
            if (!l7.contains(value)) {
                ret = l7.add(value);
            }
            return ret;
        } else if (pId == MultipartitionMapping.W8) {
            if (!l8.contains(value)) {
                ret = l8.add(value);
            }
            return ret;
        } else if (pId == MultipartitionMapping.GW) {

            if (!l1.contains(value)) {
                ret = l1.add(value);
            }
            if (!l2.contains(value)) {
                ret = l2.add(value);
            }
            if (numberpartitions >= 4) {
                if (!l3.contains(value)) {
                    ret = l3.add(value);
                }
                if (!l4.contains(value)) {
                    ret = l4.add(value);
                }

                if (numberpartitions >= 6) {

                    if (!l5.contains(value)) {
                        ret = l5.add(value);
                    }
                    if (!l6.contains(value)) {
                        ret = l6.add(value);
                    }

                    if (numberpartitions >= 8) {

                        if (!l7.contains(value)) {
                            ret = l7.add(value);
                        }
                        if (!l8.contains(value)) {
                            ret = l8.add(value);
                        }
                    }
                }
            }

            return ret;

        }
        return ret;
    }

    public byte[] execute(byte[] command, MessageContext msgCtx) {

        //System.out.println("Vai executar uma operação");
        /*boolean set = false;
        if (start == 0) {
            set = true;
            start = System.currentTimeMillis();
            throughputMeasurementStartTime = start;
        }*/
        //computeStatistics(msgCtx);
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(command);
            ByteArrayOutputStream out = null;
            byte[] reply = null;
            int cmd = new DataInputStream(in).readInt();

            /*if (set) {
                exec = cmd;
            }

            if (cmd != exec && (System.currentTimeMillis() - lastChange) > 30000) {

                if (cmd == BFTList.ADD) {
                    pw.println("***************** Vai mudar para ADD ***************");
                } else if (cmd == BFTList.CONTAINS) {
                    pw.println("***************** Vai mudar para CONTAINS ***************");
                }
                exec = cmd;
                lastChange = System.currentTimeMillis();
            }*/
            switch (cmd) {
                case MultipartitionMapping.W1:
                    Integer value = (Integer) new ObjectInputStream(in).readObject();
                    boolean ret = add(value, MultipartitionMapping.W1);
                    out = new ByteArrayOutputStream();
                    ObjectOutputStream out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.W2:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    ret = add(value, MultipartitionMapping.W2);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.W3:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    ret = add(value, MultipartitionMapping.W3);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.W4:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    ret = add(value, MultipartitionMapping.W4);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.W5:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    ret = add(value, MultipartitionMapping.W5);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.W6:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    ret = add(value, MultipartitionMapping.W6);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.W7:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    ret = add(value, MultipartitionMapping.W7);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.W8:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    ret = add(value, MultipartitionMapping.W8);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.GW:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    ret = add(value, MultipartitionMapping.GW);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.R1:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(l1.contains(value));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.R2:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(l2.contains(value));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.R3:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(l3.contains(value));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.R4:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(l4.contains(value));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.R5:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(l5.contains(value));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.R6:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(l6.contains(value));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.R7:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(l7.contains(value));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.R8:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(l8.contains(value));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.GR:
                    value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    if (this.numberpartitions == 2) {
                        if (l1.contains(value) && l2.contains(value)) {
                            out1.writeBoolean(true);
                        } else {
                            out1.writeBoolean(false);
                        }
                    } else if (this.numberpartitions == 4) {
                        if (l1.contains(value) && l2.contains(value) && l3.contains(value) && l4.contains(value)) {
                            out1.writeBoolean(true);
                        } else {
                            out1.writeBoolean(false);
                        }

                    } else if (this.numberpartitions == 6) {
                        if (l1.contains(value) && l2.contains(value) && l3.contains(value) && l4.contains(value) 
                                && l5.contains(value) && l6.contains(value)) {
                            out1.writeBoolean(true);
                        } else {
                            out1.writeBoolean(false);
                        }
                    } else { // 8 partitions 
                        if (l1.contains(value) && l2.contains(value) && l3.contains(value) && l4.contains(value) 
                                && l5.contains(value) && l6.contains(value) && l7.contains(value) && l8.contains(value)) {
                            out1.writeBoolean(true);
                        } else {
                            out1.writeBoolean(false);
                        }
                    }
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
            }
            return reply;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ListServerMP.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println(
                "Usage: ... ListServerMP '<procId>-<numShards>-<numThreads>-"
                + "<initEntries>-<CBASE?>-<busyWait?>-<workstealing?>-"
                + "<skewed?>-<stealerType>-<stealSize>-<distExpo?>-<duration>-<warmup>'"
            );
            System.exit(-1);
        }
        
        String [] aargs = args[0].split("-");

        int processId = Integer.parseInt(aargs[0]);
        int part = Integer.parseInt(aargs[1]);
        int initialNT = Integer.parseInt(aargs[2]);
        int entries = Integer.parseInt(aargs[3]);
        boolean cbase = Boolean.parseBoolean(aargs[4]);
        boolean bwait = Boolean.parseBoolean(aargs[5]);
        boolean ws = Boolean.parseBoolean(aargs[6]);
        boolean skewed = Boolean.parseBoolean(aargs[7]);
        String stealerType = aargs[8];
        int stealSize = Integer.parseInt(aargs[9]);
        boolean distExpo = Boolean.parseBoolean(aargs[10]);
        int duration = Integer.parseInt(aargs[11]);
        int warmup = Integer.parseInt(aargs[12]);

        new ListServerMP(processId, initialNT, entries, part, cbase, bwait, ws, skewed, stealSize, stealerType, distExpo, duration, warmup);
    }

}
