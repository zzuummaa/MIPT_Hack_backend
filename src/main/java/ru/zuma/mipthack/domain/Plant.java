package ru.zuma.mipthack.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plant")
public class Plant {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "plant_name")
    private String plantName;

    @Column(name = "description")
    private String description;

    public Plant(Long id) {
        this.id = id;
    }
}
