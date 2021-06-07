package bftsmart.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elbatista
 */
public class ExperimentReport {
    
    private final String workload, rootDir;
    private final int shards, threads, size, replicas, totalExperiments, execDuration, clients, warmup;
    private final boolean skewed;
    private PrintWriter pw;

    public ExperimentReport(String workload, int shards, int threads, int size, boolean skewed, int replicas, int totalExperiments, String rootDir, int execDuration, int clients, int warmup) {
        this.workload = workload;
        this.rootDir = rootDir;
        this.shards = shards;
        this.threads = threads;
        this.size = size;
        this.skewed = skewed;
        this.replicas = replicas;
        this.execDuration = execDuration;
        this.clients = clients;
        this.totalExperiments = totalExperiments;
        this.warmup = warmup;
        try { 
            this.pw = new PrintWriter(new FileWriter(new File(rootDir+"/experimentReport.tex")));
        } catch (IOException ex) {
            Logger.getLogger(ExperimentReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generate() {
        try {
            printDocumentHeader();
            printSectionConfig();
            printSectionResults();
            printDocumentFooter();
            pw.flush();
            latex();
            clearAuxFiles();
        } catch (IOException ex) {
            Logger.getLogger(ExperimentReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void printDocumentHeader() {
        pw.println("\\documentclass[a4paper,9pt]{scrartcl}");
        pw.println("\\usepackage[utf8]{inputenc}");
        pw.println("\\usepackage[T1]{fontenc}");
        pw.println("\\usepackage{graphicx}");
        pw.println("\\usepackage[yyyymmdd,hhmmss]{datetime}");
        pw.println("\\usepackage{subfigure}");
        pw.println("\\usepackage[top=0.5in, bottom=1in, left=0.4in, right=0.4in]{geometry}");
        pw.println("\\usepackage{multicol}");
        pw.println("\\title{Experiment Report}");
        pw.println("\\author{Analysis of Work Stealing in P-SMR Early Scheduling}");
        pw.println("\\date{Eliã Rafael L Batista}");
        pw.println("\\begin{document}");
        pw.println("\\maketitle");
    }

    private void printSectionConfig() {
        pw.println("\\section{Experiment configuration}");
        pw.print(
        "\\begin{multicols}{3}\n" +
        "    \\begin{itemize}\n" +
        "        \\item \\textbf{Report compiled on} \\\\ \\today\\ at \\currenttime\n" +
        "        \\item \\large \\textbf{Workload " + this.workload + "}\n" +
        "        \\item \\large \\textbf{List Size " + (this.size/1000) + "K}\n" +
        "    \\end{itemize}\n" +
        "\\columnbreak\n" +
        "    \\begin{itemize}\n" +
        "        \\item \\large \\textbf{" + (!this.skewed?"Not ":"") + "Skewed}\n" +
        "        \\item \\large \\textbf{" + this.shards + " shard(s)}\n" +
        "        \\item \\large \\textbf{" + this.threads + " thread(s)}\n" +
        "    \\end{itemize}\n" +
        "\\columnbreak\n" +
        "    \\begin{itemize}\n" +
        "    	\\small\n" +
        "        \\item \\textbf{Experiment instances} " + this.totalExperiments + "\n" +
        "        \\item \\textbf{Replicas} " + this.replicas + " (Nodes 90-92)\n" +
        "        \\item \\textbf{Clients} 4 (Nodes 1-4) \\\\ " + this.clients + " threads per node = " + (this.clients * 4) + " processes\n" +
        "        \\item \\textbf{Experiment duration} " + this.execDuration + " minute(s) \\\\ (plus " + this.warmup + "s warm-up) \n" +
        "    \\end{itemize}\n" +
        "\\end{multicols}\n");
    }

    private void printSectionResults() {
        String experimentsFolder = "experiment1";
        if(this.totalExperiments > 1) experimentsFolder = "consolidated";
        pw.println("\\section{Results}");
        pw.println("\\subsection{Throughput and quantity of stolen cmds}");
        printSubSectionThroughput(experimentsFolder);
        pw.println("\\subsection{Stealing Matrix}");
        printSubSectionStealingMatrix(experimentsFolder);
    }
    
    private void printSubSectionThroughput(String experimentsFolder) {
        pw.println(
        "\\begin{figure}[h!]\n" +
        "\\centering");
        for(int i=0; i < this.replicas; i++){
            pw.println(
            "\\subfigure[Replica "+i+"]{\n" +
            "  \\includegraphics[scale=0.55]{"+rootDir+"/"+experimentsFolder+"/replica"+i+"/run_data_"+i+".pdf}\n" +
            "}\n" +
            "\\subfigure[Replica "+i+"]{\n" +
            "   \\includegraphics[scale=0.66]{"+rootDir+"/"+experimentsFolder+"/replica"+i+"/consolidated_data_"+i+".pdf}\n" +
            "}");
        }
        pw.println(
        "\\caption{Analytic run data (left); consolidated (average) run data (right)}\n" +
        "\\end{figure}");
    }
    
    private void printSubSectionStealingMatrix(String experimentsFolder) {
        pw.println(
        "The meaning of this graph is that threads on $x$ axis stolen $y$ kops from threads represented in the legend... \n" +
        "\\begin{figure}[h!]\n" +
        "	\\centering\n");
        for(int i=0; i < this.replicas; i++){
            pw.println(
                "   \\subfigure[Replica "+i+"]{\n" +
                "       \\includegraphics[scale=1]{"+rootDir+"/"+experimentsFolder+"/replica"+i+"/steal_matrix_"+i+".pdf}\n" +
                "   }\n");
        }
        pw.println(
        "   \\caption{Stealing instances}\n" +
        "\\end{figure}");
    }
    
    private void printDocumentFooter() {
        pw.println("\\end{document}");
    }

    private void latex() throws IOException {
        try {
            final Process p = Runtime.getRuntime().exec("pdflatex -output-directory "+rootDir+" "+rootDir+"/experimentReport.tex");
            Thread t = new Thread(() -> {
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                try {
                    while ((line = input.readLine()) != null)
                        System.out.println(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t.start();
            t.join();
            p.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(Graphs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearAuxFiles() {
        Arrays.asList(new File(rootDir).listFiles((a,name)->name.endsWith(".aux"))).stream().forEach(file->file.delete());
        Arrays.asList(new File(rootDir).listFiles((a,name)->name.endsWith(".log"))).stream().forEach(file->file.delete());
    }
}
