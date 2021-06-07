package bftsmart.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elbatista
 */
public class Graphs {
    
    private void gnuplot(String plotFile) throws IOException{
        try {
            final Process p = Runtime.getRuntime().exec("gnuplot "+plotFile);
            Thread t = new Thread(new Runnable() {
                public void run() {
                 BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                 String line = null; 
                 try {
                    while ((line = input.readLine()) != null)
                        System.out.println(line);
                 } catch (IOException e) {
                        e.printStackTrace();
                 }
                }
            });
            t.start();
            t.join();
            p.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(Graphs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void plotRunData(String experimentDir, int replica, int size, int shards, int execDuration) throws IOException {
        int yMax = 300;
        if(size == 10000){
            yMax = 100;
            if(shards > 2){
                yMax = 200;
            }
        }
        if(size == 100000) yMax = 50;
        String dir = experimentDir+"/replica"+replica;
        PrintWriter pw = new PrintWriter(new FileWriter(new File(dir+"/plot_run_data_"+replica+".p")));
        pw.println("set key top maxrow 2 font \"Helvetica, 12\"");
        pw.println("set output '"+dir+"/run_data_"+replica+".pdf'");
        pw.println("set xlabel \"Execution time (seconds)\"");
        pw.println("set ylabel \"Throughput (kops)\"");
        pw.println("set yrange [-10:"+yMax+"]");
        pw.println("set xrange [0:"+(execDuration*60)+"]");
        pw.println("set grid ytics lt 0 lw 1");
        pw.println("set terminal pdf dashed size 8, 3");
        pw.println("set xlabel font \"Helvetica,18\"");
        pw.println("set ylabel font \"Helvetica,18\"");
        pw.println("set xtics font \"Helvetica, 14\"");
        pw.println("set ytics font \"Helvetica, 14\"");
        
        pw.println("plot \\");
        pw.println("\""+dir+"/resultsSEQ_"+replica+".txt\" using 1:($2/1000) title \"Sequential\" with line lc \"blue\" dashtype 1 lw 2, \\");
        pw.println("\""+dir+"/resultsCB_"+replica+".txt\" using 1:($2/1000) title \"Cbase\" with line lc \"black\" dashtype 8 lw 3, \\");
        pw.println("\""+dir+"/resultsES_"+replica+".txt\" using 1:($2/1000) title \"Early Scheduling\" with line lc \"red\" dashtype 2 lw 2, \\");
        pw.println("\""+dir+"/resultsWS_"+replica+".txt\" using 1:($2/1000) title \"Work Stealing\" with line lc \"green\" dashtype 4 lw 2, \\");
        pw.println("\""+dir+"/resultsWS_"+replica+".txt\" using 1:($3/1000) title \"Stealed\" with line lc \"violet\" dashtype 5 lw 2");

        pw.flush();
        pw.close();
        gnuplot(dir+"/plot_run_data_"+replica+".p");
    }

    public void plotConsolidatedData(String experimentDir, int replica, int size, int shards) throws IOException {
        int yMax = 300;
        if(size == 10000){
            yMax = 100;
            if(shards > 2){
                yMax = 200;
            }
        }
        if(size == 100000) yMax = 50;
        String dir = experimentDir+"/replica"+replica;
        PrintWriter pw = new PrintWriter(new FileWriter(new File(dir+"/plot_cons_data_"+replica+".p")));
        pw.println("set key left top maxrow 2 font \"Helvetica, 11\"");
        pw.println("set output '"+dir+"/consolidated_data_"+replica+".pdf'");
        pw.println("set style fill pattern border lt -1");
        pw.println("set terminal pdf dashed size 4, 2.5");
        pw.println("set style data histogram");
        pw.println("set style histogram errorbars gap 2 lw 2");
        pw.println("set yrange [0:"+yMax+"]");
        pw.println("set xlabel font \"Helvetica,16\"");
        pw.println("set ylabel font \"Helvetica,16\"");
        pw.println("set xtics font \"Helvetica, 11\"");
        pw.println("set ytics font \"Helvetica, 14\"");
        pw.println("set grid ytics lt 0 lw 1");
        pw.println("set ylabel \"Throughput (kops)\"");
        pw.println("set xlabel \"Algorithm\"");
        
        pw.println("plot \\");
        pw.println("\""+dir+"/consTp_"+replica+".txt\" using ($2/1000):($3/1000):xtic(1) ti \"Avg TP\", \\");
        pw.println("\""+dir+"/consSteal_"+replica+".txt\" using ($2/1000):($3/1000):xtic(1) ti \"Avg Stealed TP\"");

        pw.flush();
        pw.close();
        gnuplot(dir+"/plot_cons_data_"+replica+".p");
    }

    void plotStealMatrix(String experimentDir, int replica, int threads) throws IOException {
        String dir = experimentDir+"/replica"+replica;
        PrintWriter pw = new PrintWriter(new FileWriter(new File(dir+"/plot_steal_matrix_"+replica+".p")));
        
        pw.println("set key left top font \"Helvetica, 8\"");
        pw.println("set output '"+dir+"/steal_matrix_"+replica+".pdf'");
        pw.println("set style fill pattern border lt -1");
        pw.println("set terminal pdf dashed size 7, 2.5");
        pw.println("set style data histogram");
        pw.println("set xlabel font \"Helvetica,16\"");
        pw.println("set ylabel font \"Helvetica,16\"");
        pw.println("set xtics font \"Helvetica, 11\"");
        pw.println("set ytics font \"Helvetica, 14\"");
        pw.println("set grid ytics lt 0 lw 1");
        pw.println("set ylabel \"Total stolen cmds (kops)\"");
        pw.println("set xlabel \"Stealer Thread\"");
        
        pw.println("plot \\");
        
        for(int i=0; i < threads; i++){
            pw.println("\""+dir+"/stealMatrix_"+replica+".txt\" using ($"+(i+2)+"/1000):xtic(1) ti \"From t"+i+"\""+(i < (threads-1)?", \\":"")); 
        }
        
//        pw.println("\""+dir+"/stealMatrix_"+replica+".txt\" using ($2/1000):xtic(1) ti \"From t0\", \\");
//        pw.println("\""+dir+"/stealMatrix_"+replica+".txt\" using ($3/1000):xtic(1) ti \"From t1\", \\");
//        pw.println("\""+dir+"/stealMatrix_"+replica+".txt\" using ($4/1000):xtic(1) ti \"From t2\", \\");
//        pw.println("\""+dir+"/stealMatrix_"+replica+".txt\" using ($5/1000):xtic(1) ti \"From t3\"");
        
        pw.flush();
        pw.close();
        gnuplot(dir+"/plot_steal_matrix_"+replica+".p");
    }
}
