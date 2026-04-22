package com.TgvTeam.SuiviPlus.repository;

import com.TgvTeam.SuiviPlus.entity.UserTask;
import com.TgvTeam.SuiviPlus.entity.composite.UserTaskId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserTaskRepository extends JpaRepository<UserTask, UserTaskId> {

    List<UserTask> findByTaskId(UUID taskId);

    List<UserTask> findByAssigneeId(UUID userId);

    boolean existsByTaskIdAndAssigneeId(UUID taskId, UUID userId);

    void deleteByTaskIdAndAssigneeId(UUID taskId, UUID userId);
}
