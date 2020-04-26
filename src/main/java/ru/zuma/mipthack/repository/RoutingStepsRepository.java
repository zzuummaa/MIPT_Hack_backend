package ru.zuma.mipthack.repository;

import org.springframework.data.repository.CrudRepository;
import ru.zuma.mipthack.domain.Plant;
import ru.zuma.mipthack.domain.RoutingStep;

import java.util.ArrayList;
import java.util.List;

public interface RoutingStepsRepository extends CrudRepository<RoutingStep, String> {
    List<RoutingStep> getAllByPlantId(long id);
}
