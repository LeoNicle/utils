package com.cloud.operation.web.task.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name="task_info")
@JsonIgnoreProperties(value={"hibernateLazyInitializer", "handler"}) 
@NamedQuery(name="TaskInfo.findAll", query="SELECT t FROM TaskInfo t")
public class TaskInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "assigned")
    @Column(name="uuid", length=32)
    protected String uuid = java.util.UUID.randomUUID().toString();

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        if(uuid==null||uuid.length()==0){
            return;
        }
        this.uuid = uuid;
    }

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_at")
	private Date createdAt;

	@Column(name="error_code")
	private String errorCode;

	@Column(name="error_message")
	private String errorMessage;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="finish_at")
	private Date finishAt;

	@Column(name="resource_type")
	private String resourceType;

	@Column(name="resource_uuid")
	private String resourceUuid;

	@Column(name="resource_name")
	private String resourceName;

	private Integer state;

	@Column(name="task_name")
	private String taskName;
	
	@Column(name="job_id")
	private String jobId;
	@Column(name="ptype")
	private Integer ptype;
	@Column(name="htype")
	private Integer htype;
	@Column(name="task_desc")
	private String taskDesc;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updated_at")
	private Date updatedAt;

	@OneToMany(mappedBy="taskInfo")
	private List<TaskMsgInfo> taskMsgInfos;
	
	private String userUuid;

	@Transient
	private String operatorUserUuid;
	
	
	@Transient
	private Integer progress;
	
	@Resource
	private String organizationUuid;
	

	public TaskInfo() {
	}
	
	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Date getFinishAt() {
		return this.finishAt;
	}

	public void setFinishAt(Date finishAt) {
		this.finishAt = finishAt;
	}

	public String getResourceType() {
		return this.resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getResourceUuid() {
		return this.resourceUuid;
	}

	public void setResourceUuid(String resourceUuid) {
		this.resourceUuid = resourceUuid;
	}

	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getTaskName() {
		return this.taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Date getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	
	public TaskInfo createdAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public TaskInfo errorCode(String errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public TaskInfo errorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}

	public TaskInfo finishAt(Date finishAt) {
		this.finishAt = finishAt;
		return this;
	}

	public TaskInfo state(Integer state) {
		this.state = state;
		return this;
	}


	public TaskInfo taskName(String taskName) {
		this.taskName = taskName;
		return this;
	}


	public TaskInfo updatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @param jobId the jobId to set
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * @return the ptype
     */
    public Integer getPtype() {
        return ptype;
    }

    /**
     * @param ptype the ptype to set
     */
    public void setPtype(Integer ptype) {
        this.ptype = ptype;
    }

    /**
     * @return the htype
     */
    public Integer getHtype() {
        return htype;
    }

    /**
     * @param htype the htype to set
     */
    public void setHtype(Integer htype) {
        this.htype = htype;
    }

    /**
     * @return the taskDesc
     */
    public String getTaskDesc() {
        return taskDesc;
    }

    /**
     * @param taskDesc the taskDesc to set
     */
    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public List<TaskMsgInfo> getTaskMsgInfos() {
		return taskMsgInfos;
	}

	public void setTaskMsgInfos(List<TaskMsgInfo> taskMsgInfos) {
		this.taskMsgInfos = taskMsgInfos;
	}

	public TaskMsgInfo addTaskMsgInfo(TaskMsgInfo taskMsgInfo) {
		getTaskMsgInfos().add(taskMsgInfo);
		taskMsgInfo.setTaskInfo(this);
		return taskMsgInfo;
	}

	public TaskMsgInfo removeTaskMsgInfo(TaskMsgInfo taskMsgInfo) {
		getTaskMsgInfos().remove(taskMsgInfo);
		taskMsgInfo.setTaskInfo(null);
		return taskMsgInfo;
	}
	
    @Transient
	public String getUserUuid() {
		return userUuid;
	}
    
    @Transient
	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}
    
    @Transient
	public Integer getProgress() {
		return progress;
	}
    
    @Transient
	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	

	public String getOperatorUserUuid() {
		return operatorUserUuid;
	}

	public void setOperatorUserUuid(String operatorUserUuid) {
		this.operatorUserUuid = operatorUserUuid;
	}

	public String getOrganizationUuid() {
		return organizationUuid;
	}

	public void setOrganizationUuid(String organizationUuid) {
		this.organizationUuid = organizationUuid;
	}
}