package ru.zuma.mipthack.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zuma.mipthack.domain.COL;
import ru.zuma.mipthack.domain.Product;
import ru.zuma.mipthack.domain.ResourceGroup;
import ru.zuma.mipthack.domain.ResourceGroupPeriod;
import ru.zuma.mipthack.service.CSVReaderService;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.zuma.mipthack.service.impl.CSVReaderServiceImpl.ColsNums.*;
import static ru.zuma.mipthack.service.impl.CSVReaderServiceImpl.ResourcesGroupPeriodNums.*;

@Service
@RequiredArgsConstructor
public class CSVReaderServiceImpl implements CSVReaderService {
    public static class ResourcesGroupPeriodNums {
        static int RESOURCE_GROUP_ID_NUM = 1;
        static int ID_NUM = 2;
        static int AVAILABLE_CAPACITY_NUM = 3;
        static int FREE_CAPACITY_NUM = 4;
        static int START_NUM = 5;
        static int HAS_FINITE_CAPACITY_NUM = 6;
    }

    public static class ColsNums {
        static int COLLALOC_NUM = 1;
        static int QUANTITY_NUM = 2;
        static int MIN_QUANTITY_NUM  = 3;
        static int MAX_QUANTITY_NUM  = 4;
        static int HAS_SALES_BUDGET_RESERVATION_NUM  = 5;
        static int REQUIRES_ORDER_COMBINATION_NUM  = 6;
        static int NR_OF_ACTIVE_ROUTING_CHAIN_UPSTREAM_NUM  = 7;
        static int SELECTED_SHIPPING_SHOP_NUM  = 8;
        static int GP_VIEW_NUM  = 8;
        static int DELIVERY_TYPE_NUM  = 9;
        static int IMG_PLANNED_STATUS_NUM  = 10;
        static int ROUTING_ID_NUM  = 11;
        static int NAME_NUM  = 12;
        static int PRODUCT_ID_NUM  = 13;
        static int PRODUCT_NAME_NUM  = 14;
        static int LATEST_DESIRED_DELIVERY_DATE_NUM  = 15;
        static int PRODUCT_SPECIFICATION_ID_NUM  = 16;
        static int RESOURCE_GROUP_IDS_NUM  = 17;
    }


    private static String COMMA_DELIMITER = ",";
    private static String SEMICOLON_DELIMITER = ";";

    private List<String> getRecordFromLine(String line, String delimiter) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(delimiter);
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    @Override
    public List<COL> readCOLs(byte[] bytes) {
        List<COL> records = new ArrayList<>();

        try (Scanner scanner = new Scanner(new ByteArrayInputStream(bytes))) {
            while (scanner.hasNextLine()) {
                List<String> line = getRecordFromLine(scanner.nextLine(), SEMICOLON_DELIMITER);
                Set<ResourceGroup> resourceGroups = Arrays.stream(line.get(RESOURCE_GROUP_IDS_NUM).split(COMMA_DELIMITER))
                        .map(ResourceGroup::new)
                        .collect(Collectors.toSet());

                records.add(COL.builder()
                    .colAlloc(line.get(COLLALOC_NUM))
                    .quantity(Double.parseDouble(line.get(QUANTITY_NUM)))
                    .minQuantity(Double.parseDouble(line.get(MIN_QUANTITY_NUM)))
                    .maxQuantity(Double.parseDouble(line.get(MAX_QUANTITY_NUM)))
                    .hasSalesBudgetReservation(Boolean.parseBoolean(line.get(HAS_SALES_BUDGET_RESERVATION_NUM)))
                    .requiresOrderCombination(Boolean.parseBoolean(line.get(REQUIRES_ORDER_COMBINATION_NUM)))
                    .nrOfActiveRoutingChainUpstream(Integer.parseInt(line.get(NR_OF_ACTIVE_ROUTING_CHAIN_UPSTREAM_NUM)))
                    .selectedShippingShop(Integer.parseInt(line.get(SELECTED_SHIPPING_SHOP_NUM)))
                    .gpView(line.get(GP_VIEW_NUM))
                    .deliveryType(line.get(DELIVERY_TYPE_NUM))
                    .imgPlannedStatus(line.get(IMG_PLANNED_STATUS_NUM))
                    .routingId(line.get(ROUTING_ID_NUM))
                    .name(line.get(NAME_NUM))
                    .product(new Product(
                            line.get(PRODUCT_ID_NUM),
                            line.get(PRODUCT_NAME_NUM)))
                    .latestDesiredDeliveryDate(line.get(LATEST_DESIRED_DELIVERY_DATE_NUM))
                    .productSpecificationId(line.get(PRODUCT_SPECIFICATION_ID_NUM))
                    .resourceGroup(resourceGroups)
                    .build()
                );
            }
        }

        return records;
    }

    @Override
    public List<ResourceGroupPeriod> readResourceGroupPeriods(byte[] bytes) {
        List<ResourceGroupPeriod> records = new ArrayList<>();

        try (Scanner scanner = new Scanner(new ByteArrayInputStream(bytes))) {
            while (scanner.hasNextLine()) {
                List<String> line = getRecordFromLine(scanner.nextLine(), COMMA_DELIMITER);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                records.add(ResourceGroupPeriod.builder()
                        .resourceGroup(new ResourceGroup(line.get(RESOURCE_GROUP_ID_NUM)))
                        .id(line.get(ID_NUM))
                        .availableCapacity(Long.parseLong(line.get(AVAILABLE_CAPACITY_NUM)))
                        .freeCapacity(Long.parseLong(line.get(FREE_CAPACITY_NUM)))
                        .start(LocalDateTime.parse(line.get(START_NUM), formatter).toLocalDate())
                        .hasFiniteCapacity(Boolean.parseBoolean(line.get(HAS_FINITE_CAPACITY_NUM)))
                        .build()
                );
            }
        }

        return records;
    }

}
