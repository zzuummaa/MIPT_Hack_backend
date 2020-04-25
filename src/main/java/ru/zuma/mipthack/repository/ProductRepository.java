package ru.zuma.mipthack.repository;

import org.springframework.data.repository.CrudRepository;
import ru.zuma.mipthack.domain.Product;

public interface ProductRepository extends CrudRepository<Product, String> {
}
