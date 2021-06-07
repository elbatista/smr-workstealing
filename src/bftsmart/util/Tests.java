package bftsmart.util;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import parallelism.ExecutionFIFOQueue;
import parallelism.FIFOQueue;
/**
 *
 * @author elbatista
 */
public class Tests {
    
    FIFOQueue<Integer> queue;
    ExecutionFIFOQueue<Integer> execQueue;
    
    public Tests(){
        queue = new FIFOQueue<>();
        execQueue = new ExecutionFIFOQueue<>();
    }
    
    public void doTests(){
        try {
            
            queue.print();
            
            for(int i=1; i<=10; i++){
                queue.put(i);
            }
            
            System.out.println("Antes drain");
            queue.print();
            
            queue.drainSomeToQueue(execQueue, 8);
            //queue.drainToQueue(execQueue);
            
            System.out.println("Depois drain");
            queue.print();
            
            System.out.println("Fila roubo");
            execQueue.print();
            
            queue.put(21);queue.put(22);queue.put(23);
            
            System.out.println("depois novo put");
            queue.print();
            
            
        } catch (InterruptedException ex) {
            Logger.getLogger(Tests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void testDistrExpo(){
        RealDistExponential dist = new RealDistExponential(15);
        Random n = new Random();
        for(int i=0; i<1000;i++){
            System.out.println(dist.sample());
        }
        
    }
    
    public static void main(String args[]){new Tests().testDistrExpo();}
}
