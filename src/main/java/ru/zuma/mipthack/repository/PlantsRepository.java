package ru.zuma.mipthack.repository;

import org.springframework.data.repository.CrudRepository;
import ru.zuma.mipthack.domain.Plant;

public interface PlantsRepository extends CrudRepository<Plant, Long> {
}
