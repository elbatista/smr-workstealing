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

//import bftsmart.tom.parallelism.ParallelMapping;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

//import bftsmart.tom.ServiceProxy;
//import bftsmart.tom.core.messages.TOMMessageType;
import bftsmart.tom.util.Storage;
import bftsmart.util.ExtStorage;
import bftsmart.util.RealDistExponential;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Example client
 *
 */
public class ListClientMOMP {

    public static int initId = 0;

    public static int op = BFTList.ADD, writePercent = 15, globalPercent = 5;
    public static boolean mix = true, skewed=false;

    public static boolean weight = false;

    public static boolean stop = false, distExpo = false;
    static long cmds=0, sync=0;
    
    @SuppressWarnings("static-access")
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println(
                "Usage: ... ListClientMOMP '<num. threads>-<process id>-<maxIndex>-" +
                "<partitions>-<skewed?>-<writePercent>-<globalPercent>-<distrExpo ?>-<numClients>'");
            System.exit(-1);
        }
        
        String [] aargs = args[0].split("-");

        int numThreads = Integer.parseInt(aargs[0]);
        initId = Integer.parseInt(aargs[1]);

        /*if (initId == 7001 || initId == 1001 || initId == 2001) {

            op = BFTList.ADD;
        }*/

        int numberOfReqs = 1000000;//Integer.parseInt(args[2]);
        //int requestSize = Integer.parseInt(args[3]);
        int interval = 0;//Integer.parseInt(args[2]);
        int max = Integer.parseInt(aargs[2]);
        boolean parallel = true;//Boolean.parseBoolean(args[4]);
        int numberOfOps = 50;//Integer.parseInt(args[5]);
        int p = Integer.parseInt(aargs[3]);
        skewed = Boolean.parseBoolean(aargs[4]);
        writePercent = Integer.parseInt(aargs[5]);
        globalPercent = Integer.parseInt(aargs[6]);
        distExpo = Boolean.parseBoolean(aargs[7]);
        String algo = aargs[9];

        Client[] c = new Client[numThreads];

        for (int i = 0; i < numThreads; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(ListClientMOMP.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("Launching client " + (initId + i));
            c[i] = new ListClientMOMP.Client(initId + i, numberOfReqs, numberOfOps, interval, max, p, parallel, algo);
            //c[i].start();
        }

        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            Logger.getLogger(ListClientMOMP.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < numThreads; i++) {

            c[i].start();
        }
        
            

//        (new Timer()).scheduleAtFixedRate(new TimerTask() {
//            public void run() {
//                //change();
//            }
//        }, 60000, 60000); //a cada 1 minuto
//
//        (new Timer()).schedule(new TimerTask() {
//            public void run() {
//                stop();
//            }
//        }, 5 * 60000); //depois de 5 minutos

        for (int i = 0; i < numThreads; i++) {

            try {
                c[i].join();
            } catch (InterruptedException ex) {
                ex.printStackTrace(System.err);
            }
        }

        //System.exit(0);
    }

    public static void stop() {
        stop = true;
    }

    public static void change() {
        if (op == BFTList.CONTAINS) {
            op = BFTList.ADD;
        } else {
            op = BFTList.CONTAINS;
        }
    }

    static class Client extends Thread {

        int id;
        int numberOfReqs;
        int interval;

        int countNumOp = 0;

        //boolean verbose;
        //boolean dos;
        //ServiceProxy proxy;
        //byte[] request;
        BFTListMOMP<Integer> store;

        int maxIndex;
        //int percent;

        int opPerReq = 1;

        int partitions = 2;
        ExtStorage sR = new ExtStorage();
        ExtStorage sW = new ExtStorage();
        ExtStorage sGR = new ExtStorage();
        ExtStorage sGW = new ExtStorage();

        public Client(int id, int numberOfRqs, int opPerReq, int interval, int maxIndex, int partitions, boolean parallel, String algo) {
            super("Client " + id);

            this.id = id;
            this.numberOfReqs = numberOfRqs;
            this.opPerReq = opPerReq;

            this.interval = interval;

            this.partitions = partitions;

            //this.verbose = false;
            //this.proxy = new ServiceProxy(id);
            //this.request = new byte[this.requestSize];
            this.maxIndex = maxIndex;

            store = new BFTListMOMP<Integer>(id, parallel);
            //this.dos = dos;
            try {
                Files.delete(Paths.get("latency_"+algo+"_"+initId+".txt"));
            } catch (IOException ex) {
                //Logger.getLogger(ListClientMOMP.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (id == initId) {
                (new Timer()).scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        
                        System.out.println("Clients syncs: "+sync +" / " + cmds +" = "+((100 * sync) / (cmds > 0 ? cmds : 1))+"%");

                        try {
                            Files.write(
                                Paths.get("latency_"+algo+"_"+initId+".txt"),
                                (
                                    (sR.getAverage(true) / 1000) + "\t" + (sR.getDP(true) / 1000) + "\t" + 
                                    (sW.getAverage(true) / 1000) + "\t" + (sW.getDP(true) / 1000) + "\t" + 
                                    (sGR.getAverage(true) / 1000) + "\t" + (sGR.getDP(true) / 1000) + "\t" + 
                                    (sGW.getAverage(true) / 1000) + "\t" + (sGW.getDP(true) / 1000) + "\r\n"
                                ).getBytes(StandardCharsets.UTF_8),
                                StandardOpenOption.CREATE,StandardOpenOption.APPEND
                            );
                        } catch (IOException ex) {
                            //Logger.getLogger(ListClientMOMP.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }, 10000, 3000);
            }
            
        }

        /*  private boolean insertValue(int index) {

            return store.add(index);

        }*/
        
        public void runDistExp(){
            int p = 0;

            System.out.println((skewed?"Skewed e":"E")+"xperiment (expo. distr.) for " + partitions + " shards; "+writePercent+"% writes; "+globalPercent+"% global");

            Random rand = new Random();
            Random randGlobal = new Random();
            Random indexRand = new Random();
            
            RealDistExponential distExpoRandom = new RealDistExponential((100-writePercent)/writePercent);

            for (int i = 0; i < numberOfReqs && !stop; i++) {
                if (i == 1) {
                    try {Thread.currentThread().sleep(2000);} catch (InterruptedException ex) {}
                }

                int g = randGlobal.nextInt(100);
                double randDistExpo = distExpoRandom.sample();
                
                if (g < globalPercent) {
                    
                    //vai ser um gw para n gr
                    
                    Integer[] reqs = new Integer[opPerReq];
                    for (int x = 0; x < reqs.length; x++) {
                        reqs[x] = indexRand.nextInt(maxIndex);
                    }
                    
                    long last_send_instant = System.nanoTime();
                    store.addAll(reqs);
                    sGW.store(System.nanoTime() - last_send_instant);
                    
                    if (id == initId){
                        sync++;
                        cmds++;
                    }

                    i++;

                    for(int j=0; j < randDistExpo; j++){
                        Integer[] reqsR = new Integer[opPerReq];
                        for (int x = 0; x < reqsR.length; x++) {
                            reqsR[x] = indexRand.nextInt(maxIndex);
                        }
                        
                        last_send_instant = System.nanoTime();
                        store.containsAll(reqsR);
                        sGR.store(System.nanoTime() - last_send_instant);
                        
                        if (id == initId){
                            cmds++;
                        }
                        i++;
                    }
                    
                    
                } else{//local
                    
                    
                    //vai ser um add para n contains
                    
                    //primeiro calcula a partição:
                    if (this.partitions == 2) {//2 partitions
                        int r = rand.nextInt(100);

                        if(skewed){
                            if (r < 95) {
                                p = 1;
                            } else {
                                p = 2;
                            }
                        }else {
                            if (r < 50) {
                                p = 1;
                            } else {
                                p = 2;
                            }
                        }
                    } else if (this.partitions == 4) {//4 partitions
                        int r = rand.nextInt(100);
                        if(skewed){
                            if (r < 70) {
                                p = 1;
                            } else if (r < 80) {
                                p = 2;
                            } else if (r < 90) {
                                p = 3;
                            } else {
                                p = 4;
                            }
                        } else {
                            if (r < 25) {
                                p = 1;
                            } else if (r < 50) {
                                p = 2;
                            } else if (r < 75) {
                                p = 3;
                            } else {
                                p = 4;
                            }
                        }
                    } else if (this.partitions == 6) {//6 partitions
                        int r = rand.nextInt(60);
                        if (r < 10) {
                            p = 1;
                        } else if (r < 20) {
                            p = 2;
                        } else if (r < 30) {
                            p = 3;
                        } else if (r < 40) {
                            p = 4;
                        } else if (r < 50) {
                            p = 5;
                        } else {
                            p = 6;
                        }
                    } else {//8 partitions
                        int r = rand.nextInt(80);

                        if(skewed){
                            r = rand.nextInt(100);
                            if (r < 65) {
                                p = 1;
                            } else if (r < 70) {
                                p = 2;
                            } else if (r < 75) {
                                p = 3;
                            } else if (r < 80) {
                                p = 4;
                            } else if (r < 85) {
                                p = 5;
                            } else if (r < 90) {
                                p = 6;
                            } else if (r < 95) {
                                p = 7;
                            } else {
                                p = 8;
                            }
                        } else{

                            if (r < 10) {
                                p = 1;
                            } else if (r < 20) {
                                p = 2;
                            } else if (r < 30) {
                                p = 3;
                            } else if (r < 40) {
                                p = 4;
                            } else if (r < 50) {
                                p = 5;
                            } else if (r < 60) {
                                p = 6;
                            } else if (r < 70) {
                                p = 7;
                            } else {
                                p = 8;
                            }
                        }
                    }
                    
                    
                    Integer[] reqs = new Integer[opPerReq];
                    for (int x = 0; x < reqs.length; x++) {
                        reqs[x] = indexRand.nextInt(maxIndex);
                    }
                    long last_send_instant = System.nanoTime();
                    if (p == 1) {
                        store.addP1(reqs);
                    } else if (p == 2) {
                        store.addP2(reqs);
                    } else if (p == 3) {
                        store.addP3(reqs);
                    } else if (p == 4) {
                        store.addP4(reqs);
                    } else if (p == 5) {
                        store.addP5(reqs);
                    } else if (p == 6) {
                        store.addP6(reqs);
                    } else if (p == 7) {
                        store.addP7(reqs);
                    } else {
                        store.addP8(reqs);
                    }
                    sW.store(System.nanoTime() - last_send_instant);
                    
                    if (id == initId){
                        sync++;
                        cmds++;
                    }
                    i++;
                    
                    for(int j=0; j < randDistExpo; j++){
                        Integer[] reqsR = new Integer[opPerReq];
                        for (int x = 0; x < reqsR.length; x++) {
                            reqsR[x] = indexRand.nextInt(maxIndex);
                        }
                        last_send_instant = System.nanoTime();
                        if (p == 1) {
                            store.containsP1(reqsR);
                        } else if (p == 2) {
                            store.containsP2(reqsR);
                        } else if (p == 3) {
                            store.containsP3(reqsR);
                        } else if (p == 4) {
                            store.containsP4(reqsR);
                        } else if (p == 5) {
                            store.containsP5(reqsR);
                        } else if (p == 6) {
                            store.containsP6(reqsR);
                        } else if (p == 7) {
                            store.containsP7(reqsR);
                        } else {
                            store.containsP8(reqsR);
                        }
                        sR.store(System.nanoTime() - last_send_instant);
                        if (id == initId){
                            cmds++;
                        }
                        i++;
                    }
                    
                }

                if (interval > 0 && i % 50 == 100) try {Thread.sleep(interval);} catch (InterruptedException ex) {}
            }
        }
        
        public void run() {

            //System.out.println("Warm up...");
            //int req = 0;
            if (weight) {
                weighting();
            } else if(distExpo){
                runDistExp();
            } else {
                int p = 0;
                
                

                System.out.println((skewed?"Skewed e":"E")+"xperiment for " + partitions + " shards; "+writePercent+"% writes; "+globalPercent+"% global");

                Random rand = new Random();

                Random randOp = new Random();

                Random randGlobal = new Random();

                Random indexRand = new Random();

//            WorkloadGenerator work = new WorkloadGenerator(numberOfOps);
                for (int i = 0; i < numberOfReqs && !stop; i++) {
                    if (i == 1) {
                        try {
                            //Thread.currentThread().sleep(20000);
                            Thread.currentThread().sleep(2000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ListClientMOMP.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                   
                    if (mix) {
                        int g = randGlobal.nextInt(100);
                        int r = randOp.nextInt(100);
                        if (g < globalPercent) {
                            //p = -2;
                            if (r < writePercent) {
                                //GW
                                p = -1;
                            } else {
                                //GR;
                                p = -2;
                            }
                        } else{//local
                            op = BFTList.CONTAINS;
                            if (r < writePercent) {
                                op = BFTList.ADD;
                            } else {
                                op = BFTList.CONTAINS;
                            }
                        }
                    }

                    if (p == 0) {
                        if (this.partitions == 2) {//2 partitions
                            int r = rand.nextInt(100);
                            
                            if(skewed){
                                if (r < 95) {
                                    p = 1;
                                } else {
                                    p = 2;
                                }
                            }else {
                                if (r < 50) {
                                    p = 1;
                                } else {
                                    p = 2;
                                }
                            }
                            /*if(op == BFTList.CONTAINS){
                                if (r < 67) {
                                    p = 1;
                                } else {
                                    p = 2;
                                }
                            }else{
                                if (r < 33) {
                                    p = 1;
                                } else {
                                    p = 2;
                                }
                            }*/
                        } else if (this.partitions == 4) {//4 partitions
                            int r = rand.nextInt(100);
                            if(skewed){
                                if (r < 70) {
                                    p = 1;
                                } else if (r < 80) {
                                    p = 2;
                                } else if (r < 90) {
                                    p = 3;
                                } else {
                                    p = 4;
                                }
                            } else {
                                if (r < 25) {
                                    p = 1;
                                } else if (r < 50) {
                                    p = 2;
                                } else if (r < 75) {
                                    p = 3;
                                } else {
                                    p = 4;
                                }
                            }
                        } else if (this.partitions == 6) {//6 partitions
                            int r = rand.nextInt(60);
                            if (r < 10) {
                                p = 1;
                            } else if (r < 20) {
                                p = 2;
                            } else if (r < 30) {
                                p = 3;
                            } else if (r < 40) {
                                p = 4;
                            } else if (r < 50) {
                                p = 5;
                            } else {
                                p = 6;
                            }
                        } else {//8 partitions
                            int r = rand.nextInt(80);
                            
                            if(skewed){
                                r = rand.nextInt(100);
                                if (r < 65) {
                                    p = 1;
                                } else if (r < 70) {
                                    p = 2;
                                } else if (r < 75) {
                                    p = 3;
                                } else if (r < 80) {
                                    p = 4;
                                } else if (r < 85) {
                                    p = 5;
                                } else if (r < 90) {
                                    p = 6;
                                } else if (r < 95) {
                                    p = 7;
                                } else {
                                    p = 8;
                                }
                            } else{
                            
                                if (r < 10) {
                                    p = 1;
                                } else if (r < 20) {
                                    p = 2;
                                } else if (r < 30) {
                                    p = 3;
                                } else if (r < 40) {
                                    p = 4;
                                } else if (r < 50) {
                                    p = 5;
                                } else if (r < 60) {
                                    p = 6;
                                } else if (r < 70) {
                                    p = 7;
                                } else {
                                    p = 8;
                                }
                            }
                        }
                    }

                    /*if(op == BFTList.ADD){
                      p = 2;
                  }else{
                      p = 1;
                  }*/
                    if (p == -1) { //GW
                        //int index = maxIndex - 1;

                        Integer[] reqs = new Integer[opPerReq];
                        for (int x = 0; x < reqs.length; x++) {
                            //reqs[x] = index;
                            reqs[x] = indexRand.nextInt(maxIndex);
                        }
                        long last_send_instant = System.nanoTime();
                        store.addAll(reqs);
                        if (id == initId) sync++;
                        sGW.store(System.nanoTime() - last_send_instant);
                    } else if (p == -2) {//GR
                        //int index = maxIndex - 1;

                        Integer[] reqs = new Integer[opPerReq];
                        for (int x = 0; x < reqs.length; x++) {
                            //reqs[x] = index;
                            reqs[x] = indexRand.nextInt(maxIndex);
                        }
                        long last_send_instant = System.nanoTime();
                        store.containsAll(reqs);
                        sGR.store(System.nanoTime() - last_send_instant);
                    } else if (op == BFTList.ADD) {

                        //int index = rand.nextInt(maxIndex);
                        //int index = maxIndex - 1;
                        Integer[] reqs = new Integer[opPerReq];
                        for (int x = 0; x < reqs.length; x++) {
                            //  reqs[x] = index;
                            reqs[x] = indexRand.nextInt(maxIndex);
                        }

                        long last_send_instant = System.nanoTime();
                        if (p == 1) {
                            store.addP1(reqs);
                        } else if (p == 2) {
                            store.addP2(reqs);
                        } else if (p == 3) {
                            store.addP3(reqs);
                        } else if (p == 4) {
                            store.addP4(reqs);
                        } else if (p == 5) {
                            store.addP5(reqs);
                        } else if (p == 6) {
                            store.addP6(reqs);
                        } else if (p == 7) {
                            store.addP7(reqs);
                        } else {
                            store.addP8(reqs);
                        }
                        if (id == initId) sync++;

                        sW.store(System.nanoTime() - last_send_instant);
                    } else if (op == BFTList.CONTAINS) {

                        //int index = rand.nextInt(maxIndex);
                        //int index = maxIndex - 1;
                        Integer[] reqs = new Integer[opPerReq];
                        for (int x = 0; x < reqs.length; x++) {
                            //reqs[x] = index;
                            reqs[x] = indexRand.nextInt(maxIndex);
                        }

                        long last_send_instant = System.nanoTime();
                        if (p == 1) {
                            store.containsP1(reqs);
                        } else if (p == 2) {
                            store.containsP2(reqs);
                        } else if (p == 3) {
                            store.containsP3(reqs);
                        } else if (p == 4) {
                            store.containsP4(reqs);
                        } else if (p == 5) {
                            store.containsP5(reqs);
                        } else if (p == 6) {
                            store.containsP6(reqs);
                        } else if (p == 7) {
                            store.containsP7(reqs);
                        } else {
                            store.containsP8(reqs);
                        }
                        
                        sR.store(System.nanoTime() - last_send_instant);

                    }

                    if (interval > 0 && i % 50 == 100) {
                        try {
                            Thread.sleep(interval);
                        } catch (InterruptedException ex) {
                        }
                    }
                    
                    if (id == initId) cmds++;

                    /* if (verbose && (req % 1000 == 0)) {
                    System.out.println(this.id + " // " + req + " operations sent!");
                }*/
                }

                if (id == initId) {
                    System.out.println(this.id + " //READ Average time for " + numberOfReqs + " executions (-10%) = " + sR.getAverage(true) / 1000 + " us ");
                    System.out.println(this.id + " //READ Standard desviation for " + numberOfReqs + " executions (-10%) = " + sR.getDP(true) / 1000 + " us ");
                    System.out.println(this.id + " // READ 90th percentile for " + numberOfReqs + " executions = " + sR.getPercentile(90) / 1000 + " us ");
                    System.out.println(this.id + " // READ 95th percentile for " + numberOfReqs + " executions = " + sR.getPercentile(95) / 1000 + " us ");
                    System.out.println(this.id + " // READ 99th percentile for " + numberOfReqs + " executions = " + sR.getPercentile(99) / 1000 + " us ");

                    System.out.println(this.id + " //WRITE Average time for " + numberOfReqs + " executions (-10%) = " + sW.getAverage(true) / 1000 + " us ");
                    System.out.println(this.id + " //WRITE Standard desviation for " + numberOfReqs + " executions (-10%) = " + sW.getDP(true) / 1000 + " us ");
                    System.out.println(this.id + " // WRITE 90th percentile for " + numberOfReqs + " executions = " + sW.getPercentile(90) / 1000 + " us ");
                    System.out.println(this.id + " // WRITE 95th percentile for " + numberOfReqs + " executions = " + sW.getPercentile(95) / 1000 + " us ");
                    System.out.println(this.id + " // WRITE 99th percentile for " + numberOfReqs + " executions = " + sW.getPercentile(99) / 1000 + " us ");

                    System.out.println(this.id + " //GLOBAL READ Average time for " + numberOfReqs + " executions (-10%) = " + sGR.getAverage(true) / 1000 + " us ");
                    System.out.println(this.id + " //GLOBAL READ Standard desviation for " + numberOfReqs + " executions (-10%) = " + sGR.getDP(true) / 1000 + " us ");
                    System.out.println(this.id + " //GLOBAL READ 90th percentile for " + numberOfReqs + " executions = " + sGR.getPercentile(90) / 1000 + " us ");
                    System.out.println(this.id + " //GLOBAL READ 95th percentile for " + numberOfReqs + " executions = " + sGR.getPercentile(95) / 1000 + " us ");
                    System.out.println(this.id + " //GLOBAL READ 99th percentile for " + numberOfReqs + " executions = " + sGR.getPercentile(99) / 1000 + " us ");

                    System.out.println(this.id + " //GLOBAL WRITE Average time for " + numberOfReqs + " executions (-10%) = " + sGW.getAverage(true) / 1000 + " us ");
                    System.out.println(this.id + " //GLOBAL WRITE Standard desviation for " + numberOfReqs + " executions (-10%) = " + sGW.getDP(true) / 1000 + " us ");
                    System.out.println(this.id + " //GLOBAL WRITE 90th percentile for " + numberOfReqs + " executions = " + sGW.getPercentile(90) / 1000 + " us ");
                    System.out.println(this.id + " //GLOBAL WRITE 95th percentile for " + numberOfReqs + " executions = " + sGW.getPercentile(95) / 1000 + " us ");
                    System.out.println(this.id + " //GLOBAL WRITE 99th percentile for " + numberOfReqs + " executions = " + sGW.getPercentile(99) / 1000 + " us ");

                }
            }
        }

        public void weighting() {

            //System.out.println("Warm up...");
            //int req = 0;
            ExtStorage sP1 = new ExtStorage();
            ExtStorage sP2 = new ExtStorage();
            //ExtStorage sW1 = new ExtStorage();
            //ExtStorage sW2 = new ExtStorage();
            ExtStorage sGR = new ExtStorage();
            ExtStorage sGW = new ExtStorage();

            System.out.println("Executing experiment for " + numberOfReqs + " ops");

             Random rand = new Random();

                Random randOp = new Random();

                Random randGlobal = new Random();

                Random indexRand = new Random();


//            WorkloadGenerator work = new WorkloadGenerator(numberOfOps);
            for (int i = 0; i < numberOfReqs && !stop; i++) {

                if (i == 1) {
                    try {
                        //Thread.currentThread().sleep(20000);
                        Thread.currentThread().sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ListClientMOMP.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                ExtStorage st = null;

                int p = 0;
                if (mix) {
                    int g = randGlobal.nextInt(100);
                    int r = randOp.nextInt(100);
                    if (g < 5) {//global: 5%
                        //p = -2;
                        if (r < 15) {
                            //GW
                            p = -1;
                        } else {
                            //GR;
                            p = -2;
                        }
                    } else{//local
                        if (r < 15) {

                            op = BFTList.ADD;
                        } else {
                            op = BFTList.CONTAINS;
                        }
                    }
                }

                if (p == 0) {
                    if (this.partitions == 2) {//2 partitions
                        int r = rand.nextInt(100);
                        if (r < 67) {
                            //if (r < 50) {//weighs: 67 for more in P1
                            p = 1;
                            /*if (op == BFTList.ADD) {
                                st = sW1;
                            } else if (op == BFTList.CONTAINS) {
                                st = sR1;
                            }*/
                            st = sP1;

                        } else {
                            p = 2;
                            st = sP2;
                            /*if (op == BFTList.ADD) {
                                st = sW2;
                            } else if (op == BFTList.CONTAINS) {
                                st = sR2;
                            }*/
                        }
                    } else {
                        System.exit(0);
                    }
                    
                }

                

                if (p == -1) { //GW
                    //int index = maxIndex - 1;

                    Integer[] reqs = new Integer[opPerReq];
                    for (int x = 0; x < reqs.length; x++) {
                        reqs[x] = indexRand.nextInt(maxIndex);
                    }
                    long last_send_instant = System.nanoTime();
                    store.addAll(reqs);
                    sGW.store(System.nanoTime() - last_send_instant);
                } else if (p == -2) {//GR

                    Integer[] reqs = new Integer[opPerReq];
                    for (int x = 0; x < reqs.length; x++) {
                        reqs[x] = indexRand.nextInt(maxIndex);
                    }
                    long last_send_instant = System.nanoTime();
                    store.containsAll(reqs);
                    sGR.store(System.nanoTime() - last_send_instant);
                } else if (op == BFTList.ADD) {

                    //int index = rand.nextInt(maxIndex);
                    //int index = maxIndex - 1;

                    Integer[] reqs = new Integer[opPerReq];
                    for (int x = 0; x < reqs.length; x++) {
                        reqs[x] = indexRand.nextInt(maxIndex);
                    }

                    long last_send_instant = System.nanoTime();
                    if (p == 1) {
                        store.addP1(reqs);
                    } else if (p == 2) {
                        store.addP2(reqs);
                    } else {
                        System.exit(0);
                    }

                    st.store(System.nanoTime() - last_send_instant);
                } else if (op == BFTList.CONTAINS) {

                    //int index = rand.nextInt(maxIndex);
                    //int index = maxIndex - 1;

                    Integer[] reqs = new Integer[opPerReq];
                    for (int x = 0; x < reqs.length; x++) {
                        reqs[x] = indexRand.nextInt(maxIndex);
                    }

                    long last_send_instant = System.nanoTime();
                    if (p == 1) {
                        store.containsP1(reqs);
                    } else if (p == 2) {
                        store.containsP2(reqs);
                    } else{
                        System.exit(0);
                    }

                    st.store(System.nanoTime() - last_send_instant);

                }

                if (interval > 0 && i % 50 == 100) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException ex) {
                    }
                }
            }

            if (id == initId) {
                if (mix) {

                    System.out.println(this.id + " //P1 Average time for " + numberOfReqs + " executions (-10%) = " + sP1.getAverage(true) / 1000 + " us ");
                    System.out.println(this.id + " //P1 Standard desviation for " + numberOfReqs + " executions (-10%) = " + sP1.getDP(true) / 1000 + " us ");
                    System.out.println(this.id + " //P1 95th for " + numberOfReqs + " executions (-10%) = " + sP1.getPercentile(95) / 1000 + " us ");

                    System.out.println(this.id + " //P2 Average time for " + numberOfReqs + " executions (-10%) = " + sP2.getAverage(true) / 1000 + " us ");
                    System.out.println(this.id + " //P2 Standard desviation for " + numberOfReqs + " executions (-10%) = " + sP2.getDP(true) / 1000 + " us ");
System.out.println(this.id + " //P2 95th for " + numberOfReqs + " executions (-10%) = " + sP2.getPercentile(95) / 1000 + " us ");

                    
                    System.out.println(this.id + " //GR Average time for " + numberOfReqs + " executions (-10%) = " + sGR.getAverage(true) / 1000 + " us ");
                    System.out.println(this.id + " //GR Standard desviation for " + numberOfReqs + " executions (-10%) = " + sGR.getDP(true) / 1000 + " us ");
System.out.println(this.id + " //GR 95th for " + numberOfReqs + " executions (-10%) = " + sGR.getPercentile(95) / 1000 + " us ");

                    
                    System.out.println(this.id + " //GW Average time for " + numberOfReqs + " executions (-10%) = " + sGW.getAverage(true) / 1000 + " us ");
                    System.out.println(this.id + " //GW Standard desviation for " + numberOfReqs + " executions (-10%) = " + sGW.getDP(true) / 1000 + " us ");
                System.out.println(this.id + " //GW 95th for " + numberOfReqs + " executions (-10%) = " + sGW.getPercentile(95) / 1000 + " us ");

                } /*else if (op == BFTList.ADD) {
                    System.out.println(this.id + " //WRITE W1 Average time for " + numberOfReqs + " executions (-10%) = " + sW1.getAverage(true) / 1000 + " us ");
                    System.out.println(this.id + " //WRITE W1 Standard desviation for " + numberOfReqs + " executions (-10%) = " + sW1.getDP(true) / 1000 + " us ");

                    System.out.println(this.id + " //WRITE W2 Average time for " + numberOfReqs + " executions (-10%) = " + sW2.getAverage(true) / 1000 + " us ");
                    System.out.println(this.id + " //WRITE W2 Standard desviation for " + numberOfReqs + " executions (-10%) = " + sW2.getDP(true) / 1000 + " us ");

                } else {
                    System.out.println(this.id + " //READ R1 Average time for " + numberOfReqs + " executions (-10%) = " + sR1.getAverage(true) / 1000 + " us ");
                    System.out.println(this.id + " //READ  R1 Standard desviation for " + numberOfReqs + " executions (-10%) = " + sR1.getDP(true) / 1000 + " us ");

                    System.out.println(this.id + " //READ R2 Average time for " + numberOfReqs + " executions (-10%) = " + sR2.getAverage(true) / 1000 + " us ");
                    System.out.println(this.id + " //READ  R2 Standard desviation for " + numberOfReqs + " executions (-10%) = " + sR2.getDP(true) / 1000 + " us ");

                }*/
            }

        }
    }
}
