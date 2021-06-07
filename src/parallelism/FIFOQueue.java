/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author eduardo
 */
public class FIFOQueue<E> implements BlockingQueue<E> {

    protected boolean available = false;
    protected Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();

    /** Head of linked list */
    protected Node<E> head;

    /** Tail of linked list */
    protected Node<E> last;
    
    /**
     * Node class
     */
    public static class Node<E> {

        public E item;
        public Node<E> next = null;

        public Node(E x) {
            item = x;
        }
        
    }

    public FIFOQueue() {
        last = head = new Node<E>(null);
    }

    public Node<E> getHead() {
        return head;
    }

    public Node<E> getLast() {
        return last;
    }
    
    

    @Override
    public void put(E e) throws InterruptedException {
        lock.lock();
        last = last.next = new Node<E>(e);
        available = true;
        cond.signalAll();
        lock.unlock();
    }
    
   //public static int c = 0;
   public void drainToQueue(ExecutionFIFOQueue<E> q) {
        lock.lock();
        try {
            while (!available) {
                try {
                    //c++;
                    //System.out.println("--------------------------------------- Valor de c: "+c);
                    cond.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            available = false;
            q.head = this.head.next;
            this.head.next = null;
            //assert head.item == null;
            last = head;
            
            
        } finally {
            lock.unlock();
            
        }

    }
   
    public void print(){
        Node<E> atual = head.next;
        System.out.print("[");
        while(atual != null){
            System.out.print(atual.item+",");
            atual = atual.next;
        }
        System.out.println("] "+(available?"":"not ")+"avaiable");
    }
   
    public void drainSomeToQueue(ExecutionFIFOQueue<E> q, int n) {
        lock.lock();
        try {
            while (!available) {
                try {cond.await();}catch (InterruptedException e) {e.printStackTrace();}
            }
            
            q.head = this.head.next;
            Node<E> next = head, prev=null;
            for(int i=0; i<n+1;i++){
                prev = next;
                if(next == null) break;
                next = next.next;
            }
            if(prev !=null)prev.next=null;
            this.head.next = next;
            if(next == null) {
                available = false;
                last = head;
            }
            
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        lock.lock();
        Node<E> first;
        try {
            while (!available) {
                try {
                    cond.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            available = false;

            //Pegar AQUI
            
           
            first = head.next;
            head.next = null;
            //assert head.item == null;
            last = head;
            
            
        } finally {
            lock.unlock();
            
        }
        
        // Transfer the elements outside of locks
        int n = 0;
        for (Node<E> p = first; p != null; p = p.next) {
            c.add(p.item);
            p.item = null;
            ++n;
        }
        return n;
    }
    
    


    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean offer(E e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E take() throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int remainingCapacity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E poll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E element() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E peek() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
