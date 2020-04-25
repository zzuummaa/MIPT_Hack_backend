package ru.zuma.mipthack.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "col")
public class COL {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "col_alloc", nullable = false)
    private String colAlloc;

    @Column(name = "quantity", nullable = false)
    private double quantity;

    @Column(name = "min_quantity", nullable = false)
    private double minQuantity;

    @Column(name = "max_quantity", nullable = false)
    private double maxQuantity;

    @Column(name = "has_sales_budget_reservation", nullable = false)
    private boolean hasSalesBudgetReservation;

    @Column(name = "requires_order_combination", nullable = false)
    private boolean requiresOrderCombination;

    @Column(name = "nr_of_active_routing_chain_up_stream", nullable = false)
    private int nrOfActiveRoutingChainUpstream;

    @Column(name = "selectedShippingShop", nullable = false)
    private int selectedShippingShop;

    @Column(name = "gp_view", nullable = false)
    private String gpView;

    @Column(name = "delivery_type", nullable = false)
    private String deliveryType;

    @Column(name = "imgPlannedStatus", nullable = false)
    private String imgPlannedStatus;

    @Column(name = "routingId", nullable = false)
    private String routingId;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name="product", nullable=false)
    private Product product;

    @Column(name = "latestDesiredDeliveryDate", nullable = false)
    private String latestDesiredDeliveryDate;

    @Column(name = "productSpecificationId", nullable = false)
    private String productSpecificationId;

    @ManyToMany
    @Column(name = "resourceGroup", nullable = false)
    private Set<ResourceGroup> resourceGroup;
}
