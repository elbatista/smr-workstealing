package demo.list;

import java.io.IOException;
import bftsmart.util.ExtStorage;
import bftsmart.util.RealDistExponential;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ListClientMOExpDist {

    public static int initId = 0;
    public static int op = BFTList.CONTAINS;
    public static boolean mix = true;
    public static int p = 0;
    public static boolean stop = false;
    static int numThreads;
    @SuppressWarnings("static-access")
    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println(
                "Usage: ... ListClient <num. threads> <process id> " +
                "<maxIndex> <conflict percent>"
            );
            System.exit(-1);
        }

        numThreads = Integer.parseInt(args[0]);
        initId = Integer.parseInt(args[1]);
        int numberOfReqs = 1000000;
        int interval = 0;
        int max = Integer.parseInt(args[2]);
        boolean verbose = false;
        boolean parallel = true;
        boolean async = false;
        int numberOfOps = 50;
        p = Integer.parseInt(args[3]);
                
        if(p==0){
            mix=false;
        }

        Client[] c = new Client[numThreads];

        for (int i = 0; i < numThreads; i++) {
            try {Thread.sleep(100);} catch (InterruptedException ex) {}
            System.out.println("Launching client " + (initId + i));
            c[i] = new ListClientMOExpDist.Client(initId + i, numberOfReqs, numberOfOps, interval, max, verbose, parallel, async);
        }

        try {Thread.sleep(300);} catch (InterruptedException ex) {}

        for (int i = 0; i < numThreads; i++) c[i].start();

        for (int i = 0; i < numThreads; i++) {
            try {c[i].join();} catch (InterruptedException ex) {}
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
        ExtStorage latencies = new ExtStorage();
        long avgLatency = 0;

        public Client(int id, int numberOfRqs, int opPerReq, int interval, int maxIndex, boolean verbose, boolean parallel, boolean async) {
            super("Client " + id);
            this.id = id;
            this.numberOfReqs = numberOfRqs;
            this.opPerReq = opPerReq;
            this.interval = interval;
            this.verbose = verbose;
            this.maxIndex = maxIndex;
            store = new BFTListMO<>(id, parallel, async);
        }

        @Override
        public void run() {
            System.out.println("Executing experiment for " + numberOfReqs + " ops");
            ExtStorage sRead = new ExtStorage();
            ExtStorage sWrite = new ExtStorage();
            Random indexRand = new Random();
            RealDistExponential distExpoRandom = new RealDistExponential((100-p)/p);

            for (int i = 0; i < numberOfReqs && !stop; i++) {
                if (i == 1) {
                    try {Thread.currentThread().sleep(2000);} catch (InterruptedException ex) {}
                }
                
                //cliente que envia os writes
                if(id == 0){
                    /// TODO
                    sendWrites(indexRand, distExpoRandom);
                }
                // cliente que envia reads
                else {
                    Integer[] reqs = new Integer[opPerReq];
                    for (int x = 0; x < reqs.length; x++) {
                        reqs[x] = indexRand.nextInt(maxIndex);
                    }
                    long last_send_instant = System.nanoTime();
                    store.contains(reqs);
                    sRead.store(System.nanoTime() - last_send_instant);
                }

                if (interval > 0 && i % 50 == 100) {
                    try {Thread.sleep(interval);} catch (InterruptedException ex) {}
                }
            }

            if (id == initId) {
                System.out.println(this.id + " // READ Average time for " + numberOfReqs + " executions (-10%) = " + sRead.getAverage(true) / 1000 + " us ");
                System.out.println(this.id + " // READ Standard desviation for " + numberOfReqs + " executions (-10%) = " + sRead.getDP(true) / 1000 + " us ");
                System.out.println(this.id + " // READ 90th percentile for " + numberOfReqs + " executions = " + sRead.getPercentile(90) / 1000 + " us ");
                System.out.println(this.id + " // READ 95th percentile for " + numberOfReqs + " executions = " + sRead.getPercentile(95) / 1000 + " us ");
                System.out.println(this.id + " // READ 99th percentile for " + numberOfReqs + " executions = " + sRead.getPercentile(99) / 1000 + " us ");
                System.out.println(this.id + " // WRITE Average time for " + numberOfReqs + " executions (-10%) = " + sWrite.getAverage(true) / 1000 + " us ");
                System.out.println(this.id + " // WRITE Standard desviation for " + numberOfReqs + " executions (-10%) = " + sWrite.getDP(true) / 1000 + " us ");
                System.out.println(this.id + " // WRITE 90th percentile for " + numberOfReqs + " executions = " + sWrite.getPercentile(90) / 1000 + " us ");
                System.out.println(this.id + " // WRITE 95th percentile for " + numberOfReqs + " executions = " + sWrite.getPercentile(95) / 1000 + " us ");
                System.out.println(this.id + " // WRITE 99th percentile for " + numberOfReqs + " executions = " + sWrite.getPercentile(99) / 1000 + " us ");
            }
            System.out.println(this.id + " FINISHED!!!");
        }

        private void sendWrites(Random indexRand, RealDistExponential distExpoRandom) {
            long actualLatency=0;
            
            //envia
            Integer[] reqs = new Integer[opPerReq];
            for (int x = 0; x < reqs.length; x++) {
                reqs[x] = indexRand.nextInt(maxIndex);
            }
            long last_send_instant = System.nanoTime();
            store.add(reqs);
            actualLatency=(System.nanoTime() - last_send_instant);
            
            // descarta primeira latencia
            latencies.removeFirst();
            
            // insere a latencia atual
            latencies.store(actualLatency);
            
            // calcula a média
            avgLatency = latencies.getAverage(false);
            
            // espera um intervalo na distr exponencial, antes de enviar os proximos writes
            try {
                Thread.sleep((long) (TimeUnit.NANOSECONDS.toMillis(avgLatency) * (distExpoRandom.sample() / (numThreads-1) ) ));
            }
            catch (InterruptedException ex) {}
        }
    }
}
