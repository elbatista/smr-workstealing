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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
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


public final class MapServerMP implements SingleExecutable {

    private ServiceReplica replica;
    private int numberpartitions = 2;

    private TreeMap<Integer, String> map1 = new TreeMap<>();
    private TreeMap<Integer, String> map2 = new TreeMap<>();
    private TreeMap<Integer, String> map3 = new TreeMap<>();
    private TreeMap<Integer, String> map4 = new TreeMap<>();
    private TreeMap<Integer, String> map5 = new TreeMap<>();
    private TreeMap<Integer, String> map6 = new TreeMap<>();
    private TreeMap<Integer, String> map7 = new TreeMap<>();
    private TreeMap<Integer, String> map8 = new TreeMap<>();


    public MapServerMP(
        int id, int initThreads, int entries, int numberPartitions, boolean cbase, boolean bwait, 
        boolean ws, boolean skewed, int stealSize, String stealerType, boolean distExpo, 
        int duration, int warmup) {

        this.numberpartitions = numberPartitions;

        if (initThreads <= 0) {
            
            System.out.println("Replica in sequential execution model.");
            replica = new SequentialServiceReplica(id, this, null, duration, warmup);

        }
        else if (cbase) {
            System.out.println("Replica in parallel late scheduling execution model (CBASE).");
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
            System.out.println("Replica in parallel execution model: " + initThreads+"T"+numberPartitions+"P");

            /*************************************************
                2 SHARDS 
            *************************************************/
            if (numberPartitions == 2) {
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


        // initializing maps
        System.out.println("Adding "+entries+" entries to the map...");
        for (int i = 0; i < entries; i++) {
            this.map1.put(i, "Value_"+i);
            this.map2.put(i, "Value_"+i);

            if (numberpartitions >= 4) {
                this.map3.put(i, "Value_"+i);
                this.map4.put(i, "Value_"+i);
            }

            if (numberpartitions >= 6) {
                this.map5.put(i, "Value_"+i);
                this.map6.put(i, "Value_"+i);
            }

            if (numberpartitions >= 8) {
                this.map7.put(i, "Value_"+i);
                this.map8.put(i, "Value_"+i);
            }
        }

        System.out.println("Server initialization complete!");

    }

    public byte[] executeOrdered(byte[] command, MessageContext msgCtx) {
        return execute(command, msgCtx);
    }

    public byte[] executeUnordered(byte[] command, MessageContext msgCtx) {
        return execute(command, msgCtx);
    }

    public boolean add(Integer key, String value, int pId) {
        boolean ret = false;
        if (pId == MultipartitionMapping.W1) {
            if (!map1.containsKey(key)) {
                map1.put(key, value);
                return true;
            }
            return false;
        }
        else if (pId == MultipartitionMapping.W2) {
            if (!map2.containsKey(key)) {
                map2.put(key, value);
                return true;
            }
            return false;
        }
        else if (pId == MultipartitionMapping.W3) {
            if (!map3.containsKey(key)) {
                map3.put(key, value);
                return true;
            }
            return false;
        }
        else if (pId == MultipartitionMapping.W4) {
            if (!map4.containsKey(key)) {
                map4.put(key, value);
                return true;
            }
            return false;
        } else if (pId == MultipartitionMapping.W5) {
            if (!map5.containsKey(key)) {
                map5.put(key, value);
                return true;
            }
            return false;
        } else if (pId == MultipartitionMapping.W6) {
            if (!map6.containsKey(key)) {
                map6.put(key, value);
                return true;
            }
            return false;
        } else if (pId == MultipartitionMapping.W7) {
            if (!map7.containsKey(key)) {
                map7.put(key, value);
                return true;
            }
            return false;
        } else if (pId == MultipartitionMapping.W8) {
            if (!map8.containsKey(key)) {
                map8.put(key, value);
                return true;
            }
            return false;
        } else if (pId == MultipartitionMapping.GW) {

            if (!map1.containsKey(key)) {
                map1.put(key, value);
                ret = true;
            }
            else {
                ret = false;
            }
            if (!map2.containsKey(key)) {
                map2.put(key, value);
                ret = true;
            }
            else {
                ret = false;
            }
            if (numberpartitions >= 4) {
                if (!map3.containsKey(key)) {
                    map3.put(key, value);
                    ret = true;
                }
                else {
                    ret = false;
                }
                if (!map4.containsKey(key)) {
                    map4.put(key, value);
                    ret = true;
                }
                else {
                    ret = false;
                }
                if (numberpartitions >= 6) {
                    if (!map5.containsKey(key)) {
                        map5.put(key, value);
                        ret = true;
                    }
                    else {
                        ret = false;
                    }
                    if (!map6.containsKey(key)) {
                        map6.put(key, value);
                        ret = true;
                    }
                    else {
                        ret = false;
                    }
                    if (numberpartitions >= 8) {
                        if (!map7.containsKey(key)) {
                            map7.put(key, value);
                            ret = true;
                        }
                        else {
                            ret = false;
                        }
                        if (!map8.containsKey(key)) {
                            map8.put(key, value);
                            ret = true;
                        }
                        else {
                            ret = false;
                        }
                    }
                }
            } 
            return ret;
        }
        return ret;
    }

    public byte[] execute(byte[] command, MessageContext msgCtx) {

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(command);
            ByteArrayOutputStream out = null;
            byte[] reply = null;
            int cmd = new DataInputStream(in).readInt();

            Integer key = new DataInputStream(in).readInt();
            String value = null;
            boolean ret = false;

            switch (cmd) {
                case MultipartitionMapping.W1:
                    value = (String) new ObjectInputStream(in).readObject();
                    ret = add(key, value, MultipartitionMapping.W1);
                    out = new ByteArrayOutputStream();
                    ObjectOutputStream out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.W2:
                    value = (String) new ObjectInputStream(in).readObject();
                    ret = add(key, value, MultipartitionMapping.W2);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.W3:
                    value = (String) new ObjectInputStream(in).readObject();
                    ret = add(key, value, MultipartitionMapping.W3);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.W4:
                    value = (String) new ObjectInputStream(in).readObject();
                    ret = add(key, value, MultipartitionMapping.W4);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.W5:
                    value = (String) new ObjectInputStream(in).readObject();
                    ret = add(key, value, MultipartitionMapping.W5);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.W6:
                    value = (String) new ObjectInputStream(in).readObject();
                    ret = add(key, value, MultipartitionMapping.W6);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.W7:
                    value = (String) new ObjectInputStream(in).readObject();
                    ret = add(key, value, MultipartitionMapping.W7);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.W8:
                    value = (String) new ObjectInputStream(in).readObject();
                    ret = add(key, value, MultipartitionMapping.W8);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.GW:
                    value = (String) new ObjectInputStream(in).readObject();
                    ret = add(key, value, MultipartitionMapping.GW);
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(ret);
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.R1:
                    //value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(map1.containsKey(key));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.R2:
                    //value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(map2.containsKey(key));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.R3:
                    //value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(map3.containsKey(key));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;
                case MultipartitionMapping.R4:
                    //value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(map4.containsKey(key));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.R5:
                    //value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(map5.containsKey(key));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.R6:
                    //value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(map6.containsKey(key));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.R7:
                    //value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(map7.containsKey(key));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.R8:
                    //value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    out1.writeBoolean(map8.containsKey(key));
                    out.flush();
                    out1.flush();
                    reply = out.toByteArray();
                    break;

                case MultipartitionMapping.GR:
                    //value = (Integer) new ObjectInputStream(in).readObject();
                    out = new ByteArrayOutputStream();
                    out1 = new ObjectOutputStream(out);
                    if (this.numberpartitions == 2) {
                        if (map1.containsKey(key) && map2.containsKey(key)) {
                            out1.writeBoolean(true);
                        } else {
                            out1.writeBoolean(false);
                        }
                    } else if (this.numberpartitions == 4) {
                        if (map1.containsKey(key) && map2.containsKey(key) && map3.containsKey(key) && map4.containsKey(key)) {
                            out1.writeBoolean(true);
                        } else {
                            out1.writeBoolean(false);
                        }
                    } else if (this.numberpartitions == 6) {
                        if (map1.containsKey(key) && map2.containsKey(key) && map3.containsKey(key) && map4.containsKey(key)
                                && map5.containsKey(key) && map6.containsKey(key)) {
                            out1.writeBoolean(true);
                        } else {
                            out1.writeBoolean(false);
                        }
                    } else {
                        if (map1.containsKey(key) && map2.containsKey(key) && map3.containsKey(key) && map4.containsKey(key)
                        && map5.containsKey(key) && map6.containsKey(key) && map7.containsKey(key) && map8.containsKey(key)) {
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
                "Usage: ... MapServerMP '<procId>-<numShards>-<numThreads>-"
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

        new MapServerMP(processId, initialNT, entries, part, cbase, bwait, ws, skewed, stealSize, stealerType, distExpo, duration, warmup);
    }

}
