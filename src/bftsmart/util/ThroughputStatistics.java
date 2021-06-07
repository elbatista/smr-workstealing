/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bftsmart.util;

import bftsmart.tom.MessageContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import parallelism.FIFOQueue;
import parallelism.ParallelServiceReplica;

/**
 *
 * @author eduardo
 */
public class ThroughputStatistics {

    private int[][] counters;
    private long[][] stolenCounters;
    //private long[][] cmdCounters;
    private long[][] stealMatrix;
    private int period = 1000; //millis

    private int interval = 30, intervalWarmup = 10;

    private boolean started = false;
    private int now = 0;
    private PrintWriter pw, pwStealMatrix;
    private String print;

    private int numT = 0;
    private int id;

    //private Timer timer = new Timer();
    public ThroughputStatistics(int id, int numThreads, String filePath, String print, int interval, int intervalWarmup) {
        this.print = print;
        this.id = id;
        numT = numThreads;
        this.interval = interval;
        this.intervalWarmup = intervalWarmup;
        counters = new int[numThreads][interval + 1];
        stolenCounters = new long[numThreads][interval + 1];
        //cmdCounters = new long[2][interval + 1];
        stealMatrix = new long[numThreads][numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            for (int j = 0; j < interval + 1; j++) {
                counters[i][j] = 0;
                stolenCounters[i][j] = 0;
            }
            for (int ii = 0; ii < numThreads; ii++) {
                stealMatrix[i][ii] = 0;
            }
        }

        try {

            pw = new PrintWriter(new FileWriter(new File(filePath)));
            pwStealMatrix = new PrintWriter(new FileWriter(new File("stealMatrix_"+print+"_"+id+".txt")));

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void computeThroughput(long timeMillis) {

        for (int time = 0; time <= interval; time++) {
            int total = 0;
            long totalStolen = 0;
            for (int i = 0; i < numT; i++) {

                total = total + counters[i][time];
                totalStolen = totalStolen + stolenCounters[i][time];

            }

            float tp = (float) (total * 1000 / (float) timeMillis);
            float tpStolen = (float) (totalStolen * 1000 / (float) timeMillis);
            //long totalCmds = cmdCounters[0][time]+cmdCounters[1][time];
            //long totalSync = cmdCounters[1][time];
            pw.println(time + "\t" +  tp + "\t" +  tpStolen );

            //System.out.println("Throughput at " + print + " = " + tp + " operations/sec in sec : " + now);
            //pw.println(time + " " + tp);
        }
        pw.flush();
        
        for (int ii = 0; ii < numT; ii++) {
            pwStealMatrix.print("t"+ii+ "\t");
            for (int jj = 0; jj < numT; jj++) {
                pwStealMatrix.print(stealMatrix[ii][jj] + "\t");
            }
            pwStealMatrix.println("");
        }
        pwStealMatrix.flush();
        
        //if(id == 0){
        //    loadTP("results_0.txt");
        //}

    }
    
    private void loadTP(String path) {
        //System.out.println("Vai ler!!!");
        try {

            FileReader fr = new FileReader(path);

            BufferedReader rd = new BufferedReader(fr);
            String line = null;
            int j = 0;
            LinkedList<Double> l = new LinkedList<Double>();
            int nextSec = 0;
            while (((line = rd.readLine()) != null)) {
                StringTokenizer st = new StringTokenizer(line, " ");
                try {
                    int i = Integer.parseInt(st.nextToken());
                    if (i <= 120) {
                        
                        String t = st.nextToken();
                        //System.out.println(t);

                        double d = Double.parseDouble(t);
                        
                        if ( i > nextSec){
                            
                            //System.out.println("entrou para i = "+i+" e next sec = "+nextSec);
                            for(int z = nextSec; z < i; z++){
                                l.add(d);
                               
                            }
                             nextSec = i;
                             
                             //System.out.println("saiu com i = "+i+" e next sec = "+nextSec);
                        }else{
                            //System.out.println("nao entrou i = "+i+" e next sec = "+nextSec);
                        }
                        
                        if( i == nextSec){
                            l.add(d);
                            nextSec++;
                        }
                        //System.out.println("adicionou "+nextSec);
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }

            }
            fr.close();
            rd.close();

            //System.out.println("Size: " + l.size());

            double sum = 0;
            int i;
            for (i = 0; i < l.size(); i++) {
                sum = sum + l.get(i);
            }

            /* double md1 = sum/250;
            sum = 0;
            for(i = 251; i < l.size(); i++){
                sum = sum + l.get(i);
            }
            double md2 = sum/(l.size()-250);
            
            
            System.out.println("Media: "+((md1+md2)/2));*/
            //System.out.println("Sum: " + sum);
            System.out.println("Throughput: " + (sum / l.size()));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void printTP(long timeMillis) {
        int total = 0;
        long totalStolen = 0;
        for (int i = 0; i < numT; i++) {
            total = total + counters[i][now];
            totalStolen = totalStolen + stolenCounters[i][now];
            /* if (!restart[i]) {
                total = total + counters[i];
                counters[i] = 0;
                restart[i] = true;
            }*/

        }

        float tp = (float) (total * 1000 / (float) timeMillis);
        float tpStolen = (float) (totalStolen * 1000 / (float) timeMillis);

        //System.out.println("Throughput at " + print + " = " + tp + " operations/sec in sec : " + now);
        System.out.println(
            print + " Throughput at " + tp + " ops/sec; "
            //+ "Sync at " +totalSync +"/" + totalCmds +" = "+ ((100 * totalSync) / (totalCmds > 0 ? totalCmds : 1)) + "% of all ops; "
            + "in sec : " + now
        );
        //System.out.println("valor de c: "+FIFOQueue.c);

    }

    boolean stoped = true;
    int fakenow = 0;
    public void start() {
        if (!started) {
            started = true;
            now = 0;
            System.out.println("Warming up for "+intervalWarmup+"s !");
            (new Timer()).scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    fakenow++;
                    if(fakenow == intervalWarmup){
                        stoped = false;
                        for(int i = 0; i < numT; i++){
                            counters[i][0] = 0;
                            stolenCounters[i][0] = 0;
                        }
                    }else if (!stoped) {
                        
                        if (now <= interval) {
                            printTP(period);
                            now++;
                        }

                        if (now == interval + 1) {
                            stoped = true;
                            computeThroughput(period);
                        }
                    }
                }
            }, period, period);

        }
    }


    /*public void start() {
        if (!started) {
            started = true;
            (new Timer()).scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    computeThroughput(period);
                }
            }, period, period);
            now = 0;
        }
    }*/
    public void computeStatistics(int threadId, int amount) {
        /*if (restart[threadId]) {
            counters[threadId] = amount;
            restart[threadId] = false;
        } else {
            counters[threadId] = counters[threadId] + amount;
        }*/
        if(!stoped){
            
            try{
                counters[threadId][now] = counters[threadId][now] + amount;
            }catch(ArrayIndexOutOfBoundsException ignore){
                
            }
        }

    }
    
    public void computeStatistics(int threadId, int amount, int victim) {
        if(!stoped){
            try{
                counters[threadId][now] = counters[threadId][now] + amount;
                stolenCounters[threadId][now] = stolenCounters[threadId][now] + amount;
                stealMatrix[threadId][victim] = stealMatrix[threadId][victim] + amount;
            }catch(ArrayIndexOutOfBoundsException ignore){}
        }
    }
    
//    public void incrementConc(){
//        try{cmdCounters[0][now] = cmdCounters[0][now] + 1;}catch(ArrayIndexOutOfBoundsException ignore){}
//    }
//    public void incrementSync(){
//        try{cmdCounters[1][now] = cmdCounters[1][now] + 1;}catch(ArrayIndexOutOfBoundsException ignore){}
//    }

}
