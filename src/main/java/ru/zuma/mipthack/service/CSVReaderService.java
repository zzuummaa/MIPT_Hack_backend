package ru.zuma.mipthack.service;

import ru.zuma.mipthack.domain.ResourceGroupPeriod;

import java.util.List;

public interface CSVReaderService {
    List<ResourceGroupPeriod> readResourceGroupPeriods(byte[] bytes);
}