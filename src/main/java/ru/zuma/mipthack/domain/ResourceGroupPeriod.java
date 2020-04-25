package ru.zuma.mipthack.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "resource_group_period")
public class ResourceGroupPeriod {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "available_capacity", nullable = false)
    private Long availableCapacity;

    @Column(name = "free_capacity", nullable = false)
    private Long freeCapacity;

    @Column(name = "start", nullable = false)
    private LocalDate start;
}
