package com.cloud.operation.web.task.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author sc
 * @since 2016年2月2日
 */
@SuppressWarnings("serial")
@Entity
@Table(name="task_msg_info")
@JsonIgnoreProperties(value={"hibernateLazyInitializer", "handler", "taskInfo"}) 
public class TaskMsgInfo implements Serializable {
	
	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "assigned")
    @Column(name="uuid", length=32)
    protected String uuid = java.util.UUID.randomUUID().toString();
	
	
    public static final String OPEERATOR_WEB = "1";
    public static final String OPEERATOR_ADAPTER = "2";
    
//    private String taskInfoUuid;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="operate_time")
    private Date operateTime;

    @Column(name="operator")
    private String operator;
    
    @Lob 
    @Basic(fetch = FetchType.LAZY) 
    @Column(name=" message", columnDefinition="CLOB", nullable=true) 
    private byte[] message;
    
    private String messageStr;

	@ManyToOne
	@JoinColumn(name="taskInfoUuid")
	private TaskInfo taskInfo;

//	/**
//     * @return the taskInfoUuid
//     */
//    public String getTaskInfoUuid() {
//        return taskInfoUuid;
//    }
//
//    /**
//     * @param taskInfoUuid the taskInfoUuid to set
//     */
//    public void setTaskInfoUuid(String taskInfoUuid) {
//        this.taskInfoUuid = taskInfoUuid;
//    }
//
    /**
     * @return the operateTime
     */
    public Date getOperateTime() {
        return operateTime;
    }

    /**
     * @param operateTime the operateTime to set
     */
    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    /**
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * @return the message
     */
    public byte[] getMessage() {
        return message;
    }

    /**
     * @param messag the messag to set
     */
    public void setMessage(byte[] messag) {
        this.message = messag;
    }

    /**
     * @return the messageStr
     */
    public String getMessageStr() {
        return messageStr;
    }

    /**
     * @param parseType the parseType to set
     */
    public void setMessageStr(String messageStr) {
        this.messageStr = messageStr;
    }

	public TaskInfo getTaskInfo() {
		return taskInfo;
	}

	public void setTaskInfo(TaskInfo taskInfo) {
		this.taskInfo = taskInfo;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}

