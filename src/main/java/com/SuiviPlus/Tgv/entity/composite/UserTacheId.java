package com.SuiviPlus.Tgv.entity.composite;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class UserTacheId implements Serializable {
    private UUID tacheId;
    private UUID userId;
}
