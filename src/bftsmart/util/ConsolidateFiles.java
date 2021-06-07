package bftsmart.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elbatista
 */
public class ConsolidateFiles {
    private String rootDir, experimentDir;
    private String workload;
    private int shards, threads, experiment, size, replicas, totalExperiments, execDuration, clients, warmup, conflictPercentage, stealSize, global;
    private boolean skewed, generateReport, copyFiles, generateConsolidatedFiles, distrExpo, wsVersions;
    private final Graphs graphs;
    private ExperimentReport report;
    String [] stealerTypes = {/*"smartOptStealer",*/"smartOptStealer"};
    
    public static void main(String [] args){
        new ConsolidateFiles(args);
    }
    
    public ConsolidateFiles(String [] args){
        this.graphs = new Graphs();
        parseArgs(args);
        createDirs();
        for(int i=0; i< this.replicas; i++){
            consolidateReplica(i);
        }
        if(this.generateReport){
            report = new ExperimentReport(workload, shards, threads, size, skewed, replicas, totalExperiments, rootDir, execDuration, clients, warmup);
            report.generate();
        }
    }
    
    private void parseArgs(String[] args) {

        String [] aargs = args[0].split("-");
        this.shards = Integer.valueOf(aargs[0]);
        this.threads = Integer.valueOf(aargs[1]);
        this.size = Integer.valueOf(aargs[2]);
        this.skewed = Boolean.valueOf(aargs[3]);
        this.replicas = Integer.valueOf(aargs[4]);
        this.generateReport = Boolean.valueOf(aargs[5]);
        this.execDuration = Integer.valueOf(aargs[6]);
        this.clients = Integer.valueOf(aargs[7]);
        this.copyFiles = Boolean.valueOf(aargs[8]);
        this.generateConsolidatedFiles = Boolean.valueOf(aargs[9]);
        this.warmup = Integer.valueOf(aargs[10]);
        this.conflictPercentage = Integer.valueOf(aargs[11]);
        this.stealSize = Integer.parseInt(aargs[12]);
        this.distrExpo = Boolean.valueOf(aargs[13]);
        this.global = Integer.parseInt(aargs[14]);
        try{ this.wsVersions = Boolean.valueOf(aargs[15]);}catch(Exception e){}
        
        if(this.shards == 1){
            this.rootDir = "results/singleShard/"+(this.size/1000)+"K_"+(this.skewed?"Skewed_":"")+
            this.threads+"T_"+this.conflictPercentage+"Writes_"+this.stealSize+"Steal"+(this.distrExpo?"_DistrExpo_":"_")+clients+"Clients";
            //this.rootDir = "results/singleShard/"+(this.size/1000)+"K_"+(this.skewed?"Skewed_":"")+this.threads+"T_"+this.conflictPercentage+"Writes";
        } else {
            this.rootDir = "results/multiShard/"+(global>0?"global/":"")+(this.size/1000)+"K_"+(this.skewed?"Skewed_":"")+
            this.shards+"P"+this.threads+"T_"+this.conflictPercentage+"Writes_"+this.stealSize+"Steal"+(this.distrExpo?"_DistrExpo_":"_")+clients+"Clients" +
            (global>0?global+"g":"");
            //this.rootDir = "results/multiShard/"+(this.size/1000)+"K_"+(this.skewed?"Skewed_":"")+this.shards+"P"+this.threads+"T_"+this.conflictPercentage+"Writes";
        }
        //this.experimentDir = this.rootDir+"/experiment"+this.experiment;

        // if(this.wsVersions){
        //     stealerTypes = new String[4];
        //     //stealerTypes[0] = "defaultStealer_v1";//,"defaultStealer_v2","smartOptStealer"}
        //     //stealerTypes[1] = "defaultStealer_v2";
        //     //stealerTypes[2] = "smartStealer";
        //     stealerTypes[3] = "smartOptStealer";
        // }
    }
    
    private void createDirs(){
        File dir = new File(rootDir);
        if (!dir.exists()) dir.mkdirs();
        dir = new File("results/multiShard/consolidated");
        if (!dir.exists()) dir.mkdirs();
        if(global > 0){
            dir = new File("results/multiShard/global/consolidated");
            if (!dir.exists()) dir.mkdirs();
        }
        dir = new File("results/singleShard/consolidated");
        if (!dir.exists()) dir.mkdirs();
        for(int i=0; i< this.replicas; i++){
            dir = new File(rootDir+"/replica"+i);
            if (!dir.exists()) dir.mkdirs();
        }
    }
    
    public static Double[] consolidateFile(String file){
        Double [] ret = new Double[6];
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            Double sum=0.0, avg, standardDeviation=0.0;
            Double sumSteal=0.0, avgSteal, standardDeviationSteal=0.0;
            Double sumSync=0.0, avgSync, standardDeviationSync=0.0;
            int cont = 0;
            ArrayList<Double> array = new ArrayList<>();
            ArrayList<Double> arraySteal = new ArrayList<>();
            ArrayList<Double> arraySync = new ArrayList<>();
            String str = "", strSteal = "", strSync = "";
            while (line != null) {
                str = line.split("\t")[1];
                strSteal = line.split("\t")[2];
                try{strSync = line.split("\t")[3];}catch(Exception e){strSync="0";}
                sum += Double.parseDouble(str);
                sumSteal += Double.parseDouble(strSteal);
                sumSync += Double.parseDouble(strSync);
                line = br.readLine();
                cont++;
                array.add(Double.parseDouble(str));
                arraySteal.add(Double.parseDouble(strSteal));
                arraySync.add(Double.parseDouble(strSync));
            }
            br.close();
            
            avg = sum/cont;
            avgSteal = sumSteal/cont;
            avgSync = sumSync/cont;
            ret[0] = (avg);
            ret[2] = (avgSteal);
            ret[4] = (avgSync);
            
            for(Double i : array){
                standardDeviation += Math.pow(i - avg, 2);
            }
            ret[1]=(Math.sqrt(standardDeviation/cont));
            
            for(Double ii : arraySteal){
                standardDeviationSteal += Math.pow(ii - avgSteal, 2);
            }
            ret[3]=(Math.sqrt(standardDeviationSteal/cont));
            
            for(Double iii : arraySync){
                standardDeviationSync += Math.pow(iii - avgSync, 2);
            }
            ret[5]=(Math.sqrt(standardDeviationSync/cont));
            
        }
        catch(IOException | NumberFormatException e){
            Logger.getLogger(ConsolidateFiles.class.getName()).log(Level.SEVERE, null, e);
        }
        return ret;
    }

    private void consolidateReplica(int replica) {
        try {
            if(this.copyFiles) copyFiles(replica);
            if(this.generateConsolidatedFiles){
                generateConsolidatedFiles(replica);
                //graphs.plotRunData(experimentDir, replica, size, shards, execDuration);
                //graphs.plotConsolidatedData(experimentDir, replica, size, shards);
                //graphs.plotStealMatrix(experimentDir, replica, threads);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(ConsolidateFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void copyFiles(int replica) throws IOException {
        Arrays.asList(new File("./").listFiles((a,name)->name.matches("results_(.*).txt"))).stream().forEach(file->{
            try{
                Files.copy(
                    file.toPath(), 
                    Paths.get(this.rootDir+"/replica"+replica+"/"+file.getName()), 
                    StandardCopyOption.REPLACE_EXISTING
                );
            }
            catch (NoSuchFileException  e){} 
            catch (IOException ex) {}
        });

        Arrays.asList(new File(".").listFiles((a,name)->name.matches("stealMatrix*"+replica+".txt"))).stream().forEach(file->{
            try{
                Files.copy(
                    file.toPath(), 
                    Paths.get(this.rootDir+"/replica"+replica+"/"+file.getName()), 
                    StandardCopyOption.REPLACE_EXISTING
                );
            }
            catch (NoSuchFileException  e){} 
            catch (IOException ex) {}
        });

        Arrays.asList(new File(".").listFiles((a,name)->name.matches("latency_(.*).txt"))).stream().forEach(file->{
            try{
                Files.copy(
                    file.toPath(), 
                    Paths.get(this.rootDir+"/replica"+replica+"/"+file.getName()), 
                    StandardCopyOption.REPLACE_EXISTING
                );
            }
            catch (NoSuchFileException  e){} 
            catch (IOException ex) {}
        });
    }

    private void generateConsolidatedFiles(int replica) throws IOException {
        String dir = this.rootDir+"/replica"+replica;
        PrintWriter pw = new PrintWriter(new FileWriter(new File(dir+"/consTp_"+replica+".txt")));
        
        String dirRootCons = (shards == 1 ? "results/singleShard/consolidated/tp_" : "results/multiShard/"+(global>0?"global/":"")+"consolidated/tp_");
        String fileName = "_"+(size/1000)+"K_" + (shards > 1 ? shards+"P_" : "") +conflictPercentage+"W"+(skewed?"_skewed":"")+"_"+this.stealSize+"Steal_"+
                this.clients+"Cli" + (global>0 ? ("_"+global+"g") : "") +"_rep"+replica+".txt";
        int coluna0 = threads;//(shards == 1 ? threads : shards);
        
        PrintWriter pw2 = new PrintWriter(new FileWriter(new File(dir+"/consSteal_"+replica+".txt")));
        
        Double [] ret;
        
        int nthreadsaux = 4;
        if(shards == 4) nthreadsaux = 9;
        if(shards == 8) nthreadsaux = 17;
        
        //if(!this.wsVersions){
            //if(threads < nthreadsaux ){
                ret = consolidateFile(dir+"/results_seq_"+replica+".txt");
                pw.print("\"SEQ\"\t"+ret[0]+"\t"+ret[1]+"\r\n");
                pw2.print("\"SEQ\"\t"+ret[2]+"\t"+ret[3]+"\r\n");
                String seq = "1"+"\t"+ret[0]+"\t"+ret[1]+"\r\n";
                Files.write(Paths.get(dirRootCons+"seq"+fileName), seq.getBytes(StandardCharsets.UTF_8),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
            //}
            ret = consolidateFile(dir+"/results_cbase_"+replica+".txt");
            pw.print("\"CBASE\"\t"+ret[0]+"\t"+ret[1]+"\r\n");
            pw2.print("\"CBASE\"\t"+ret[2]+"\t"+ret[3]+"\r\n");
            String cbase = coluna0+"\t"+ret[0]+"\t"+ret[1]+"\r\n";
            Files.write(Paths.get(dirRootCons+"cbase"+fileName), cbase.getBytes(StandardCharsets.UTF_8),StandardOpenOption.CREATE,StandardOpenOption.APPEND);

            ret = consolidateFile(dir+"/results_busyWait_"+replica+".txt");
            pw.print("\"Early Busy Wait\"\t"+ret[0]+"\t"+ret[1]+"\t"+ret[4]+"\t"+ret[5]+"\r\n");
            pw2.print("\"Early Busy Wait\"\t"+ret[2]+"\t"+ret[3]+"\r\n");
            String bwait = coluna0+"\t"+ret[0]+"\t"+ret[1]+"\t"+ret[4]+"\t"+ret[5]+"\r\n";
            Files.write(Paths.get(dirRootCons+"earlybw"+fileName), bwait.getBytes(StandardCharsets.UTF_8),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
       // }
        
        ret = consolidateFile(dir+"/results_early_"+replica+".txt");
        pw.print("\"Early Scheduling\"\t"+ret[0]+"\t"+ret[1]+"\t"+ret[4]+"\t"+ret[5]+"\r\n");
        pw2.print("\"Early Scheduling\"\t"+ret[2]+"\t"+ret[3]+"\r\n");
        String early = coluna0+"\t"+ret[0]+"\t"+ret[1]+"\t"+ret[4]+"\t"+ret[5]+"\r\n";
        Files.write(Paths.get(dirRootCons+"early"+fileName), early.getBytes(StandardCharsets.UTF_8),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
//        ret = consolidateFile(dir+"/results_ws_"+replica+".txt");
//        pw.print("\"Work Stealing\"\t"+ret[0]+"\t"+ret[1]+"\r\n");
//        pw2.print("\"Work Stealing\"\t"+ret[2]+"\t"+ret[3]+"\r\n");
//        String ws = coluna0+"\t"+ret[0]+"\t"+ret[1]+"\t"+ret[2]+"\t"+ret[3]+"\r\n";
//        Files.write(Paths.get(dirRootCons+"ws"+fileName), ws.getBytes(StandardCharsets.UTF_8),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
        for(String s : stealerTypes){
            ret = consolidateFile(dir+"/results_"+s+"_"+replica+".txt");
            pw.print("\""+s+"\"\t"+ret[0]+"\t"+ret[1]+"\t"+ret[4]+"\t"+ret[5]+"\r\n");
            pw2.print("\""+s+"\"\t"+ret[2]+"\t"+ret[3]+"\r\n");
            String ws = coluna0+"\t"+ret[0]+"\t"+ret[1]+"\t"+ret[2]+"\t"+ret[3]+"\t"+ret[4]+"\t"+ret[5]+"\r\n";
            Files.write(Paths.get(dirRootCons+s+fileName), ws.getBytes(StandardCharsets.UTF_8),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
        }
        
        pw.flush();
        pw2.flush();
        pw.close();
        pw2.close();
    }
   
}
