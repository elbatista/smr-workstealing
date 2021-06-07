
package demo.list;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import bftsmart.tom.util.Storage;
import bftsmart.util.ExtStorage;
import bftsmart.util.RealDistExponential;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MapClientMO {

    public static int initId = 0;
    public static int op = BFTList.CONTAINS;
    public static boolean mix = true, distExpo = false;
    public static int p = 0;
    public static boolean stop = false;

    @SuppressWarnings("static-access")
    public static void main(String[] args) throws IOException {
        if (args.length < 5) {
            System.out.println("Usage: ... MapClientMO <num. threads> <process id> <maxIndex> <conflict percent> <dist expo?>");
            System.exit(-1);
        }

        int numThreads = Integer.parseInt(args[0]);
        initId = Integer.parseInt(args[1]);
        int numberOfReqs = 1000000;
        int interval = 0;//Integer.parseInt(args[3]);
        int max = Integer.parseInt(args[2]);
        boolean verbose = false;
        boolean parallel = true;//Boolean.parseBoolean(args[5]);
        boolean async = false;
        int numberOfOps = 50;//Integer.parseInt(args[6]);

        p = Integer.parseInt(args[3]);
        
        distExpo = Boolean.parseBoolean(args[4]);
        
        System.out.println("percent : " + p);
        
        if(p==0){
            mix=false;
        }

        Client[] c = new Client[numThreads];

        for (int i = 0; i < numThreads; i++) {
            try {Thread.sleep(100);} catch (InterruptedException ex) {}
            System.out.println("Launching client " + (initId + i));
            c[i] = new MapClientMO.Client(initId + i, numberOfReqs, numberOfOps, interval, max, verbose, parallel, async);
        }

        try {Thread.sleep(300);} catch (InterruptedException ex) {}

        for (int i = 0; i < numThreads; i++) {
            c[i].start();
        }
        for (int i = 0; i < numThreads; i++) {
            try {
                c[i].join();
            } catch (InterruptedException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    static class Client extends Thread {
        int id;
        int numberOfReqs;
        int interval;
        int countNumOp = 0;
        boolean verbose;
        BFTListMO<Integer> store;
        int maxIndex;
        int opPerReq = 1;

        public Client(int id, int numberOfRqs, int opPerReq, int interval, int maxIndex, boolean verbose, boolean parallel, boolean async) {
            super("Client " + id);

            this.id = id;
            this.numberOfReqs = numberOfRqs;
            this.opPerReq = opPerReq;
            this.interval = interval;
            this.verbose = verbose;
            this.maxIndex = maxIndex;
            store = new BFTListMO<Integer>(id, parallel, async, true);
        }

        public void run() {
            
            if(distExpo){
                runDistExp();
                return;
            } 

            int req = 0;

            ExtStorage sRead = new ExtStorage();
            ExtStorage sWrite = new ExtStorage();

            System.out.println("Executing experiment for " + numberOfReqs + " ops");
            Random rand = new Random();
            Random indexRand = new Random();

            for (int i = 0; i < numberOfReqs && !stop; i++, req++) {

                if (i == 1) {
                    try {
                        Thread.currentThread().sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ListClientMO.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (mix) {
                        int r = rand.nextInt(100);
                        if (r < p) {
                            op = BFTList.ADD;
                        } else {
                            op = BFTList.CONTAINS;
                        }
                }

                if (op == BFTList.ADD) {
                    Integer[] reqs = new Integer[opPerReq];
                    for (int x = 0; x < reqs.length; x++) {
                        reqs[x] = indexRand.nextInt(maxIndex);
                    }

                    long last_send_instant = System.nanoTime();
                    store.add(reqs);
                    sWrite.store(System.nanoTime() - last_send_instant);
                } else if (op == BFTList.CONTAINS) {

                    Integer[] reqs = new Integer[opPerReq];
                    for (int x = 0; x < reqs.length; x++) {
                        reqs[x] = indexRand.nextInt(maxIndex);
                    }

                    long last_send_instant = System.nanoTime();
                    store.contains(reqs);
                    sRead.store(System.nanoTime() - last_send_instant);

                }

                if (verbose) {
                    System.out.println(this.id + " // sent!");
                }

                if (interval > 0 && i % 50 == 100) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException ex) {}
                }

                if (verbose && (req % 1000 == 0)) {
                    System.out.println(this.id + " // " + req + " operations sent!");
                }

            }

            if (id == initId) {
                System.out.println(this.id + " //READ Average time for " + numberOfReqs + " executions (-10%) = " + sRead.getAverage(true) / 1000 + " us ");
                System.out.println(this.id + " //READ Standard desviation for " + numberOfReqs + " executions (-10%) = " + sRead.getDP(true) / 1000 + " us ");
                System.out.println(this.id + " // READ 90th percentile for " + numberOfReqs + " executions = " + sRead.getPercentile(90) / 1000 + " us ");
                System.out.println(this.id + " // READ 95th percentile for " + numberOfReqs + " executions = " + sRead.getPercentile(95) / 1000 + " us ");
                System.out.println(this.id + " // READ 99th percentile for " + numberOfReqs + " executions = " + sRead.getPercentile(99) / 1000 + " us ");

                System.out.println(this.id + " //WRITE Average time for " + numberOfReqs + " executions (-10%) = " + sWrite.getAverage(true) / 1000 + " us ");
                System.out.println(this.id + " //WRITE Standard desviation for " + numberOfReqs + " executions (-10%) = " + sWrite.getDP(true) / 1000 + " us ");
                System.out.println(this.id + " // WRITE 90th percentile for " + numberOfReqs + " executions = " + sWrite.getPercentile(90) / 1000 + " us ");
                System.out.println(this.id + " // WRITE 95th percentile for " + numberOfReqs + " executions = " + sWrite.getPercentile(95) / 1000 + " us ");
                System.out.println(this.id + " // WRITE 99th percentile for " + numberOfReqs + " executions = " + sWrite.getPercentile(99) / 1000 + " us ");
            }

            System.out.println(this.id + " FINISHED!!!");

        }

        private void runDistExp() {

            System.out.println("Executing experiment (expo. distr.) single shard; "+p+"% writes");
            Random indexRand = new Random();
            
            RealDistExponential distExpoRandom = new RealDistExponential((100-p)/p);

            for (int i = 0; i < numberOfReqs && !stop; i++) {

                if (i == 1) {
                    try {Thread.currentThread().sleep(2000);} catch (InterruptedException ex) {}
                }

                double randDistExpo = distExpoRandom.sample();
                                
                Integer[] reqs = new Integer[opPerReq];
                for (int x = 0; x < reqs.length; x++) {
                    reqs[x] = indexRand.nextInt(maxIndex);
                }

                store.add(reqs);
                i++;
                    
                for(int j=0; j < randDistExpo; j++){    
                    reqs = new Integer[opPerReq];
                    for (int x = 0; x < reqs.length; x++) {
                        reqs[x] = indexRand.nextInt(maxIndex);
                    }

                    store.contains(reqs);
                    i++;
                }
            }
        }
    }
}
