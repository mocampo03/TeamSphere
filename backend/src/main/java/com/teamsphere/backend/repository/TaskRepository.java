package com.teamsphere.backend.repository;

import com.teamsphere.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByOrganizationId(Long organizationId);

    List<Task> findByAssignedMemberId(Long memberId);

}