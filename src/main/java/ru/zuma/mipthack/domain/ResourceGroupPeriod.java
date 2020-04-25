package ru.zuma.mipthack.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "resource_group_period")
public class ResourceGroupPeriod {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name="resource_group_id", nullable=false)
    private ResourceGroup resourceGroupID;

    @Column(name = "available_capacity", nullable = false)
    private Long availableCapacity;

    @Column(name = "free_capacity", nullable = false)
    private Long freeCapacity;

    @Column(name = "start", nullable = false)
    private LocalDate start;
}
