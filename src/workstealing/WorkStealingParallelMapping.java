package workstealing;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Phaser;
import parallelism.ClassToThreads;
import parallelism.ParallelMapping;

/**
 *
 * @author elbatista
 */
public class WorkStealingParallelMapping extends ParallelMapping{
    
    public static int NO_CLASS = -4;
    
    public WorkStealingParallelMapping(int numberOfThreads, ClassToThreads[] cToT) {
        super(numberOfThreads, cToT);
        for (int i = 0; i < queues.length; i++) {
            queues[i] = new WorkStealingFIFOQueue(cToT);
        }
        for (ClassToThreads classe : this.classes) {
            if(classe != null){
                BlockingQueue[] q = new BlockingQueue[classe.tIds.length];
                // para cada thread pertencente a esta classe, guarda nos arrays as referencias das suas filas
                for (int j = 0; j < q.length; j++) {
                    q[j] = queues[classe.tIds[j]];
                }
                classe.setQueues(q);
            }
        }
    }
    
    @Override
    public ClassToThreads getClass(int id){
        return this.classes[id];
    }

    public Phaser getPhaser(int classId) {return ((WorkStealingClassToThreads)this.getClass(classId)).phaser;}
}
