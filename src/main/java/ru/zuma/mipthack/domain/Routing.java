package ru.zuma.mipthack.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "routing")
public class Routing {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    @Column(name = "inputProduct", nullable = false)
    private Product inputProduct;

    @OneToOne(cascade = CascadeType.ALL)
    @Column(name = "outputProduct", nullable = false)
    private Product outputProduct;

    @ManyToOne(cascade = CascadeType.ALL)
    @Column(name = "inputStockingPointId", nullable = false)
    private StokingPoints inputStockingPointId;

    @ManyToOne(cascade = CascadeType.ALL)
    @Column(name = "outputStockingPointId", nullable = false)
    private StokingPoints outputStockingPointId;
}
