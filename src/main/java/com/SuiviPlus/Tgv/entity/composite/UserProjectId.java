package com.SuiviPlus.Tgv.entity.composite;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class UserProjectId implements Serializable {
    private UUID userId;
    private UUID projectId;
}
