package co.sorus.plutus.products.services;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import co.sorus.plutus.core.AuditedEntity;

@Entity
@Table(indexes = {
        @Index(columnList = "code", unique = true),
        @Index(columnList = "parent_id"),
        @Index(columnList = "deleted"),
        @Index(columnList = "updatedAt")
})
public class Category extends AuditedEntity {

    public String code;

    public String name;

    @ManyToOne
    public Category parent;

    public boolean deleted;
}
