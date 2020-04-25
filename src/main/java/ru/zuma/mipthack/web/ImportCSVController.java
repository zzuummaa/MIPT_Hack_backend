package ru.zuma.mipthack.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.zuma.mipthack.domain.COL;
import ru.zuma.mipthack.domain.Product;
import ru.zuma.mipthack.domain.ResourceGroup;
import ru.zuma.mipthack.domain.ResourceGroupPeriod;
import ru.zuma.mipthack.model.out.BaseResponse;
import ru.zuma.mipthack.model.out.ErrorResponse;
import ru.zuma.mipthack.model.out.UploadCSVResponse;
import ru.zuma.mipthack.repository.COLsRepository;
import ru.zuma.mipthack.repository.ProductRepository;
import ru.zuma.mipthack.repository.ResourceGroupPeriodsRepository;
import ru.zuma.mipthack.repository.ResourceGroupsRepository;
import ru.zuma.mipthack.service.CSVReaderService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
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

    public ImportCSVController(CSVReaderService csvReaderService,
                               ResourceGroupPeriodsRepository periodsRepository,
                               ResourceGroupsRepository groupRepository,
                               COLsRepository coLsRepository,
                               ProductRepository productRepository) {
        this.csvReaderService = csvReaderService;
        this.periodsRepository = periodsRepository;
        this.groupRepository = groupRepository;
        this.colsRepository = coLsRepository;
        this.productRepository = productRepository;
    }

    private ResponseEntity<? extends BaseResponse> uploadFileLogic(MultipartFile file, CheckedFunction<ResponseEntity<BaseResponse>> logic) {
        if (!file.isEmpty()) {
            try {
                long starttime = System.currentTimeMillis();
                ResponseEntity<? extends BaseResponse> responseEntity = logic.execute();
                long endtime = System.currentTimeMillis();
                if (responseEntity.getBody() instanceof UploadCSVResponse) {
                    int difTime = (int)(endtime - starttime) / 1000;
                    UploadCSVResponse csvResponse = (UploadCSVResponse) responseEntity.getBody();
                    csvResponse.setWorkingTime(LocalTime.of(difTime / 60 / 60, difTime / 60, difTime));
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
            return new ResponseEntity<>( new UploadCSVResponse(null, (int)periodsCount), HttpStatus.OK);
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
            return new ResponseEntity<>( new UploadCSVResponse(null, (int)colsCount), HttpStatus.OK);
        });
    }
}
