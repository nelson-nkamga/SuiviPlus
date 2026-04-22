package com.TgvTeam.SuiviPlus.repository;

import com.TgvTeam.SuiviPlus.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByCreateurId(UUID userId);

    List<Project> findByMembres_User_Id(UUID userId);

    boolean existsByIdAndCreateurId(UUID projectId, UUID userId);
}
