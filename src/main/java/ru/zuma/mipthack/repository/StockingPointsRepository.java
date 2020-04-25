package ru.zuma.mipthack.repository;

import org.springframework.data.repository.CrudRepository;
import ru.zuma.mipthack.domain.StockingPoint;

public interface StockingPointsRepository extends CrudRepository<StockingPoint, String> {
}
