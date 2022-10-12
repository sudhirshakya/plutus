package co.sorus.plutus.core;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@MappedSuperclass
public class AuditedEntity extends PanacheEntity {

    public long createdAt;
    public long updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = updatedAt = System.currentTimeMillis();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = System.currentTimeMillis();
    }
}
