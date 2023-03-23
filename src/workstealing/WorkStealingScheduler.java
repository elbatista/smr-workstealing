package workstealing;

//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.Timer;
//import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import parallelism.ClassToThreads;
import parallelism.MessageContextPair;
import parallelism.scheduler.DefaultScheduler;

/**
 *
 * @author elbatista
 */
public class WorkStealingScheduler extends DefaultScheduler{

    public WorkStealingScheduler(int numberWorkers, ClassToThreads[] cToT, int replica, boolean distrExpo) {
        this.mapping = new WorkStealingParallelMapping(numberWorkers, cToT);
//        listaConcs = new ArrayList<>();
//        this.replica = replica;
//        this.distrExpo = distrExpo;
//        
//        (new Timer()).scheduleAtFixedRate(new TimerTask() {
//            public void run() {
//                System.out.println("SCHEDULER --------> "+
//                    totalCmds + " - " + totalSyncCmds +" = " + ((100*(double)totalSyncCmds) / (double)totalCmds)
//                );                                
//            }
//        }, 10000, 3000);
        
    }
    
    @Override
    public void schedule(MessageContextPair request) {
        try {
            ClassToThreads ct = this.mapping.getClass(request.classId);
            if(ct == null) System.err.println("CLASStoTHREADs MAPPING NOT FOUND");
            //totalCmds++;
            //conc
            if(ct.type == ClassToThreads.CONC){
                request.scheduledTime = System.nanoTime();
                ((WorkStealingFIFOQueue)ct.queues[ct.executorIndex]).put(request);
                ct.executorIndex = (ct.executorIndex+1)% ct.queues.length;
            }
            //sync
            else {
//                if(totalConcCmds > 0){
//                    if(totalCmds > 50000 && listaConcs.size() < 500){
//                        //System.out.println("Adding conc cmd");
//                        listaConcs.add(totalConcCmds);
//                    }
//                }
                //totalConcCmds=0;
                request.scheduledTime = System.nanoTime();
                for (BlockingQueue q : ct.queues) ((WorkStealingFIFOQueue)q).putSync(request);
            }
            
//            if(!done && listaConcs.size() == 500){
//                System.out.println("Writing concs file rep "+ replica);
//                try {
//                    PrintWriter pw = new PrintWriter(new FileWriter(new File("concs"+(distrExpo?"_distExp":"")+"_"+replica+".txt")));
//                    for(long x : listaConcs){
//                        pw.println(x);
//                    }
//                    pw.flush();
//                    done=true;
//                } catch (IOException ex) {}
//            }
        }
        catch (InterruptedException ex) {
            Logger.getLogger(WorkStealingScheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
