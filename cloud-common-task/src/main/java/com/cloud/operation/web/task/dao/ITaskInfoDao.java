package com.cloud.operation.web.task.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import com.cloud.operation.web.db.MyJpaRepository;
import com.cloud.operation.web.task.entity.TaskInfo;

/**
 * 任务DAO
 */
public interface ITaskInfoDao extends MyJpaRepository<TaskInfo, String>{
	
	@Query(value="SELECT c.uuid, c.foreign_ref, c.ptype, c.htype, s.project_admin, s.project_password FROM cluster c LEFT JOIN vpc_cluster_relation v ON c.uuid = v.cluster_uuid left join hypervisor_server_container s on c.hypervisor_server_container_uuid = s.uuid WHERE v.vpc_uuid = ?1" , nativeQuery=true)
	List<Object[]> findConfigInfo(String vpcUuid);
}
