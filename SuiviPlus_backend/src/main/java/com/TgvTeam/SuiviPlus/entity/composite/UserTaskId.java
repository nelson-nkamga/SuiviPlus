package com.TgvTeam.SuiviPlus.entity.composite;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
@NoArgsConstructor
public class UserTaskId implements Serializable {

    public UserTaskId(UUID userId, UUID taskId) {
        this.userId = userId;
        this.taskId = taskId;
    }

    private UUID userId;
    private UUID taskId;
}


