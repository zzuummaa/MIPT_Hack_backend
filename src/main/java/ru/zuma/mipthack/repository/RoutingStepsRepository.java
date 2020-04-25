package ru.zuma.mipthack.repository;

import org.springframework.data.repository.CrudRepository;
import ru.zuma.mipthack.domain.RoutingStep;

public interface RoutingStepsRepository extends CrudRepository<RoutingStep, String> {
}
