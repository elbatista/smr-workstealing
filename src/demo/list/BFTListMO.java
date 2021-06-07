/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo.list;

import bftsmart.tom.ParallelAsynchServiceProxy;
import bftsmart.tom.ParallelServiceProxy;
import bftsmart.tom.ServiceProxy;
import bftsmart.tom.core.messages.TOMMessageType;
import bftsmart.util.MultiOperationRequest;
import bftsmart.util.MultiOperationResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import parallelism.ParallelMapping;

/**
 *
 * @author alchieri
 */
public class BFTListMO<V> extends BFTList<V> {
    private boolean isMap = false;
    public BFTListMO(int id, boolean parallelExecution, boolean async) {
        super(id, parallelExecution, async);
    }

    public BFTListMO(int id, boolean parallelExecution, boolean async, boolean isMap) {
        super(id, parallelExecution, async);
        this.isMap = isMap;
    }

    public boolean add(V[] e) {
        try {
            MultiOperationRequest mo = new MultiOperationRequest(e.length);
            for (int i = 0; i < e.length; i++) {
                out = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(out);
                dos.writeInt(ADD);
                if(isMap) dos.writeInt((Integer)e[i]);
                ObjectOutputStream out1 = new ObjectOutputStream(out);
                out1.writeObject(isMap ? "Value_"+e[i] : e[i]);
                out1.close();
                mo.add(i, out.toByteArray(), ParallelMapping.SYNC_ALL);
            }
            byte[] rep = null;
            if (parallel) {
                if (async) {
                    int id = asyncProxy.invokeParallelAsynchRequest(mo.serialize(), null, TOMMessageType.ORDERED_REQUEST, ParallelMapping.SYNC_ALL);
                    asyncProxy.cleanAsynchRequest(id);
                    return true;
                } else {
                    rep = proxy.invokeParallel(mo.serialize(), ParallelMapping.SYNC_ALL);
                }
            } else {
                rep = proxy.invokeOrdered(mo.serialize());
            }
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean contains(V[] e) {
        try {
            MultiOperationRequest mo = new MultiOperationRequest(e.length);
            for (int i = 0; i < e.length; i++) {
                out = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(out);
                dos.writeInt(CONTAINS);

                if(isMap){
                    dos.writeInt((Integer)e[i]);
                }
                else {
                    ObjectOutputStream out1 = new ObjectOutputStream(out);
                    out1.writeObject(e[i]);
                    out1.close();
                }

                mo.add(i, out.toByteArray(), ParallelMapping.CONC_ALL);
            }
            if (parallel) {
                if (async) {
                    int id = asyncProxy.invokeParallelAsynchRequest(mo.serialize(), null, TOMMessageType.ORDERED_REQUEST, ParallelMapping.CONC_ALL);
                    asyncProxy.cleanAsynchRequest(id);
                } else {
                    proxy.invokeParallel(mo.serialize(), ParallelMapping.CONC_ALL);
                }
            } else {
                proxy.invokeOrdered(mo.serialize());
            }
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
