package ru.zuma.mipthack.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.List;
import java.util.Set;
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

    private final CSVReaderService csvReaderService;
    private final ResourceGroupPeriodsRepository periodsRepository;
    private final ResourceGroupsRepository groupRepository;
    private final COLsRepository colsRepository;
    private final ProductRepository productRepository;
    private final StockingPointsRepository stockingPointsRepository;
    private final RoutingRepository routingRepository;
    private final RoutingStepsRepository routingStepsRepository;

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
            List<Routing> routings = csvReaderService.readRoutings(file.getBytes()).stream().skip(startLine).limit(50_000).collect(Collectors.toList());

            Set<StockingPoint> stockingPoints = routings.stream()
                    .map(Routing::getInputStockingPoint)
                    .collect(Collectors.toSet());
            Set<StockingPoint> outputStockingPoints = routings.stream()
                    .map(Routing::getOutputStockingPoint)
                    .collect(Collectors.toSet());
            stockingPoints.addAll(outputStockingPoints);
            stockingPointsRepository.saveAll(stockingPoints);

            Set<Product> products = routings.stream().map(Routing::getInputProduct).collect(Collectors.toSet());
            Set<Product> outputProducts = routings.stream().map(Routing::getOutputProduct).collect(Collectors.toSet());
            products.addAll(outputProducts);
            productRepository.saveAll(products);

            routingRepository.saveAll(routings);
            return new ResponseEntity<>( new InitDataBaseResponse(null, routings.size()), HttpStatus.OK);
        });
    }

    @PostMapping("/routing_steps")
    public ResponseEntity<? extends BaseResponse> uploadRoutingSteps(@RequestParam("file") MultipartFile file) {
        return uploadFileLogic(file, () -> {
            List<RoutingStep> routingSteps = csvReaderService.readRoutingSteps(file.getBytes());



            long count = Stream.of(routingStepsRepository.saveAll(routingSteps)).count();
            return new ResponseEntity<>( new InitDataBaseResponse(null, (int)count), HttpStatus.OK);
        });
    }
}
