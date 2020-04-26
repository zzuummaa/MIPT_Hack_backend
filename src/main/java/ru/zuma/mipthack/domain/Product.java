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
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "name")
    private String name;

//    @OneToMany(mappedBy="product_routings")
//    private Set<Routing> routings;

    public Product(String id) {
        this.id = id;
    }
}
