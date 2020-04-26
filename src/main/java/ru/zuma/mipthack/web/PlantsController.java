package ru.zuma.mipthack.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import ru.zuma.mipthack.model.out.BaseResponse;
import ru.zuma.mipthack.model.out.PlantResponse;
import ru.zuma.mipthack.repository.COLsRepository;
import ru.zuma.mipthack.repository.PlantsRepository;
import ru.zuma.mipthack.repository.ResourceGroupPeriodsRepository;
import ru.zuma.mipthack.repository.RoutingStepsRepository;
import ru.zuma.mipthack.utils.TimeConverter;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/plants")
@RequiredArgsConstructor
public class PlantsController {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public ArrayList<PlantResponse> getPlants(@RequestParam(name = "from_time", required = false) Long fromTime,
                                              @RequestParam(name = "to_time", required = false) Long toTime) {


        String query;
        if (fromTime != null && toTime != null) {
            query = "select * from plan_view where  start >= \'"
                    + simpleDateFormat.format(new Date(fromTime)) + "\' and start <= \'" + simpleDateFormat.format(new Date(toTime)) + "\'";
        } else {
            query = "select * from plan_view";
        }

        List<Map<String, Object>> queryRes = jdbcTemplate.queryForList(query);

        Map<Long, List<Map<String, Object>>> plants = new HashMap<>();
        queryRes.forEach(item -> {
            Long key = (Long) item.get("plant_id");
            if (!plants.containsKey(key)) plants.put(key, new ArrayList<>());
            plants.get(key).add(item);
        });

        ArrayList<PlantResponse> plantResponses = new ArrayList<>();
        plants.keySet().forEach(item -> {
            PlantResponse plantResponse = new PlantResponse();
            plantResponse.setId(item);
            plantResponse.setFullName((String) plants.get(item).get(0).get("description"));
            plantResponse.setTimeData(plants.get(item));
            plantResponses.add(plantResponse);
        });

//        ArrayList<PlantResponse> plantResponses = new ArrayList<>();
//        plantsRepository.findAll().forEach(item -> {
//            ArrayList<PlantData> plantDatas = new ArrayList<>();
//            item.getCol().forEach(element -> {
//                resourceGroupPeriodsRepository.findById(element.getResourceGroup().iterator().next().getId()).ifPresent(it -> {
//                    plantDatas.add(new PlantData (
//                        it.getStart(),
//                        it.getPercentage(),
//                        it.isHasFiniteCapacity()
//                    ));
//                });
//            });
//            plantResponses.add(new PlantResponse(item.getId(), item.getPlantName(), item.getDescription(), plantDatas));
//        });

//        ArrayList<PlantData> dataTime1 = new ArrayList<>();
//        dataTime1.add(new PlantData(LocalDate.now().plusDays(0), 0 , false));
//        dataTime1.add(new PlantData(LocalDate.now().plusDays(1), 30, false));
//        dataTime1.add(new PlantData(LocalDate.now().plusDays(2), 50, false));
//        dataTime1.add(new PlantData(LocalDate.now().plusDays(3), 80, false));
//        dataTime1.add(new PlantData(LocalDate.now().plusDays(4), 90, false));
//
//        ArrayList<PlantData> dataTime2 = new ArrayList<>();
//        dataTime2.add(new PlantData(LocalDate.now().plusDays(0), 10 , false));
//        dataTime2.add(new PlantData(LocalDate.now().plusDays(1), 40 , true));
//        dataTime2.add(new PlantData(LocalDate.now().plusDays(2), 60 , false));
//        dataTime2.add(new PlantData(LocalDate.now().plusDays(3), 70 , false));
//        dataTime2.add(new PlantData(LocalDate.now().plusDays(4), 100, false));
//
//        ArrayList<PlantResponse> plantResponses = new ArrayList<>();
//        plantResponses.add(new PlantResponse((long)0, "Конвертерный цех 1", "КЦ-1", dataTime1));
//        plantResponses.add(new PlantResponse((long)1, "Конвертерный цех 2", "КЦ-2", dataTime2));

//        return plantResponses;
        return plantResponses;
    }

    @GetMapping("/{id}")
    public ResponseEntity<? extends BaseResponse> getPlant(@RequestParam(name = "from_time", required = false) Long fromTime,
                                                           @RequestParam(name = "to_time", required = false) Long toTime,
                                                           @PathVariable("id") long id) {
        String query;
        if (fromTime != null && toTime != null) {
            query = "select * from plan_view where plant_id = " + id + " and start >= \'"
                    + simpleDateFormat.format(new Date(fromTime)) + "\' and start <= \'" + simpleDateFormat.format(new Date(toTime)) + "\'";
        } else {
            query = "select * from plan_view where plant_id = " + id;
        }

        List<Map<String, Object>> queryRes = jdbcTemplate.queryForList(query);

        if (queryRes.size() == 0) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        PlantResponse plantResponse = new PlantResponse();
        plantResponse.setId((Long) queryRes.get(0).get("plant_id"));
        plantResponse.setFullName((String) queryRes.get(0).get("description"));
        plantResponse.setTimeData(new ArrayList<>());

        queryRes.forEach(item -> {
            item.remove("plant_id");
            item.remove("description");
            plantResponse.getTimeData().add(item);
        });

        return new ResponseEntity<>(plantResponse, HttpStatus.OK);

//        PlantResponse[] plantResponses = new PlantResponse[1];
//        plantsRepository.findById(id).ifPresent(item -> {
//            ArrayList<PlantData> plantDatas = new ArrayList<>();
//            item.getCol().forEach(
//            });
//            plantResponses[0] = new PlantResponse(item.getId(), item.getPlantName(), item.getDescription(), plantDatas);
//        });

//        if (plantResponses[0] == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } else {
//            return new ResponseEntity<>(plantResponses[0], HttpStatus.OK);
//        }
//        ArrayList<PlantData> dataTime1 = new ArrayList<>();
//        dataTime1.add(new PlantData(LocalDate.now().plusDays(0), 0 , false));
//        dataTime1.add(new PlantData(LocalDate.now().plusDays(1), 30, false));
//        dataTime1.add(new PlantData(LocalDate.now().plusDays(2), 50, false));
//        dataTime1.add(new PlantData(LocalDate.now().plusDays(3), 80, false));
//        dataTime1.add(new PlantData(LocalDate.now().plusDays(4), 90, false));
//        return new PlantResponse((long)0, "Конвертерный цех 1", "КЦ-1", dataTime1);
    }
}
