/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism;

//import bftsmart.tom.MessageContext;
import bftsmart.tom.core.messages.TOMMessage;

/**
 *
 * @author eduardo
 */
public class MessageContextPair {
        public TOMMessage request;
        public int classId;
        public byte[] operation;
        public int index;
        public byte[] resp;
        public long decisionTime;
        public long recMsgTime;
        public long scheduledTime;
        
        public MessageContextPair(TOMMessage message, int classId, int index, byte[] operation, long decisionTime, long recMsgTime) {
            this.request = message;
            this.classId = classId;
            this.index = index;
            this.operation = operation;
            this.decisionTime = decisionTime;
            this.recMsgTime = recMsgTime;
        }

}
