/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workstealing;

import demo.list.MultipartitionMapping;

/**
 *
 * @author elbatista
 */
public class WorkStealingMultiPartitionMapping extends MultipartitionMapping {
    
    
    // two shards:
    public static WorkStealingClassToThreads[] getWSM2P2T2(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        int[] conflicts = new int[3];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //GW
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        conflicts = new int[6];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        conflicts[3] = R1;
        conflicts[4] = R2;
        conflicts[5] = GR;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //R1
        ids = new int[1];
        ids[0] = 0;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //R2
        ids = new int[1];
        ids[0] = 1;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        
        //W1
        ids = new int[1];
        ids[0] = 0;
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //W2
        ids = new int[1];
        ids[0] = 1;
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSM2P2T4(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 0;
        ids[1] = 2;
        int[] conflicts = new int[3];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);

        //GW
        ids = new int[4];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        conflicts = new int[6];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        conflicts[3] = R1;
        conflicts[4] = R2;
        conflicts[5] = GR;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R1
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //R2
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 3;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //W1
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //W2
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 3;
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSM2P2T6(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 0;
        ids[1] = 2;
        int[] conflicts = new int[3];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);

        //GW
        ids = new int[6];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        conflicts = new int[6];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        conflicts[3] = R1;
        conflicts[4] = R2;
        conflicts[5] = GR;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R1
        ids = new int[3];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 4;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //R2
        ids = new int[3];
        ids[0] = 2;
        ids[1] = 3;
        ids[2] = 5;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //W1
        ids = new int[3];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 4;
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //W2
        ids = new int[3];
        ids[0] = 2;
        ids[1] = 3;
        ids[2] = 5;
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSM2P2T8(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 3;
        ids[1] = 7;
        int[] conflicts = new int[3];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);

        //GW
        ids = new int[8];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        conflicts = new int[6];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        conflicts[3] = R1;
        conflicts[4] = R2;
        conflicts[5] = GR;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R1
        ids = new int[4];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //R2
        ids = new int[4];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //W1
        ids = new int[4];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //W2
        ids = new int[4];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSP2T10(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 3;
        ids[1] = 7;
        int[] conflicts = new int[3];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //GW
        ids = new int[10];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        ids[8] = 8;
        ids[9] = 9;
        conflicts = new int[6];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        conflicts[3] = R1;
        conflicts[4] = R2;
        conflicts[5] = GR;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //R1
        ids = new int[5];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 8;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W1
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //R2
        ids = new int[5];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 9;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W2
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSM2P2T12(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 1;
        ids[1] = 9;
        int[] conflicts = new int[3];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //GW
        ids = new int[12];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        ids[8] = 8;
        ids[9] = 9;
        ids[10] = 10;
        ids[11] = 11;
        conflicts = new int[6];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        conflicts[3] = R1;
        conflicts[4] = R2;
        conflicts[5] = GR;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //R1
        ids = new int[6];
        ids[0] = 2;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 8;
        ids[5] = 9;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //R2
        ids = new int[6];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 10;
        ids[5] = 11;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        
        //W1
        ids = new int[6];
        ids[0] = 2;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 8;
        ids[5] = 9;
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //W2
        ids = new int[6];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 10;
        ids[5] = 11;
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSM2P2T12RW(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        int[] conflicts = new int[3];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //GW
        ids = new int[12];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        ids[8] = 8;
        ids[9] = 9;
        ids[10] = 10;
        ids[11] = 11;
        conflicts = new int[6];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        conflicts[3] = R1;
        conflicts[4] = R2;
        conflicts[5] = GR;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //R1
        ids = new int[11];
        ids[0] = 1;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 5;
        ids[5] = 6;
        ids[6] = 7;
        ids[7] = 8;
        ids[8] = 9;
        ids[9] = 10;
        ids[10] = 11;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //R2
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        
        //W1
        ids = new int[11];
        ids[0] = 1;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 5;
        ids[5] = 6;
        ids[6] = 7;
        ids[7] = 8;
        ids[8] = 9;
        ids[9] = 10;
        ids[10] = 11;
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //W2
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSM2P2T16(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[4];
        ids[0] = 3;
        ids[1] = 7;
        ids[2] = 11;
        ids[3] = 15;
        int[] conflicts = new int[3];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //GW
        ids = new int[16];
        for(int i = 0; i < 16;i++){
            ids[i] = i;
        }
        conflicts = new int[6];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        conflicts[3] = R1;
        conflicts[4] = R2;
        conflicts[5] = GR;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R1
        ids = new int[8];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 8;
        ids[5] = 10;
        ids[6] = 11;
        ids[7] = 12;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //R2
        ids = new int[8];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 9;
        ids[5] = 13;
        ids[6] = 14;
        ids[7] = 15;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //W1
        ids = new int[8];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 8;
        ids[5] = 10;
        ids[6] = 11;
        ids[7] = 12;
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //W2
        ids = new int[8];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 9;
        ids[5] = 13;
        ids[6] = 14;
        ids[7] = 15;
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSM2P2T32(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[8];
        ids[0] = 3;
        ids[1] = 7;
        ids[2] = 11;
        ids[3] = 15;
        ids[4] = 19;
        ids[5] = 23;
        ids[6] = 27;
        ids[7] = 31;
        int[] conflicts = new int[3];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //GW
        ids = new int[32];
        for(int i = 0; i < 32;i++){
            ids[i] = i;
        }
        conflicts = new int[6];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        conflicts[3] = R1;
        conflicts[4] = R2;
        conflicts[5] = GR;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R1
        ids = new int[16];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 8;
        ids[5] = 10;
        ids[6] = 11;
        ids[7] = 12;
        ids[8] = 16;
        ids[9] = 18;
        ids[10] = 19;
        ids[11] = 20;
        ids[12] = 24;
        ids[13] = 26;
        ids[14] = 27;
        ids[15] = 28;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //R2
        ids = new int[16];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 9;
        ids[5] = 13;
        ids[6] = 14;
        ids[7] = 15;
        ids[8] = 17;
        ids[9] = 21;
        ids[10] = 22;
        ids[11] = 23;
        ids[12] = 25;
        ids[13] = 29;
        ids[14] = 30;
        ids[15] = 31;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //W1
        ids = new int[16];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 8;
        ids[5] = 10;
        ids[6] = 11;
        ids[7] = 12;
        ids[8] = 16;
        ids[9] = 18;
        ids[10] = 19;
        ids[11] = 20;
        ids[12] = 24;
        ids[13] = 26;
        ids[14] = 27;
        ids[15] = 28;
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //W2
        ids = new int[16];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 9;
        ids[5] = 13;
        ids[6] = 14;
        ids[7] = 15;
        ids[8] = 17;
        ids[9] = 21;
        ids[10] = 22;
        ids[11] = 23;
        ids[12] = 25;
        ids[13] = 29;
        ids[14] = 30;
        ids[15] = 31;
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSM2P2T40(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[10];
        ids[0] = 3;
        ids[1] = 7;
        ids[2] = 11;
        ids[3] = 15;
        ids[4] = 19;
        ids[5] = 23;
        ids[6] = 27;
        ids[7] = 31;
        ids[8] = 33;
        ids[9] = 39;
        int[] conflicts = new int[3];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //GW
        ids = new int[40];
        for(int i = 0; i < 40;i++){
            ids[i] = i;
        }
        conflicts = new int[6];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        conflicts[3] = R1;
        conflicts[4] = R2;
        conflicts[5] = GR;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R1
        ids = new int[20];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 8;
        ids[5] = 10;
        ids[6] = 11;
        ids[7] = 12;
        ids[8] = 16;
        ids[9] = 18;
        ids[10] = 19;
        ids[11] = 20;
        ids[12] = 24;
        ids[13] = 26;
        ids[14] = 27;
        ids[15] = 28;
        ids[16] = 32;
        ids[17] = 34;
        ids[18] = 35;
        ids[19] = 36;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //R2
        ids = new int[20];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 9;
        ids[5] = 13;
        ids[6] = 14;
        ids[7] = 15;
        ids[8] = 17;
        ids[9] = 21;
        ids[10] = 22;
        ids[11] = 23;
        ids[12] = 25;
        ids[13] = 29;
        ids[14] = 30;
        ids[15] = 31;
        ids[16] = 33;
        ids[17] = 37;
        ids[18] = 38;
        ids[19] = 39;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //W1
        ids = new int[20];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 8;
        ids[5] = 10;
        ids[6] = 11;
        ids[7] = 12;
        ids[8] = 16;
        ids[9] = 18;
        ids[10] = 19;
        ids[11] = 20;
        ids[12] = 24;
        ids[13] = 26;
        ids[14] = 27;
        ids[15] = 28;
        ids[16] = 32;
        ids[17] = 34;
        ids[18] = 35;
        ids[19] = 36;
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //W2
        ids = new int[20];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 9;
        ids[5] = 13;
        ids[6] = 14;
        ids[7] = 15;
        ids[8] = 17;
        ids[9] = 21;
        ids[10] = 22;
        ids[11] = 23;
        ids[12] = 25;
        ids[13] = 29;
        ids[14] = 30;
        ids[15] = 31;
        ids[16] = 33;
        ids[17] = 37;
        ids[18] = 38;
        ids[19] = 39;
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSM2P2T48(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[12];
        ids[0] = 3;
        ids[1] = 7;
        ids[2] = 11;
        ids[3] = 15;
        ids[4] = 19;
        ids[5] = 23;
        ids[6] = 27;
        ids[7] = 31;
        ids[8] = 33;
        ids[9] = 39;
        ids[10] = 43;
        ids[11] = 47;
        int[] conflicts = new int[3];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //GW
        ids = new int[48];
        for(int i = 0; i < 48;i++){
            ids[i] = i;
        }
        conflicts = new int[6];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        conflicts[3] = R1;
        conflicts[4] = R2;
        conflicts[5] = GR;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R1
        ids = new int[24];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 8;
        ids[5] = 10;
        ids[6] = 11;
        ids[7] = 12;
        ids[8] = 16;
        ids[9] = 18;
        ids[10] = 19;
        ids[11] = 20;
        ids[12] = 24;
        ids[13] = 26;
        ids[14] = 27;
        ids[15] = 28;
        ids[16] = 32;
        ids[17] = 34;
        ids[18] = 35;
        ids[19] = 36;
        ids[20] = 40;
        ids[21] = 42;
        ids[22] = 43;
        ids[23] = 44;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //R2
        ids = new int[24];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 9;
        ids[5] = 13;
        ids[6] = 14;
        ids[7] = 15;
        ids[8] = 17;
        ids[9] = 21;
        ids[10] = 22;
        ids[11] = 23;
        ids[12] = 25;
        ids[13] = 29;
        ids[14] = 30;
        ids[15] = 31;
        ids[16] = 33;
        ids[17] = 37;
        ids[18] = 38;
        ids[19] = 39;
        ids[20] = 41;
        ids[21] = 45;
        ids[22] = 46;
        ids[23] = 47;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //W1
        ids = new int[24];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 8;
        ids[5] = 10;
        ids[6] = 11;
        ids[7] = 12;
        ids[8] = 16;
        ids[9] = 18;
        ids[10] = 19;
        ids[11] = 20;
        ids[12] = 24;
        ids[13] = 26;
        ids[14] = 27;
        ids[15] = 28;
        ids[16] = 32;
        ids[17] = 34;
        ids[18] = 35;
        ids[19] = 36;
        ids[20] = 40;
        ids[21] = 42;
        ids[22] = 43;
        ids[23] = 44;
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //W2
        ids = new int[24];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 9;
        ids[5] = 13;
        ids[6] = 14;
        ids[7] = 15;
        ids[8] = 17;
        ids[9] = 21;
        ids[10] = 22;
        ids[11] = 23;
        ids[12] = 25;
        ids[13] = 29;
        ids[14] = 30;
        ids[15] = 31;
        ids[16] = 33;
        ids[17] = 37;
        ids[18] = 38;
        ids[19] = 39;
        ids[20] = 41;
        ids[21] = 45;
        ids[22] = 46;
        ids[23] = 47;
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSM2P2T56(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[14];
        ids[0] = 3;
        ids[1] = 7;
        ids[2] = 11;
        ids[3] = 15;
        ids[4] = 19;
        ids[5] = 23;
        ids[6] = 27;
        ids[7] = 31;
        ids[8] = 33;
        ids[9] = 39;
        ids[10] = 43;
        ids[11] = 47;
        ids[12] = 51;
        ids[13] = 55;
        int[] conflicts = new int[3];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //GW
        ids = new int[56];
        for(int i = 0; i < 56;i++){
            ids[i] = i;
        }
        conflicts = new int[6];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = GW;
        conflicts[3] = R1;
        conflicts[4] = R2;
        conflicts[5] = GR;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R1
        ids = new int[28];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 8;
        ids[5] = 10;
        ids[6] = 11;
        ids[7] = 12;
        ids[8] = 16;
        ids[9] = 18;
        ids[10] = 19;
        ids[11] = 20;
        ids[12] = 24;
        ids[13] = 26;
        ids[14] = 27;
        ids[15] = 28;
        ids[16] = 32;
        ids[17] = 34;
        ids[18] = 35;
        ids[19] = 36;
        ids[20] = 40;
        ids[21] = 42;
        ids[22] = 43;
        ids[23] = 44;
        ids[24] = 48;
        ids[25] = 50;
        ids[26] = 51;
        ids[27] = 52;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //R2
        ids = new int[28];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 9;
        ids[5] = 13;
        ids[6] = 14;
        ids[7] = 15;
        ids[8] = 17;
        ids[9] = 21;
        ids[10] = 22;
        ids[11] = 23;
        ids[12] = 25;
        ids[13] = 29;
        ids[14] = 30;
        ids[15] = 31;
        ids[16] = 33;
        ids[17] = 37;
        ids[18] = 38;
        ids[19] = 39;
        ids[20] = 41;
        ids[21] = 45;
        ids[22] = 46;
        ids[23] = 47;
        ids[24] = 49;
        ids[25] = 53;
        ids[26] = 54;
        ids[27] = 55;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //W1
        ids = new int[28];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 3;
        ids[3] = 4;
        ids[4] = 8;
        ids[5] = 10;
        ids[6] = 11;
        ids[7] = 12;
        ids[8] = 16;
        ids[9] = 18;
        ids[10] = 19;
        ids[11] = 20;
        ids[12] = 24;
        ids[13] = 26;
        ids[14] = 27;
        ids[15] = 28;
        ids[16] = 32;
        ids[17] = 34;
        ids[18] = 35;
        ids[19] = 36;
        ids[20] = 40;
        ids[21] = 42;
        ids[22] = 43;
        ids[23] = 44;
        ids[24] = 48;
        ids[25] = 50;
        ids[26] = 51;
        ids[27] = 52;
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //W2
        ids = new int[28];
        ids[0] = 1;
        ids[1] = 5;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 9;
        ids[5] = 13;
        ids[6] = 14;
        ids[7] = 15;
        ids[8] = 17;
        ids[9] = 21;
        ids[10] = 22;
        ids[11] = 23;
        ids[12] = 25;
        ids[13] = 29;
        ids[14] = 30;
        ids[15] = 31;
        ids[16] = 33;
        ids[17] = 37;
        ids[18] = 38;
        ids[19] = 39;
        ids[20] = 41;
        ids[21] = 45;
        ids[22] = 46;
        ids[23] = 47;
        ids[24] = 49;
        ids[25] = 53;
        ids[26] = 54;
        ids[27] = 55;
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
    // four shards
    public static WorkStealingClassToThreads[] getWSM2P4T8(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[4];
        ids[0] = 0;
        ids[1] = 4;
        ids[2] = 6;
        ids[3] = 7;
        int[] conflicts = new int[5];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = W3;
        conflicts[3] = W4;
        conflicts[4] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //GW
        ids = new int[8];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 2;
        ids[3] = 3;
        ids[4] = 4;
        ids[5] = 5;
        ids[6] = 6;
        ids[7] = 7;
        conflicts = new int[10];
        conflicts[0] = R1;
        conflicts[1] = R2;
        conflicts[2] = R3;
        conflicts[3] = R4;
        conflicts[4] = W1;
        conflicts[5] = W2;
        conflicts[6] = W3;
        conflicts[7] = W4;
        conflicts[8] = GR;
        conflicts[9] = GW;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //R1
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 4;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //R2
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 6;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //R3
        ids = new int[2];
        ids[0] = 3;
        ids[1] = 5;
        conflicts = new int[2];
        conflicts[0] = W3;
        conflicts[1] = GW;
        cts[R3] = new WorkStealingClassToThreads(R3, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        //R4
        ids = new int[2];
        ids[0] = 1;
        ids[1] = 7;
        conflicts = new int[2];
        conflicts[0] = W4;
        conflicts[1] = GW;
        cts[R4] = new WorkStealingClassToThreads(R4, WorkStealingClassToThreads.CONC, ids, conflicts);
        
        
        
        //W1
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 4;
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //W2
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 6;
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //W3
        ids = new int[2];
        ids[0] = 3;
        ids[1] = 5;
        conflicts = new int[4];
        conflicts[0] = W3;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R3;
        cts[W3] = new WorkStealingClassToThreads(W3, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //W4
        ids = new int[2];
        ids[0] = 1;
        ids[1] = 7;
        conflicts = new int[4];
        conflicts[0] = W4;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R4;
        cts[W4] = new WorkStealingClassToThreads(W4, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSM2P4T16(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[8];
        ids[0] = 0;
        ids[1] = 4;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 9;
        ids[5] = 11;
        ids[6] = 13;
        ids[7] = 15;
        int[] conflicts = new int[5];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = W3;
        conflicts[3] = W4;
        conflicts[4] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //GW
        ids = new int[16];
        for(int i = 0; i < 16;i++){
            ids[i] = i;
        }
        conflicts = new int[10];
        conflicts[0] = R1;
        conflicts[1] = R2;
        conflicts[2] = R3;
        conflicts[3] = R4;
        conflicts[4] = W1;
        conflicts[5] = W2;
        conflicts[6] = W3;
        conflicts[7] = W4;
        conflicts[8] = GR;
        conflicts[9] = GW;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //R1
        ids = new int[4];
        ids[0] = 2;
        ids[1] = 4;
        ids[2] = 10;
        ids[3] = 12;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        //w1
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R2
        ids = new int[4];
        ids[0] = 0;
        ids[1] = 6;
        ids[2] = 8;
        ids[3] = 14;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        //w2
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R3
        ids = new int[4];
        ids[0] = 3;
        ids[1] = 5;
        ids[2] = 11;
        ids[3] = 13;
        conflicts = new int[2];
        conflicts[0] = W3;
        conflicts[1] = GW;
        cts[R3] = new WorkStealingClassToThreads(R3, WorkStealingClassToThreads.CONC, ids, conflicts);
        //w3
        conflicts = new int[4];
        conflicts[0] = W3;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R3;
        cts[W3] = new WorkStealingClassToThreads(W3, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R4
        ids = new int[4];
        ids[0] = 1;
        ids[1] = 7;
        ids[2] = 9;
        ids[3] = 15;
        conflicts = new int[2];
        conflicts[0] = W4;
        conflicts[1] = GW;
        cts[R4] = new WorkStealingClassToThreads(R4, WorkStealingClassToThreads.CONC, ids, conflicts);
        //w4
        conflicts = new int[4];
        conflicts[0] = W4;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R4;
        cts[W4] = new WorkStealingClassToThreads(W4, WorkStealingClassToThreads.SYNC, ids, conflicts);        
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSM2P4T32(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[16];
        ids[0] = 0;
        ids[1] = 4;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 9;
        ids[5] = 11;
        ids[6] = 13;
        ids[7] = 15;
        ids[8] = 18;
        ids[9] = 20;
        ids[10] = 22;
        ids[11] = 23;
        ids[12] = 25;
        ids[13] = 27;
        ids[14] = 29;
        ids[15] = 31;
        int[] conflicts = new int[5];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = W3;
        conflicts[3] = W4;
        conflicts[4] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //GW
        ids = new int[32];
        for(int i = 0; i < 32;i++){
            ids[i] = i;
        }
        conflicts = new int[10];
        conflicts[0] = R1;
        conflicts[1] = R2;
        conflicts[2] = R3;
        conflicts[3] = R4;
        conflicts[4] = W1;
        conflicts[5] = W2;
        conflicts[6] = W3;
        conflicts[7] = W4;
        conflicts[8] = GR;
        conflicts[9] = GW;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //R1
        ids = new int[8];
        ids[0] = 2;
        ids[1] = 4;
        ids[2] = 10;
        ids[3] = 12;
        ids[4] = 18;
        ids[5] = 20;
        ids[6] = 26;
        ids[7] = 28;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        //w1
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R2
        ids = new int[8];
        ids[0] = 0;
        ids[1] = 6;
        ids[2] = 8;
        ids[3] = 14;
        ids[4] = 16;
        ids[5] = 22;
        ids[6] = 24;
        ids[7] = 30;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        //w2
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R3
        ids = new int[8];
        ids[0] = 3;
        ids[1] = 5;
        ids[2] = 11;
        ids[3] = 13;
        ids[4] = 19;
        ids[5] = 21;
        ids[6] = 27;
        ids[7] = 29;
        conflicts = new int[2];
        conflicts[0] = W3;
        conflicts[1] = GW;
        cts[R3] = new WorkStealingClassToThreads(R3, WorkStealingClassToThreads.CONC, ids, conflicts);
        //w3
        conflicts = new int[4];
        conflicts[0] = W3;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R3;
        cts[W3] = new WorkStealingClassToThreads(W3, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R4
        ids = new int[8];
        ids[0] = 1;
        ids[1] = 7;
        ids[2] = 9;
        ids[3] = 15;
        ids[4] = 17;
        ids[5] = 23;
        ids[6] = 25;
        ids[7] = 31;
        conflicts = new int[2];
        conflicts[0] = W4;
        conflicts[1] = GW;
        cts[R4] = new WorkStealingClassToThreads(R4, WorkStealingClassToThreads.CONC, ids, conflicts);
        //w4
        conflicts = new int[4];
        conflicts[0] = W4;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R4;
        cts[W4] = new WorkStealingClassToThreads(W4, WorkStealingClassToThreads.SYNC, ids, conflicts);        
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSM2P4T40(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[20];
        ids[0] = 0;
        ids[1] = 4;
        ids[2] = 6;
        ids[3] = 7;
        ids[4] = 9;
        ids[5] = 11;
        ids[6] = 13;
        ids[7] = 15;
        ids[8] = 18;
        ids[9] = 20;
        ids[10] = 22;
        ids[11] = 23;
        ids[12] = 25;
        ids[13] = 27;
        ids[14] = 29;
        ids[15] = 31;
        ids[16] = 32;
        ids[17] = 36;
        ids[18] = 38;
        ids[19] = 39;
        int[] conflicts = new int[5];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = W3;
        conflicts[3] = W4;
        conflicts[4] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //GW
        ids = new int[40];
        for(int i = 0; i < 40;i++){
            ids[i] = i;
        }
        conflicts = new int[10];
        conflicts[0] = R1;
        conflicts[1] = R2;
        conflicts[2] = R3;
        conflicts[3] = R4;
        conflicts[4] = W1;
        conflicts[5] = W2;
        conflicts[6] = W3;
        conflicts[7] = W4;
        conflicts[8] = GR;
        conflicts[9] = GW;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        
        //R1
        ids = new int[10];
        ids[0] = 2;
        ids[1] = 4;
        ids[2] = 10;
        ids[3] = 12;
        ids[4] = 18;
        ids[5] = 20;
        ids[6] = 26;
        ids[7] = 28;
        ids[8] = 32;
        ids[9] = 33;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        //w1
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R2
        ids = new int[10];
        ids[0] = 0;
        ids[1] = 6;
        ids[2] = 8;
        ids[3] = 14;
        ids[4] = 16;
        ids[5] = 22;
        ids[6] = 24;
        ids[7] = 30;
        ids[8] = 34;
        ids[9] = 35;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        //w2
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R3
        ids = new int[10];
        ids[0] = 3;
        ids[1] = 5;
        ids[2] = 11;
        ids[3] = 13;
        ids[4] = 19;
        ids[5] = 21;
        ids[6] = 27;
        ids[7] = 29;
        ids[8] = 36;
        ids[9] = 37;
        conflicts = new int[2];
        conflicts[0] = W3;
        conflicts[1] = GW;
        cts[R3] = new WorkStealingClassToThreads(R3, WorkStealingClassToThreads.CONC, ids, conflicts);
        //w3
        conflicts = new int[4];
        conflicts[0] = W3;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R3;
        cts[W3] = new WorkStealingClassToThreads(W3, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R4
        ids = new int[10];
        ids[0] = 1;
        ids[1] = 7;
        ids[2] = 9;
        ids[3] = 15;
        ids[4] = 17;
        ids[5] = 23;
        ids[6] = 25;
        ids[7] = 31;
        ids[8] = 38;
        ids[9] = 39;
        conflicts = new int[2];
        conflicts[0] = W4;
        conflicts[1] = GW;
        cts[R4] = new WorkStealingClassToThreads(R4, WorkStealingClassToThreads.CONC, ids, conflicts);
        //w4
        conflicts = new int[4];
        conflicts[0] = W4;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R4;
        cts[W4] = new WorkStealingClassToThreads(W4, WorkStealingClassToThreads.SYNC, ids, conflicts);        
        
        return cts;
    }
    
    
    // six shards
    public static WorkStealingClassToThreads[] getWSP6T12(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[6];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 4;
        ids[3] = 6;
        ids[4] = 8;
        ids[5] = 10;
        int[] conflicts = new int[7];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = W3;
        conflicts[3] = W4;
        conflicts[4] = W5;
        conflicts[5] = W6;
        conflicts[6] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //GW
        ids = new int[12];
        for(int i = 0; i < 12;i++){
            ids[i] = i;
        }
        conflicts = new int[14];
        conflicts[0] = R1;
        conflicts[1] = R2;
        conflicts[2] = R3;
        conflicts[3] = R4;
        conflicts[4] = R5;
        conflicts[5] = R6;
        conflicts[6] = W1;
        conflicts[7] = W2;
        conflicts[8] = W3;
        conflicts[9] = W4;
        conflicts[10] = W5;
        conflicts[11] = W6;
        conflicts[12] = GR;
        conflicts[13] = GW;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R1
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W1
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R2
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 3;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W2
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R3
        ids = new int[2];
        ids[0] = 4;
        ids[1] = 5;
        conflicts = new int[2];
        conflicts[0] = W3;
        conflicts[1] = GW;
        cts[R3] = new WorkStealingClassToThreads(R3, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W3
        conflicts = new int[4];
        conflicts[0] = W3;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R3;
        cts[W3] = new WorkStealingClassToThreads(W3, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R4
        ids = new int[2];
        ids[0] = 6;
        ids[1] = 7;
        conflicts = new int[2];
        conflicts[0] = W4;
        conflicts[1] = GW;
        cts[R4] = new WorkStealingClassToThreads(R4, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W4
        conflicts = new int[4];
        conflicts[0] = W4;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R4;
        cts[W4] = new WorkStealingClassToThreads(W4, WorkStealingClassToThreads.SYNC, ids, conflicts);
    
        //R5
        ids = new int[2];
        ids[0] = 8;
        ids[1] = 9;
        conflicts = new int[2];
        conflicts[0] = W5;
        conflicts[1] = GW;
        cts[R5] = new WorkStealingClassToThreads(R5, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W5
        conflicts = new int[4];
        conflicts[0] = W5;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R5;
        cts[W5] = new WorkStealingClassToThreads(W5, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R6
        ids = new int[2];
        ids[0] = 10;
        ids[1] = 11;
        conflicts = new int[2];
        conflicts[0] = W6;
        conflicts[1] = GW;
        cts[R6] = new WorkStealingClassToThreads(R6, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W6
        conflicts = new int[4];
        conflicts[0] = W6;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R6;
        cts[W6] = new WorkStealingClassToThreads(W6, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
     }
    
    
    // eight shards
    public static WorkStealingClassToThreads[] getWSP8T16(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[8];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 4;
        ids[3] = 6;
        ids[4] = 8;
        ids[5] = 10;
        ids[6] = 12;
        ids[7] = 14;
        int[] conflicts = new int[9];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = W3;
        conflicts[3] = W4;
        conflicts[4] = W5;
        conflicts[5] = W6;
        conflicts[6] = W7;
        conflicts[7] = W8;
        conflicts[8] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //GW
        ids = new int[16];
        for(int i = 0; i < 16;i++){
            ids[i] = i;
        }
        conflicts = new int[18];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = W3;
        conflicts[3] = W4;
        conflicts[4] = W5;
        conflicts[5] = W6;
        conflicts[6] = W7;
        conflicts[7] = W8;
        conflicts[8] = R1;
        conflicts[9] = R2;
        conflicts[10] = R3;
        conflicts[11] = R4;
        conflicts[12] = R5;
        conflicts[13] = R6;
        conflicts[14] = R7;
        conflicts[15] = R8;
        conflicts[16] = GR;
        conflicts[17] = GW;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R1
        ids = new int[2];
        ids[0] = 0;
        ids[1] = 1;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W1
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R2
        ids = new int[2];
        ids[0] = 2;
        ids[1] = 3;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W2
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R3
        ids = new int[2];
        ids[0] = 4;
        ids[1] = 5;
        conflicts = new int[2];
        conflicts[0] = W3;
        conflicts[1] = GW;
        cts[R3] = new WorkStealingClassToThreads(R3, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W3
        conflicts = new int[4];
        conflicts[0] = W3;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R3;
        cts[W3] = new WorkStealingClassToThreads(W3, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R4
        ids = new int[2];
        ids[0] = 6;
        ids[1] = 7;
        conflicts = new int[2];
        conflicts[0] = W4;
        conflicts[1] = GW;
        cts[R4] = new WorkStealingClassToThreads(R4, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W4
        conflicts = new int[4];
        conflicts[0] = W4;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R4;
        cts[W4] = new WorkStealingClassToThreads(W4, WorkStealingClassToThreads.SYNC, ids, conflicts);
    
        //R5
        ids = new int[2];
        ids[0] = 8;
        ids[1] = 9;
        conflicts = new int[2];
        conflicts[0] = W5;
        conflicts[1] = GW;
        cts[R5] = new WorkStealingClassToThreads(R5, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W5
        conflicts = new int[4];
        conflicts[0] = W5;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R5;
        cts[W5] = new WorkStealingClassToThreads(W5, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R6
        ids = new int[2];
        ids[0] = 10;
        ids[1] = 11;
        conflicts = new int[2];
        conflicts[0] = W6;
        conflicts[1] = GW;
        cts[R6] = new WorkStealingClassToThreads(R6, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W6
        conflicts = new int[4];
        conflicts[0] = W6;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R6;
        cts[W6] = new WorkStealingClassToThreads(W6, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R7
        ids = new int[2];
        ids[0] = 12;
        ids[1] = 13;
        conflicts = new int[2];
        conflicts[0] = W7;
        conflicts[1] = GW;
        cts[R7] = new WorkStealingClassToThreads(R7, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W7
        conflicts = new int[4];
        conflicts[0] = W7;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R7;
        cts[W7] = new WorkStealingClassToThreads(W7, WorkStealingClassToThreads.SYNC, ids, conflicts);
    
        //R8
        ids = new int[2];
        ids[0] = 14;
        ids[1] = 15;
        conflicts = new int[2];
        conflicts[0] = W8;
        conflicts[1] = GW;
        cts[R8] = new WorkStealingClassToThreads(R8, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W8
        conflicts = new int[4];
        conflicts[0] = W8;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R8;
        cts[W8] = new WorkStealingClassToThreads(W8, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSP8T32(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[16];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 4;
        ids[3] = 6;
        ids[4] = 8;
        ids[5] = 10;
        ids[6] = 12;
        ids[7] = 14;
        ids[8] = 16;
        ids[9] = 18;
        ids[10] = 20;
        ids[11] = 22;
        ids[12] = 24;
        ids[13] = 26;
        ids[14] = 28;
        ids[15] = 30;
        int[] conflicts = new int[9];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = W3;
        conflicts[3] = W4;
        conflicts[4] = W5;
        conflicts[5] = W6;
        conflicts[6] = W7;
        conflicts[7] = W8;
        conflicts[8] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //GW
        ids = new int[32];
        for(int i = 0; i < 32;i++){
            ids[i] = i;
        }
        conflicts = new int[18];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = W3;
        conflicts[3] = W4;
        conflicts[4] = W5;
        conflicts[5] = W6;
        conflicts[6] = W7;
        conflicts[7] = W8;
        conflicts[8] = R1;
        conflicts[9] = R2;
        conflicts[10] = R3;
        conflicts[11] = R4;
        conflicts[12] = R5;
        conflicts[13] = R6;
        conflicts[14] = R7;
        conflicts[15] = R8;
        conflicts[16] = GR;
        conflicts[17] = GW;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R1
        ids = new int[4];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 16;
        ids[3] = 17;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W1
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R2
        ids = new int[4];
        ids[0] = 2;
        ids[1] = 3;
        ids[2] = 18;
        ids[3] = 19;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W2
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R3
        ids = new int[4];
        ids[0] = 4;
        ids[1] = 5;
        ids[2] = 20;
        ids[3] = 21;
        conflicts = new int[2];
        conflicts[0] = W3;
        conflicts[1] = GW;
        cts[R3] = new WorkStealingClassToThreads(R3, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W3
        conflicts = new int[4];
        conflicts[0] = W3;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R3;
        cts[W3] = new WorkStealingClassToThreads(W3, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R4
        ids = new int[4];
        ids[0] = 6;
        ids[1] = 7;
        ids[2] = 22;
        ids[3] = 23;
        conflicts = new int[2];
        conflicts[0] = W4;
        conflicts[1] = GW;
        cts[R4] = new WorkStealingClassToThreads(R4, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W4
        conflicts = new int[4];
        conflicts[0] = W4;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R4;
        cts[W4] = new WorkStealingClassToThreads(W4, WorkStealingClassToThreads.SYNC, ids, conflicts);
    
        //R5
        ids = new int[4];
        ids[0] = 8;
        ids[1] = 9;
        ids[2] = 24;
        ids[3] = 25;
        conflicts = new int[2];
        conflicts[0] = W5;
        conflicts[1] = GW;
        cts[R5] = new WorkStealingClassToThreads(R5, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W5
        conflicts = new int[4];
        conflicts[0] = W5;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R5;
        cts[W5] = new WorkStealingClassToThreads(W5, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R6
        ids = new int[4];
        ids[0] = 10;
        ids[1] = 11;
        ids[2] = 26;
        ids[3] = 27;
        conflicts = new int[2];
        conflicts[0] = W6;
        conflicts[1] = GW;
        cts[R6] = new WorkStealingClassToThreads(R6, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W6
        conflicts = new int[4];
        conflicts[0] = W6;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R6;
        cts[W6] = new WorkStealingClassToThreads(W6, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R7
        ids = new int[4];
        ids[0] = 12;
        ids[1] = 13;
        ids[2] = 28;
        ids[3] = 29;
        conflicts = new int[2];
        conflicts[0] = W7;
        conflicts[1] = GW;
        cts[R7] = new WorkStealingClassToThreads(R7, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W7
        conflicts = new int[4];
        conflicts[0] = W7;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R7;
        cts[W7] = new WorkStealingClassToThreads(W7, WorkStealingClassToThreads.SYNC, ids, conflicts);
    
        //R8
        ids = new int[4];
        ids[0] = 14;
        ids[1] = 15;
        ids[2] = 30;
        ids[3] = 31;
        conflicts = new int[2];
        conflicts[0] = W8;
        conflicts[1] = GW;
        cts[R8] = new WorkStealingClassToThreads(R8, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W8
        conflicts = new int[4];
        conflicts[0] = W8;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R8;
        cts[W8] = new WorkStealingClassToThreads(W8, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
    public static WorkStealingClassToThreads[] getWSP8T40(){
        WorkStealingClassToThreads[] cts = new WorkStealingClassToThreads[100];
        
        //GR
        int[] ids = new int[20];
        ids[0] = 0;
        ids[1] = 2;
        ids[2] = 4;
        ids[3] = 6;
        ids[4] = 8;
        ids[5] = 10;
        ids[6] = 12;
        ids[7] = 14;
        ids[8] = 16;
        ids[9] = 18;
        ids[10] = 20;
        ids[11] = 22;
        ids[12] = 24;
        ids[13] = 26;
        ids[14] = 28;
        ids[15] = 30;
        ids[16] = 32;
        ids[17] = 34;
        ids[18] = 36;
        ids[19] = 38;
        int[] conflicts = new int[9];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = W3;
        conflicts[3] = W4;
        conflicts[4] = W5;
        conflicts[5] = W6;
        conflicts[6] = W7;
        conflicts[7] = W8;
        conflicts[8] = GW;
        cts[GR] = new WorkStealingClassToThreads(GR, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //GW
        ids = new int[40];
        for(int i = 0; i < 40;i++){
            ids[i] = i;
        }
        conflicts = new int[18];
        conflicts[0] = W1;
        conflicts[1] = W2;
        conflicts[2] = W3;
        conflicts[3] = W4;
        conflicts[4] = W5;
        conflicts[5] = W6;
        conflicts[6] = W7;
        conflicts[7] = W8;
        conflicts[8] = R1;
        conflicts[9] = R2;
        conflicts[10] = R3;
        conflicts[11] = R4;
        conflicts[12] = R5;
        conflicts[13] = R6;
        conflicts[14] = R7;
        conflicts[15] = R8;
        conflicts[16] = GR;
        conflicts[17] = GW;
        cts[GW] = new WorkStealingClassToThreads(GW, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R1
        ids = new int[5];
        ids[0] = 0;
        ids[1] = 1;
        ids[2] = 16;
        ids[3] = 17;
        ids[4] = 32;
        conflicts = new int[2];
        conflicts[0] = W1;
        conflicts[1] = GW;
        cts[R1] = new WorkStealingClassToThreads(R1, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W1
        conflicts = new int[4];
        conflicts[0] = W1;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R1;
        cts[W1] = new WorkStealingClassToThreads(W1, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R2
        ids = new int[5];
        ids[0] = 2;
        ids[1] = 3;
        ids[2] = 18;
        ids[3] = 19;
        ids[4] = 33;
        conflicts = new int[2];
        conflicts[0] = W2;
        conflicts[1] = GW;
        cts[R2] = new WorkStealingClassToThreads(R2, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W2
        conflicts = new int[4];
        conflicts[0] = W2;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R2;
        cts[W2] = new WorkStealingClassToThreads(W2, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R3
        ids = new int[5];
        ids[0] = 4;
        ids[1] = 5;
        ids[2] = 20;
        ids[3] = 21;
        ids[4] = 34;
        conflicts = new int[2];
        conflicts[0] = W3;
        conflicts[1] = GW;
        cts[R3] = new WorkStealingClassToThreads(R3, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W3
        conflicts = new int[4];
        conflicts[0] = W3;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R3;
        cts[W3] = new WorkStealingClassToThreads(W3, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R4
        ids = new int[5];
        ids[0] = 6;
        ids[1] = 7;
        ids[2] = 22;
        ids[3] = 23;
        ids[4] = 35;
        conflicts = new int[2];
        conflicts[0] = W4;
        conflicts[1] = GW;
        cts[R4] = new WorkStealingClassToThreads(R4, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W4
        conflicts = new int[4];
        conflicts[0] = W4;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R4;
        cts[W4] = new WorkStealingClassToThreads(W4, WorkStealingClassToThreads.SYNC, ids, conflicts);
    
        //R5
        ids = new int[5];
        ids[0] = 8;
        ids[1] = 9;
        ids[2] = 24;
        ids[3] = 25;
        ids[4] = 36;
        conflicts = new int[2];
        conflicts[0] = W5;
        conflicts[1] = GW;
        cts[R5] = new WorkStealingClassToThreads(R5, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W5
        conflicts = new int[4];
        conflicts[0] = W5;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R5;
        cts[W5] = new WorkStealingClassToThreads(W5, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R6
        ids = new int[5];
        ids[0] = 10;
        ids[1] = 11;
        ids[2] = 26;
        ids[3] = 27;
        ids[4] = 37;
        conflicts = new int[2];
        conflicts[0] = W6;
        conflicts[1] = GW;
        cts[R6] = new WorkStealingClassToThreads(R6, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W6
        conflicts = new int[4];
        conflicts[0] = W6;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R6;
        cts[W6] = new WorkStealingClassToThreads(W6, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        //R7
        ids = new int[5];
        ids[0] = 12;
        ids[1] = 13;
        ids[2] = 28;
        ids[3] = 29;
        ids[4] = 38;
        conflicts = new int[2];
        conflicts[0] = W7;
        conflicts[1] = GW;
        cts[R7] = new WorkStealingClassToThreads(R7, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W7
        conflicts = new int[4];
        conflicts[0] = W7;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R7;
        cts[W7] = new WorkStealingClassToThreads(W7, WorkStealingClassToThreads.SYNC, ids, conflicts);
    
        //R8
        ids = new int[5];
        ids[0] = 14;
        ids[1] = 15;
        ids[2] = 30;
        ids[3] = 31;
        ids[4] = 39;
        conflicts = new int[2];
        conflicts[0] = W8;
        conflicts[1] = GW;
        cts[R8] = new WorkStealingClassToThreads(R8, WorkStealingClassToThreads.CONC, ids, conflicts);
        //W8
        conflicts = new int[4];
        conflicts[0] = W8;
        conflicts[1] = GW;
        conflicts[2] = GR;
        conflicts[3] = R8;
        cts[W8] = new WorkStealingClassToThreads(W8, WorkStealingClassToThreads.SYNC, ids, conflicts);
        
        return cts;
    }
    
}
