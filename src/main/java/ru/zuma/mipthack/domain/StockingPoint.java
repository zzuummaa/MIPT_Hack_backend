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
public class StockingPoint {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "s_name")
    private String name;

    public StockingPoint(String id) {
        this.id = id;
    }
}
