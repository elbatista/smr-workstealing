package workstealing;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;
import parallelism.ClassToThreads;

/**
 *
 * @author elbatista
 */
public class WorkStealingClassToThreads extends ClassToThreads{
    private final int [] conflicts;
    Phaser phaser;
    
    public  WorkStealingClassToThreads(int classId, int type, int[] ids, int[] conflicts) {
        super(classId, type, ids);
        this.conflicts = conflicts;
    }
    
    public int[] getConflicts(){return this.conflicts;}
    
    @Override
    public void setQueues(BlockingQueue[] q){
        if(q.length != this.tIds.length){
            System.err.println("INCORRECT MAPPING");
        }
        this.queues = q;
        if(this.type == SYNC){
            this.phaser = new Phaser(this.tIds.length);
            this.barrier = new CyclicBarrier(this.tIds.length);
        }
    }
}
