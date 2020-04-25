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
@Table(name = "routing_step")
public class RoutingStep {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "sequenceNr", nullable = false)
    private int sequenceNr;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="routing_id", nullable=false)
    private Routing routing;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="resourceGroupId", nullable=false)
    private ResourceGroup resourceGroup;

    @Column(name = "yield", nullable = false)
    private double yield;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="plant_id", nullable=false)
    private Plant plant;
}
