package workstealing;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import parallelism.ClassToThreads;
import parallelism.ExecutionFIFOQueue;
import parallelism.FIFOQueue;
import parallelism.MessageContextPair;

/**
 *
 * @author elbatista
 */
public class WorkStealingFIFOQueue<E> extends FIFOQueue<E> implements BlockingQueue<E> {

    private boolean ready_flag;
    private final int ready_classes_size = 100;
    private boolean[] ready_classes;
    private final ClassToThreads[] ctot;
    
    public WorkStealingFIFOQueue(ClassToThreads[] ctot){
        super();
        this.ready_flag = false;
        this.ready_classes = new boolean[ready_classes_size];
        this.ctot = ctot;
    }
    
    @Override
    public void put(E e) throws InterruptedException {
        lock.lock();
        try {
            last = last.next = new Node<>(e);
            available = true;
            this.ready_classes[((MessageContextPair) e).classId] = true;
        }
        catch(Exception ie){
            java.util.logging.Logger.getLogger(WorkStealingFIFOQueue.class.getName()).log(Level.SEVERE, null, ie);
        }
        finally {lock.unlock();}
    }

    public void putSync(E e) {
        lock.lock();
        try {
            last = last.next = new Node<>(e);
            available = true;
            this.ready_flag = true;
            this.ready_classes[((MessageContextPair) e).classId] = true;
        }
        catch(Exception ie){
            java.util.logging.Logger.getLogger(WorkStealingFIFOQueue.class.getName()).log(Level.SEVERE, null, ie);
        }
        finally {lock.unlock();}
    }
    
    private boolean thereIsNoConflictWith(int classId){
        int[] conflicts = ((WorkStealingClassToThreads)ctot[classId]).getConflicts();
        for(int id : conflicts){
            if(this.ready_classes[id]) return false;
        }
        return true;
    }
    
    public boolean drainToQueue(ExecutionFIFOQueue<E> q, boolean[] exec_flags, int threadId){
        lock.lock();
        try {
            if (available) {
                available = false;
                q.head = this.head.next;
                this.head.next = null;
                last = head;
                exec_flags[threadId] = this.ready_flag;
                this.ready_flag = false;
                this.ready_classes = new boolean[ready_classes_size];
                return true;
            }
            return false;
        }
        catch(Exception ie){
            java.util.logging.Logger.getLogger(WorkStealingFIFOQueue.class.getName()).log(Level.SEVERE, null, ie);
            return false;
        }
        finally {lock.unlock();}
    }
    
    public boolean optimisticSteal(ExecutionFIFOQueue<E> q, boolean[] exec_flags, AtomicInteger[][] markers, int classId, int stealerId, int ownerId) {
        if( available && !this.ready_flag && !exec_flags[ownerId] ){
            boolean steal;
            if(classId == WorkStealingParallelMapping.NO_CLASS){
                steal = true;
            } else {
                steal = thereIsNoConflictWith(classId);
            }
            if(steal){
                lock.lock();
                try {
                    if( available && !this.ready_flag && !exec_flags[ownerId] ){
                        if(classId == WorkStealingParallelMapping.NO_CLASS){
                            steal = true;
                        } else {
                            steal = thereIsNoConflictWith(classId);
                        }
                        if(steal){
                            
                            if(markers[stealerId][ownerId].compareAndSet(0, 1)){
                                available = false;
                                q.head = this.head.next;
                                this.head.next = null;
                                last = head;
                                if(classId == WorkStealingParallelMapping.NO_CLASS){
                                    exec_flags[stealerId] = false;
                                }
                                this.ready_classes = new boolean[ready_classes_size];
                                return true;
                            }
                            else {
                                while(true) {
                                    System.out.println("Should never reach here! Gonna steal, but markers[stealerId][ownerId] was not 0 !");
                                    java.awt.Toolkit.getDefaultToolkit().beep();
                                    try {Thread.sleep(500);} catch (InterruptedException ex) {}
                                }
                            }
                        }
                    }
                }
                catch(Exception ie){
                    java.util.logging.Logger.getLogger(WorkStealingFIFOQueue.class.getName()).log(Level.SEVERE, null, ie);
                }
                finally {lock.unlock();}
            }
        }
        return false;
    }
    
    public boolean optimisticStealSome(ExecutionFIFOQueue<E> q, boolean[] exec_flags, AtomicInteger[][] markers, int classId, int stealerId, int ownerId, int n) {
        if( available && !this.ready_flag && !exec_flags[ownerId] ){
            boolean steal;
            if(classId == WorkStealingParallelMapping.NO_CLASS){
                steal = true;
            } else {
                steal = thereIsNoConflictWith(classId);
            }
            if(steal){
                lock.lock();
                try {
                    if( available && !this.ready_flag && !exec_flags[ownerId] ){
                        if(classId == WorkStealingParallelMapping.NO_CLASS){
                            steal = true;
                        } else {
                            steal = thereIsNoConflictWith(classId);
                        }
                        if(steal){
                            
                            if(markers[stealerId][ownerId].compareAndSet(0, 1)){
                                
                                q.head = this.head.next;
                                Node<E> next = head, prev=null;
                                for(int i=0; i<n+1; i++){
                                    prev = next;
                                    if(next == null) break;
                                    next = next.next;
                                }
                                if(prev != null)prev.next=null;
                                this.head.next = next;
                                if(next == null) {
                                    available = false;
                                    last = head;
                                }
                                
                                if(classId == WorkStealingParallelMapping.NO_CLASS){
                                    exec_flags[stealerId] = false;
                                }
                                this.ready_classes = new boolean[ready_classes_size];
                                return true;
                            }
                            else {
                                while(true) {
                                    System.out.println("Should never reach here! Gonna steal, but markers[stealerId][ownerId] was not 0 !");
                                    java.awt.Toolkit.getDefaultToolkit().beep();
                                    try {Thread.sleep(500);} catch (InterruptedException ex) {}
                                }
                            }
                        }
                    }
                }
                catch(Exception ie){
                    java.util.logging.Logger.getLogger(WorkStealingFIFOQueue.class.getName()).log(Level.SEVERE, null, ie);
                }
                finally {lock.unlock();}
            }
        }
        return false;
    }
    
    public boolean stealToQueue(ExecutionFIFOQueue<E> q, boolean[] exec_flags, AtomicInteger[][] markers, int classId, int stealerId, int ownerId) {
        lock.lock();
        try {
            if( available && !this.ready_flag && !exec_flags[ownerId] ){
                boolean steal;
                if(classId == WorkStealingParallelMapping.NO_CLASS){
                    steal = true;
                } else {
                    steal = thereIsNoConflictWith(classId);
                }
                
                if(steal){
                    if(markers[stealerId][ownerId].compareAndSet(0, 1)){
                        available = false;
                        q.head = this.head.next;
                        this.head.next = null;
                        last = head;
                        if(classId == WorkStealingParallelMapping.NO_CLASS){
                            exec_flags[stealerId] = false;
                        }
                        this.ready_classes = new boolean[ready_classes_size];
                        return true;
                    }
                    else {
                        System.out.println("Should never reach here! Gonna steal, but markers[stealerId][ownerId] was not 0 !");
                    }
                }
            }
            return false;
        }
        catch(Exception ie){
            java.util.logging.Logger.getLogger(WorkStealingFIFOQueue.class.getName()).log(Level.SEVERE, null, ie);
            return false;
        }
        finally {lock.unlock();}
    }
    
    public boolean stealSomeToQueue(ExecutionFIFOQueue<E> q, boolean[] exec_flags, AtomicInteger[][] markers, int classId, int stealerId, int ownerId, int n) {
        lock.lock();
        try {
            if( available && !this.ready_flag && !exec_flags[ownerId] ){
                boolean steal;
                if(classId == WorkStealingParallelMapping.NO_CLASS){
                    steal = true;
                } else {
                    steal = thereIsNoConflictWith(classId);
                }
                
                if(steal){
                    if(markers[stealerId][ownerId].compareAndSet(0, 1)){
                        
                        q.head = this.head.next;
                        Node<E> next = head, prev=null;
                        for(int i=0; i<n+1; i++){
                            prev = next;
                            if(next == null) break;
                            next = next.next;
                        }
                        if(prev != null)prev.next=null;
                        this.head.next = next;
                        if(next == null) {
                            available = false;
                            last = head;
                        }
                        
                        if(classId == WorkStealingParallelMapping.NO_CLASS){
                            exec_flags[stealerId] = false;
                        }
                        this.ready_classes = new boolean[ready_classes_size];
                        return true;
                    }
                    else {
                        System.out.println("Should never reach here! Gonna steal, but markers[stealerId][ownerId] was not 0 !");
                    }
                }
            }
            return false;
        }
        catch(Exception ie){
            java.util.logging.Logger.getLogger(WorkStealingFIFOQueue.class.getName()).log(Level.SEVERE, null, ie);
            return false;
        }
        finally {lock.unlock();}
    }
    
}
