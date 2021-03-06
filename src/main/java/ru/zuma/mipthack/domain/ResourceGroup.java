package ru.zuma.mipthack.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@Data
@Entity
@Table(name = "resource_group")
public class ResourceGroup {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "full_name")
    private String fullName;

    public ResourceGroup(String id) {
        this.id = id;
    }
}
