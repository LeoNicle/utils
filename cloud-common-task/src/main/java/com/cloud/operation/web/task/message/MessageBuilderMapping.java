package com.cloud.operation.web.task.message;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;

public class MessageBuilderMapping {
	private String messageType;
	private Object bean;
	private Method method;
	
	public MessageBuilderMapping(String messageType, Object bean, Method method) {
		this.messageType = messageType;
		this.bean = bean;
		this.method = method;
	}
	
	public Object invoke(JoinPoint joinPoint, Object result){
		Object[] args;
		if(method.getParameterTypes().length>1){
			args = new Object[]{joinPoint,result};
		}else{
			args = new Object[]{joinPoint};
		}
		try {
			return method.invoke(bean, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	public String getMessageType() {
		return messageType;
	}
}
