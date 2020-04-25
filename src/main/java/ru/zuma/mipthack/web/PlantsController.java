package ru.zuma.mipthack.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        ArrayList<PlantData> dataTime1 = new ArrayList<>();
        dataTime1.add(new PlantData(LocalDate.now(), 0));
        dataTime1.add(new PlantData(LocalDate.now().plusDays(1), 30));
        dataTime1.add(new PlantData(LocalDate.now().plusDays(2), 50));
        dataTime1.add(new PlantData(LocalDate.now().plusDays(3), 80));
        dataTime1.add(new PlantData(LocalDate.now().plusDays(4), 90));

        ArrayList<PlantData> dataTime2 = new ArrayList<>();
        dataTime2.add(new PlantData(LocalDate.now(), 10));
        dataTime2.add(new PlantData(LocalDate.now().plusDays(1), 40));
        dataTime2.add(new PlantData(LocalDate.now().plusDays(2), 60));
        dataTime2.add(new PlantData(LocalDate.now().plusDays(3), 70));
        dataTime2.add(new PlantData(LocalDate.now().plusDays(4), 100));

        ArrayList<PlantResponse> plantResponses = new ArrayList<>();
        plantResponses.add(new PlantResponse((long)0, "Конвертерный цех 1", "КЦ-1", dataTime1));
        plantResponses.add(new PlantResponse((long)1, "Конвертерный цех 2", "КЦ-2", dataTime2));

        return plantResponses;
    }

    @GetMapping("/{id}")
    public PlantResponse getPlant(@PathVariable("id") long id) {
        ArrayList<PlantData> dataTime1 = new ArrayList<>();
        dataTime1.add(new PlantData(LocalDate.now(), 0));
        dataTime1.add(new PlantData(LocalDate.now().plusDays(1), 30));
        dataTime1.add(new PlantData(LocalDate.now().plusDays(2), 50));
        dataTime1.add(new PlantData(LocalDate.now().plusDays(3), 80));
        dataTime1.add(new PlantData(LocalDate.now().plusDays(4), 90));
        return new PlantResponse((long)0, "Конвертерный цех 1", "КЦ-1", dataTime1);
    }
}
