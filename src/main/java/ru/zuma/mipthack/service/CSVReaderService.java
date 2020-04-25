package ru.zuma.mipthack.service;

import ru.zuma.mipthack.domain.COL;
import ru.zuma.mipthack.domain.ResourceGroupPeriod;
import ru.zuma.mipthack.domain.Routing;
import ru.zuma.mipthack.domain.RoutingStep;

import java.util.List;

public interface CSVReaderService {
    List<ResourceGroupPeriod> readResourceGroupPeriods(byte[] bytes);
    List<COL> readCOLs(byte[] bytes);

    List<Routing> readRoutings(byte[] bytes);
    List<RoutingStep> readRoutingSteps(byte[] bytes);
}
