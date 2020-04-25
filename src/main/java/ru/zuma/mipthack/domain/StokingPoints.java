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
@Table(name = "stocking_point")
public class StokingPoints {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name")
    private String name;
}
