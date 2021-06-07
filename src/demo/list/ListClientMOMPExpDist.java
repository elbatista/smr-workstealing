package demo.list;

import bftsmart.util.ExtStorage;
import java.io.IOException;
import bftsmart.util.RealDistExponential;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Example client
 */
public class ListClientMOMPExpDist {

    public static int initId = 0;
    public static int op = BFTList.ADD, writePercent = 15, globalPercent = 5, numThreads, numClients, totalOfThreads;
    public static boolean mix = true, skewed=false;
    public static boolean weight = false;
    public static boolean stop = false;

    @SuppressWarnings("static-access")
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println(
                "Usage: ... ListClientMOMP '<num. threads>-<process id>-<maxIndex>-" +
                "<partitions>-<skewed?>-<writePercent>-<globalPercent>-<distrExpo ?>-<numClients>'");
            System.exit(-1);
        }
        
        String [] aargs = args[0].split("-");

        int numberOfReqs = 1000000;
        int numberOfOps = 50;
        int interval = 0;
        boolean parallel = true;
        numThreads = Integer.parseInt(aargs[0]);
        initId = Integer.parseInt(aargs[1]);
        int max = Integer.parseInt(aargs[2]);
        int p = Integer.parseInt(aargs[3]);
        skewed = Boolean.parseBoolean(aargs[4]);
        writePercent = Integer.parseInt(aargs[5]);
        globalPercent = Integer.parseInt(aargs[6]);
        numClients = Integer.parseInt(aargs[8]);
        
        totalOfThreads = numThreads*numClients;

        Client[] c = new Client[numThreads];

        for (int i = 0; i < numThreads; i++) {
            try {Thread.sleep(100);} catch (InterruptedException ex) {}
            System.out.println("Launching process " + (initId + i));
            c[i] = new ListClientMOMPExpDist.Client(initId + i, numberOfReqs, numberOfOps, interval, max, p, parallel);
        }
        
        try {Thread.sleep(300);} catch (InterruptedException ex) {}
        for (int i = 0; i < numThreads; i++) c[i].start();
        for (int i = 0; i < numThreads; i++) try {c[i].join();} catch (InterruptedException ex) {}
    }

    static class Client extends Thread {
        BFTListMOMP<Integer> store;
        int id;
        int numberOfReqs;
        int interval;
        int maxIndex;
        int opPerReq = 1;
        int partitions = 2;
        long avgLatency = 0;
        ExtStorage latencies = new ExtStorage();

        public Client(int id, int numberOfRqs, int opPerReq, int interval, int maxIndex, int partitions, boolean parallel) {
            super("Client " + id);
            this.id = id;
            this.numberOfReqs = numberOfRqs;
            this.opPerReq = opPerReq;
            this.interval = interval;
            this.partitions = partitions;
            this.maxIndex = maxIndex;
            store = new BFTListMOMP<>(id, parallel);
        }

        public void run() {
            int p;

            System.out.println((skewed?"Skewed e":"E")+"xperiment (net. expo. distr.) for " + partitions + " shards; "+writePercent+"% writes; "+globalPercent+"% global");

            Random rand = new Random();
            Random randGlobal = new Random();
            Random indexRand = new Random();
            RealDistExponential distExpoRandom = new RealDistExponential((100-writePercent)/writePercent);

            for (int i = 0; i < numberOfReqs && !stop; i++) {
                if (i == 1) {
                    try {Thread.currentThread().sleep(2000);} catch (InterruptedException ex) {}
                }

                int g = randGlobal.nextInt(100);
                if (g < globalPercent) {
                    
                    //cliente que envia os writes
                    if(id == 0){
                        /// TODO
                        sendWrites(0, indexRand, distExpoRandom);
                    }
                    // cliente que envia reads
                    else {
                        Integer[] reqs = new Integer[opPerReq];
                        for (int x = 0; x < reqs.length; x++) {
                            reqs[x] = indexRand.nextInt(maxIndex);
                        }
                        store.containsAll(reqs);
                    }

                } else {//local
                    
                    // calcula a partição
                    p = calculateShard(rand);
                    
                    //cliente que envia os writes
                    if(id == 0){
                        /// TODO
                        sendWrites(p, indexRand, distExpoRandom);
                    }
                    // cliente que envia reads
                    else {
                        Integer[] reqs = new Integer[opPerReq];
                        for (int x = 0; x < reqs.length; x++) {
                            reqs[x] = indexRand.nextInt(maxIndex);
                        }
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
                    }
                }

                if (interval > 0 && i % 50 == 100) {
                    try {Thread.sleep(interval);} catch (InterruptedException ex) {}
                }
            }
        }

        private void sendWrites(int p, Random indexRand, RealDistExponential distExpoRandom) {
            
            long actualLatency=0;
            
            //envia
            if(p == 0){ // global
                Integer[] reqs = new Integer[opPerReq];
                for (int x = 0; x < reqs.length; x++) {
                    reqs[x] = indexRand.nextInt(maxIndex);
                }
                long last_send_instant = System.nanoTime();
                store.addAll(reqs);
                actualLatency = (System.nanoTime() - last_send_instant);
            }else { // local
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
                
                actualLatency=(System.nanoTime() - last_send_instant);
            }
            
            //long discountIni = System.nanoTime();
            // descarta primeira latencia
            latencies.removeFirst();
            
            // insere a latencia atual
            latencies.store(actualLatency);
            
            // calcula a média
            avgLatency = latencies.getAverage(false);
            
           // long discountFim = System.nanoTime();
            
            // espera um intervalo na distr exponencial, antes de enviar os proximos writes
            try {Thread.sleep(
                    TimeUnit.NANOSECONDS.toMillis(avgLatency * (long)(distExpoRandom.sample() / (totalOfThreads-1)))
                    //- (discountFim-discountIni)
            );} catch (InterruptedException ex) {}
        }

        private int calculateShard(Random rand) {
            int p = 1;
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
            return p;
        }
    }
}
