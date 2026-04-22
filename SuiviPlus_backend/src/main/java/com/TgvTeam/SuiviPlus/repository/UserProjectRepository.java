package com.TgvTeam.SuiviPlus.repository;

import com.TgvTeam.SuiviPlus.entity.UserProject;
import com.TgvTeam.SuiviPlus.entity.composite.UserProjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectId> {

    List<UserProject> findByProjectId(UUID projectId);

    List<UserProject> findByUserId(UUID userId);

    boolean existsByProjectIdAndUserId(UUID projectId, UUID userId);

    void deleteByProjectIdAndUserId(UUID projectId, UUID userId);
}