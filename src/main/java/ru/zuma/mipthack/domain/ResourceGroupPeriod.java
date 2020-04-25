package ru.zuma.mipthack.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resource_group_period")
public class ResourceGroupPeriod {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name="resource_group_id", nullable=false)
    private ResourceGroup resourceGroup;

    @Column(name = "available_capacity", nullable = false)
    private Long availableCapacity;

    @Column(name = "free_capacity", nullable = false)
    private Long freeCapacity;

    @Column(name = "start", nullable = false)
    private LocalDate start;

    @Column(name = "has_finite_capacity", nullable = false)
    private boolean hasFiniteCapacity;
}
