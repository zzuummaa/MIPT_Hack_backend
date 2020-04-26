package ru.zuma.mipthack.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.zuma.mipthack.domain.*;
import ru.zuma.mipthack.model.out.BaseResponse;
import ru.zuma.mipthack.model.out.ErrorResponse;
import ru.zuma.mipthack.model.out.InitDataBaseResponse;
import ru.zuma.mipthack.repository.*;
import ru.zuma.mipthack.service.CSVReaderService;
import ru.zuma.mipthack.utils.TimeConverter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/upload")
public class ImportCSVController {

    @FunctionalInterface
    public interface CheckedFunction<R> {
        R execute() throws IOException;
    }

    <T> T mergeFunction(T a, T b) {
        return a;
    }

    private final CSVReaderService csvReaderService;
    private final ResourceGroupPeriodsRepository periodsRepository;
    private final ResourceGroupsRepository groupRepository;
    private final COLsRepository colsRepository;
    private final ProductRepository productRepository;
    private final StockingPointsRepository stockingPointsRepository;
    private final RoutingRepository routingRepository;
    private final RoutingStepsRepository routingStepsRepository;
    private final PlantsRepository plantsRepository;

    private ResponseEntity<? extends BaseResponse> uploadFileLogic(MultipartFile file, CheckedFunction<ResponseEntity<BaseResponse>> logic) {
        if (!file.isEmpty()) {
            try {
                long startTime = System.currentTimeMillis();
                ResponseEntity<? extends BaseResponse> responseEntity = logic.execute();
                long endTime = System.currentTimeMillis();
                if (responseEntity.getBody() instanceof InitDataBaseResponse) {
                    InitDataBaseResponse csvResponse = (InitDataBaseResponse) responseEntity.getBody();
                    csvResponse.setWorkingTime(TimeConverter.fromMS(endTime - startTime));
                }
                return responseEntity;
            } catch (Exception e) {
                e.printStackTrace();
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                return new ResponseEntity<>(new ErrorResponse("Вам не удалось загрузить => " + System.lineSeparator() + sw.toString()), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(new ErrorResponse("Вам не удалось загрузить потому что файл пустой."), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/resource_group_periods")
    public ResponseEntity<? extends BaseResponse> uploadGroupPeriods(@RequestParam("file") MultipartFile file) {
        return uploadFileLogic(file, () -> {
            List<ResourceGroupPeriod> periods = csvReaderService.readResourceGroupPeriods(file.getBytes());
            Set<ResourceGroup> groups = periods.stream().map(ResourceGroupPeriod::getResourceGroup).collect(Collectors.toSet());
            groupRepository.saveAll(groups);
            long periodsCount = Stream.of(periodsRepository.saveAll(periods)).count();
            return new ResponseEntity<>( new InitDataBaseResponse(null, (int)periodsCount), HttpStatus.OK);
        });
    }

    @PostMapping("/cols")
    public ResponseEntity<? extends BaseResponse> uploadCOLs(@RequestParam("file") MultipartFile file) {
        return uploadFileLogic(file, () -> {
            List<COL> cols = csvReaderService.readCOLs(file.getBytes());

            Set<ResourceGroup> groups = cols.stream().flatMap((item) -> item.getResourceGroup().stream()).collect(Collectors.toSet());
            groupRepository.saveAll(groups);

            Set<Product> products = cols.stream().map(COL::getProduct).collect(Collectors.toSet());
            productRepository.saveAll(products);

            long colsCount = Stream.of(colsRepository.saveAll(cols)).count();
            return new ResponseEntity<>( new InitDataBaseResponse(null, (int)colsCount), HttpStatus.OK);
        });
    }

    @PostMapping("/routings")
    public ResponseEntity<? extends BaseResponse> uploadRoutings(@RequestParam("start_line") int startLine, @RequestParam("file") MultipartFile file) {
        return uploadFileLogic(file, () -> {
            List<Routing> routingsAll = csvReaderService.readRoutings(file.getBytes()).stream().skip(startLine).collect(Collectors.toList());

            int routingOldSize = routingsAll.size();
            long startTime = System.currentTimeMillis();
            while (routingsAll.size() > 0 && System.currentTimeMillis() - startTime < 1000 * 60 * 10) {
                Map<String, Routing> routings = routingsAll.stream().limit(1000).collect(Collectors.toMap(Routing::getId, Function.identity(), this::mergeFunction));
                routingsAll.removeAll(routings.values());

                Set<StockingPoint> stockingPoints = routings.values().stream()
                        .map(Routing::getInputStockingPoint)
                        .collect(Collectors.toSet());
                Set<StockingPoint> outputStockingPoints = routings.values().stream()
                        .map(Routing::getOutputStockingPoint)
                        .collect(Collectors.toSet());
                stockingPoints.addAll(outputStockingPoints);
                stockingPointsRepository.saveAll(stockingPoints);

                Map<String, Product> products = routings.values().stream()
                        .map(Routing::getInputProduct)
                        .collect(Collectors.toMap(Product::getId, Function.identity(), this::mergeFunction));
                Map<String, Product> outputProducts = routings.values().stream()
                        .map(Routing::getOutputProduct)
                        .collect(Collectors.toMap(Product::getId, Function.identity(), this::mergeFunction));
                products.putAll(outputProducts);
                productRepository.findAllById(products.keySet()).forEach((item) -> { products.remove(item.getId()); });
                productRepository.saveAll(products.values());


                routingRepository.findAllById(routings.keySet()).forEach((item) -> { routings.remove(item.getId()); });
                routingRepository.saveAll(routings.values());
            }

            return new ResponseEntity<>( new InitDataBaseResponse(null, routingOldSize - routingsAll.size()), HttpStatus.OK);
        });
    }

    @PostMapping("/routing_steps")
    public ResponseEntity<? extends BaseResponse> uploadRoutingSteps(@RequestParam("start_line") int startLine, @RequestParam("file") MultipartFile file) {
        return uploadFileLogic(file, () -> {
            List<RoutingStep> routingStepsAll = csvReaderService.readRoutingSteps(file.getBytes()).stream().skip(startLine).collect(Collectors.toList());

            int routingStepsOldSize = routingStepsAll.size();
            long startTime = System.currentTimeMillis();
            while (routingStepsAll.size() > 0 && System.currentTimeMillis() - startTime < 1000 * 60 * 10) {
                Map<String, RoutingStep> routingSteps = routingStepsAll.stream().limit(1000).collect(Collectors.toMap(RoutingStep::getId, Function.identity(), this::mergeFunction));
                routingStepsAll.removeAll(routingSteps.values());

                Map<Long, Plant> plants = routingSteps.values().stream()
                        .map(RoutingStep::getPlant)
                        .collect(Collectors.toMap(Plant::getId, Function.identity(), this::mergeFunction));

                plantsRepository.findAllById(plants.keySet()).forEach(item -> plants.remove(item.getId()));
                plantsRepository.saveAll(plants.values());

                Map<String, Routing> routings = routingSteps.values().stream().map(RoutingStep::getRouting).collect(Collectors.toMap(Routing::getId, Function.identity(), this::mergeFunction));
                routingRepository.findAllById(routings.keySet()).forEach(element -> routings.remove(element.getId()));
                routingRepository.saveAll(routings.values());

                Map<String, ResourceGroup> resourceGroups = routingSteps.values().stream().map(RoutingStep::getResourceGroup).collect(Collectors.toMap(ResourceGroup::getId, Function.identity(), this::mergeFunction));
                groupRepository.findAllById(resourceGroups.keySet()).forEach(element -> resourceGroups.remove(element.getId()));
                groupRepository.saveAll(resourceGroups.values());

                routingStepsRepository.findAllById(routingSteps.keySet()).forEach(item -> routingSteps.remove(item.getId()));
                routingStepsRepository.saveAll(routingSteps.values());
            }

            return new ResponseEntity<>( new InitDataBaseResponse(null, routingStepsOldSize - routingStepsAll.size()), HttpStatus.OK);
        });
    }

    @PostMapping("/routing_steps/plants")
    public ResponseEntity<? extends BaseResponse> uploadRoutingStepsPlants(@RequestParam("start_line") int startLine, @RequestParam("file") MultipartFile file) {
        return uploadFileLogic(file, () -> {
            List<RoutingStep> routingSteps = csvReaderService.readRoutingSteps(file.getBytes());

            Map<Long, Plant> plants = routingSteps.stream().map(RoutingStep::getPlant).collect(Collectors.toMap(Plant::getId, Function.identity(), this::mergeFunction));
            plantsRepository.findAllById(plants.keySet()).forEach(item -> plants.remove(item.getId()));
            plantsRepository.saveAll(plants.values());

            return new ResponseEntity<>( new InitDataBaseResponse(null, plants.size()), HttpStatus.OK);
        });
    }

    @PostMapping("/routing_steps/routings")
    public ResponseEntity<? extends BaseResponse> uploadRoutingStepsRoutings(@RequestParam("file") MultipartFile file) {
        return uploadFileLogic(file, () -> {
            List<RoutingStep> routingSteps = csvReaderService.readRoutingSteps(file.getBytes());

            Map<String, Routing> routings = routingSteps.stream().map(RoutingStep::getRouting).collect(Collectors.toMap(Routing::getId, Function.identity(), this::mergeFunction));
            routingRepository.findAllById(routings.keySet()).forEach(element -> routings.remove(element.getId()));
            routingRepository.saveAll(routings.values().stream().limit(32000).collect(Collectors.toList()));

            return new ResponseEntity<>( new InitDataBaseResponse(null, routings.size()), HttpStatus.OK);
        });
    }

    @Transactional
    @PostMapping("/routing_steps/resourceGroups")
    public ResponseEntity<? extends BaseResponse> uploadRoutingStepsResourceGroups(@RequestParam("start_line") int start_line, @RequestParam("file") MultipartFile file) {
        return uploadFileLogic(file, () -> {
            List<RoutingStep> routingSteps = csvReaderService.readRoutingSteps(file.getBytes());

            Map<String, ResourceGroup> resourceGroups = routingSteps.stream().map(RoutingStep::getResourceGroup).skip(start_line).limit(32000).collect(Collectors.toMap(ResourceGroup::getId, Function.identity(), this::mergeFunction));
            groupRepository.findAllById(resourceGroups.keySet()).forEach(element -> resourceGroups.remove(element.getId()));
            groupRepository.saveAll(resourceGroups.values());

            return new ResponseEntity<>( new InitDataBaseResponse(null, resourceGroups.size()), HttpStatus.OK);
        });
    }
}
