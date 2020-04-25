package ru.zuma.mipthack.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zuma.mipthack.domain.ResourceGroup;
import ru.zuma.mipthack.domain.ResourceGroupPeriod;
import ru.zuma.mipthack.service.CSVReaderService;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class CSVReaderServiceImpl implements CSVReaderService {
    private static int RESOURCE_GROUP_ID_NUM = 1;
    private static int ID_NUM = 2;
    private static int AVAILABLE_CAPACITY_NUM = 3;
    private static int FREE_CAPACITY_NUM = 4;
    private static int START_NUM = 5;
    private static int HAS_FINITE_CAPACITY_NUM = 6;

    private static String COMMA_DELIMITER = ",";

    private List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(COMMA_DELIMITER);
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    @Override
    public List<ResourceGroupPeriod> readResourceGroupPeriods(byte[] bytes) {
        List<ResourceGroupPeriod> records = new ArrayList<>();

        try (Scanner scanner = new Scanner(new ByteArrayInputStream(bytes))) {
            while (scanner.hasNextLine()) {
                List<String> line = getRecordFromLine(scanner.nextLine());
                records.add(ResourceGroupPeriod.builder()
                    .resourceGroup(new ResourceGroup(line.get(RESOURCE_GROUP_ID_NUM)))
                    .id(line.get(ID_NUM))
                    .availableCapacity(Long.parseLong(line.get(AVAILABLE_CAPACITY_NUM)))
                    .freeCapacity(Long.parseLong(line.get(FREE_CAPACITY_NUM)))
                    .start(LocalDate.parse(line.get(START_NUM)))
                    .hasFiniteCapacity(Boolean.parseBoolean(line.get(HAS_FINITE_CAPACITY_NUM)))
                    .build()
                );
            }
        }

        return records;
    }
}
