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
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "inputProduct_id", referencedColumnName = "id")
    private Product inputProduct;

    @ManyToOne
    @JoinColumn(name = "outputProduct_id", referencedColumnName = "id")
    private Product outputProduct;

    @ManyToOne
    @JoinColumn(name="inputStockingPointId", nullable=false)
    private StockingPoint inputStockingPoint;

    @ManyToOne
    @JoinColumn(name="outputStockingPointId", nullable=false)
    private StockingPoint outputStockingPoint;

    public Routing(String id) {
        this.id = id;
    }
}
