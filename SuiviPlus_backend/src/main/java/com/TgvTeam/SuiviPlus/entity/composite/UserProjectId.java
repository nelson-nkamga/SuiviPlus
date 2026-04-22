package com.TgvTeam.SuiviPlus.entity.composite;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
public class UserProjectId implements Serializable {

    private UUID userId;
    private UUID projectId;
}
