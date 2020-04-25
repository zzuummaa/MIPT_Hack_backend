package ru.zuma.mipthack.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zuma.mipthack.model.out.PlantData;
import ru.zuma.mipthack.model.out.PlantResponse;

import java.time.LocalDate;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/api/v1/plants")
@RequiredArgsConstructor
public class PlantsController {

    @GetMapping("/")
    public ArrayList<PlantResponse> getPlants() {
        ArrayList<PlantData> dataTime = new ArrayList<>();
        dataTime.add(new PlantData(LocalDate.now(), 20));
        dataTime.add(new PlantData(LocalDate.now().plusDays(1), 50));
        dataTime.add(new PlantData(LocalDate.now().plusDays(2), 80));

        ArrayList<PlantResponse> plantResponses = new ArrayList<>();
        plantResponses.add(new PlantResponse((long)0, "Конвертерный цех 1", "КЦ-1", dataTime));

        return plantResponses;
    }

}