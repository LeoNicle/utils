package com.cloud.operation.web.task.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cloud.adapter.client.Request;
import com.cloud.adapter.client.Response;
import com.cloud.operation.web.task.entity.TaskMsgInfo;
import com.cloud.protobuf.ProtobufUtil;

@Service
public class TaskInfoService {
	
	/***
	 * 构造TaskMsgInfo
	 * @param req
	 * @return
	 * @throws IOException
	 */
	public TaskMsgInfo constructorTaskMsgInfo(Request req) throws IOException {
        byte[] message = req.toByteArray();
        List<com.google.protobuf.Message> msgs = ProtobufUtil.parseRequest(message);
        final StringBuffer buf = new StringBuffer("{\n");
        for(com.google.protobuf.Message msg:msgs){
            buf.append(msg.toString()).append("\n");
        }
        TaskMsgInfo msg = new TaskMsgInfo();
        msg.setOperateTime(new Date());
        msg.setOperator(TaskMsgInfo.OPEERATOR_WEB);
        msg.setMessage(message);
        msg.setMessageStr(buf.toString());
        return msg;
    }
	
	
	/***
	 * 构造TaskMsgInfo 用response
	 * @param response
	 * @return
	 */
	public TaskMsgInfo constructorTaskMsgInfo(Response response){
		byte[] message = response.toByteArray();
        List<com.google.protobuf.Message> msgs = ProtobufUtil.parseResponse(message);
        final StringBuffer buf = new StringBuffer("{\n");
        for(com.google.protobuf.Message msg:msgs){
            buf.append(msg.toString()).append("\n");
        }
        TaskMsgInfo msg = new TaskMsgInfo();
        msg.setOperateTime(new Date());
        msg.setOperator(TaskMsgInfo.OPEERATOR_WEB);
        msg.setMessage(message);
        msg.setMessageStr(buf.toString());
        return msg;
	}
  
}
