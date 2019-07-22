package com.cloud.operation.web.task.utils;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cloud.adapter.client.Request;
import com.cloud.adapter.client.Response;
import com.cloud.adapter.client.mq.MessageRouter;
import com.cloud.protobuf.AdapterProtos.Message;
import com.cloud.protobuf.ProtobufUtil;
import com.google.protobuf.InvalidProtocolBufferException;

public class RabbitMQConnectionManager {

    public static final Logger logger = LoggerFactory.getLogger(RabbitMQConnectionManager.class);
    
    private static MessageRouter sender;
    
    public static void sendMessage2Adapter(Request request){
        sender.sendToAdapter(request);
    }
    
    public static void sendMessage2Talker(Response response){
        sender.sendToTalker(response);
    }
    
    public static void sendMessage(Request req) throws IOException {
    	logger.info("send message request:{" + req.toString() + "}");
        byte[] message = req.toByteArray();
        List<com.google.protobuf.Message> msgs = ProtobufUtil.parseRequest(message);
        final StringBuffer buf = new StringBuffer("{\n");
        for(com.google.protobuf.Message msg:msgs){
            buf.append(msg.toString()).append("\n");
        }
        String ptype = req.getPlatformCode();
        String htype = req.getHypervisorCode();
        sendMessage(ptype,htype,req.getId(),message,buf.toString());
    }
    
    private static void sendMessage(String ptype,String htype,String taskId,byte[] dataMessage,String messageStr) throws IOException {
        sender.sendToAdapter(ptype, htype, dataMessage);
    }

    /**
     * @param dataMessage
     * @throws IOException
     */
    public static void sendMessage2Adapter(byte[] dataMessage) throws IOException {
        //sendMessage(queueName,dataMessage);
        String ptype=null;
        String htype = null;
        try{
            Message message = Message.parseFrom(dataMessage);
            ptype = String.valueOf(message.getPtype().getNumber());
            htype = String.valueOf(message.getHtype().getNumber());
        } catch (InvalidProtocolBufferException e) {//按新框架处理
            Request request = Request.parseRequest(dataMessage);
            ptype = request.getPlatformCode();
            htype = request.getHypervisorCode();
        } catch (Exception e){
            throw new RuntimeException("not valid message,cann't resend message!!");
        }
        
        sender.sendToAdapter(ptype, htype, dataMessage);
    }
    
    public static void sendMessage2Talker(byte[] dataMessage) throws IOException {
        //sendMessage(talkerQueueName,dataMessage);
        sender.sendToTalker(dataMessage);
    }
    
    public void setMessageRouter(MessageRouter sender) {
        RabbitMQConnectionManager.sender = sender;
    }
}
