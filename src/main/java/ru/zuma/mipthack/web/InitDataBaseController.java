package ru.zuma.mipthack.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zuma.mipthack.domain.Plant;
import ru.zuma.mipthack.model.out.BaseResponse;
import ru.zuma.mipthack.model.out.InitDataBaseResponse;
import ru.zuma.mipthack.repository.PlantsRepository;
import ru.zuma.mipthack.utils.TimeConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/init_db")
public class InitDataBaseController {

    private final PlantsRepository plantsRepository;

    @PostMapping("/")
    public BaseResponse initDataBase() {
        List<Plant> plants = new ArrayList<>();
        plants.add(new Plant(6L, "КЦ-1", "Конвертерный цех 1"));
        plants.add(new Plant(7L, "КЦ-2", "Конвертерный цех 2"));
        plants.add(new Plant(12L, "ЦГП", "Цех горячего проката"));
        plants.add(new Plant(15L, "ЦДС", "Цех динамной стали"));
        plants.add(new Plant(11L, "ЦТС", "Цех трансформаторной стали"));
        plants.add(new Plant(13L, "ЦХПП", "Цех холодного проката и покрытий"));

        long startTime = System.currentTimeMillis();
        int count = (int)Stream.of(plantsRepository.saveAll(plants)).count();
        long endTime = System.currentTimeMillis();

        return new InitDataBaseResponse(TimeConverter.fromMS(endTime - startTime), count);
    }
}
