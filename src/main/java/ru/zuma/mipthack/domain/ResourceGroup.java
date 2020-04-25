package ru.zuma.mipthack.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@Data
@Entity
@Accessors(chain = true)
@Table(name = "resource_group")
public class ResourceGroup {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "full_name")
    private String fullName;

    public ResourceGroup(String id) {
        this.id = id;
    }
}
