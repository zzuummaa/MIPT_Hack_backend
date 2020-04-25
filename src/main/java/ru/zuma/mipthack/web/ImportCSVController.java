package ru.zuma.mipthack.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.zuma.mipthack.domain.ResourceGroupPeriod;
import ru.zuma.mipthack.model.out.BaseResponse;
import ru.zuma.mipthack.model.out.ErrorResponse;
import ru.zuma.mipthack.repository.ResourceGroupPeriodsRepository;
import ru.zuma.mipthack.service.CSVReaderService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/upload")
public class ImportCSVController {

    private final CSVReaderService csvReaderService;
    private ResourceGroupPeriodsRepository repository;

    public ImportCSVController(CSVReaderService csvReaderService) {
        this.csvReaderService = csvReaderService;
    }

    @PostMapping("/resource_group_periods")
    public ResponseEntity<BaseResponse> handleFileUpload(@RequestParam("file") MultipartFile file){
        if (!file.isEmpty()) {
            try {
                List<ResourceGroupPeriod> periods = csvReaderService.readResourceGroupPeriods(file.getBytes());
                repository.save(periods);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (IOException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                return new ResponseEntity<>(new ErrorResponse("Вам не удалось загрузить => " + System.lineSeparator() + sw.toString()), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(new ErrorResponse("Вам не удалось загрузить потому что файл пустой."), HttpStatus.BAD_REQUEST);
        }
    }
}
