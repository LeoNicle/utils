package com.cloud.operation.web.task.message;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.cloud.adapter.client.Request;
import com.cloud.adapter.client.Response;
import com.cloud.operation.web.task.annotation.Message;
import com.cloud.operation.web.task.utils.RabbitMQConnectionManager;

@Component
@Aspect
public class MessageBuilderManager implements Ordered, BeanPostProcessor {
	private static final Logger logger = LoggerFactory.getLogger(MessageBuilderManager.class);
	private Map<String,MessageBuilderMapping> messageHandlerMappings = new HashMap<String,MessageBuilderMapping>();
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if(beanName.contains("nstanceMessageService")){
			System.out.println();
		}
		MessageBuilder ann = AnnotationUtils.findAnnotation( bean.getClass(),MessageBuilder.class);
		if(ann!=null){
			registeryMessageHandler(bean);
		}
		return bean;
	}

	private void registeryMessageHandler(Object bean) {
		Method[] methods = bean.getClass().getDeclaredMethods();
		for(Method method:methods){
			MessageBuilder messageHandler = AnnotationUtils.findAnnotation(method, MessageBuilder.class);
			if(messageHandler==null){
				continue;
			}
			
			String[] messageTypes = messageHandler.value();
			if(messageTypes==null||messageTypes.length==0){
				throw new RuntimeException("must set messageType for ["+method.getName()+"]");
			}
			
			for(String messageType:messageTypes){
				messageHandlerMappings.put(messageType, new MessageBuilderMapping(messageType, bean, method));
			}
		}
	}
	
	@AfterReturning(value = "@annotation(com.cloud.operation.web.task.annotation.Message)", returning = "result")
	public void afterReturning(JoinPoint joinPoint, Object result) throws Exception{
		logger.info("构造消息体，保存任务：" + Thread.currentThread().getId() + "#" + Thread.currentThread().getName());
		
		try{
			MethodSignature ms = (MethodSignature) joinPoint.getSignature();
			String name = AnnotationUtils.findAnnotation(ms.getMethod(), Message.class).name();
			
			MessageBuilderMapping messageHandlerMapping = messageHandlerMappings.get(name);
			
			if(messageHandlerMapping==null){
			    throw new RuntimeException("messageHandlerMapping not registry : "+name);
			}
			Object request = messageHandlerMapping.invoke(joinPoint, result);
			
			List<Object> messages = new ArrayList<Object>();
			if(request instanceof List){
				messages.addAll((Collection<? extends Object>) request);
			}else{
				messages.add(request);
			}
			registryAfterTransactionCommitEvent(messages);
		}catch(Exception e){
			logger.error("构造保存任务和消息异常：" + e.getMessage(), e);
			//throw new StructureTaskAndMessageException("构造保存任务和消息异常：" + e.getMessage(), e);
			throw new RuntimeException("构造保存任务和消息异常：" + e.getMessage(), e);
		}
	}
	
	private void registryAfterTransactionCommitEvent(final List<Object> messages){
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
				for(Object o : messages){
					try {
						if(o instanceof Request){
							RabbitMQConnectionManager.sendMessage((Request)o);
						}
						if(o instanceof Response){
							Response response = (Response)o;
							RabbitMQConnectionManager.sendMessage2Talker(response);
						}
					} catch (IOException e) {
						throw new RuntimeException("系统异常",e);
					}
    			}
            }
		});
	}

	@Override
	public int getOrder() {
		return 4;
	}

}
