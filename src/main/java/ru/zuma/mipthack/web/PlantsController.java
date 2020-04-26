package ru.zuma.mipthack.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import ru.zuma.mipthack.model.out.AveragePercentageResponse;
import ru.zuma.mipthack.model.out.BaseResponse;
import ru.zuma.mipthack.model.out.PlantResponse;
import ru.zuma.mipthack.model.out.ResourceInfoResponse;
import ru.zuma.mipthack.repository.ResourceGroupPeriodsRepository;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/plants")
@RequiredArgsConstructor
public class PlantsController {

    private final ResourceGroupPeriodsRepository resourceGroupPeriodsRepository;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public ArrayList<PlantResponse> getPlants(@RequestParam(name = "from_time", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromTime,
                                              @RequestParam(name = "to_time", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date  toTime    ) {


        String query;
        if (fromTime != null && toTime != null) {
            query = "select description, plant_id, avg(percent) percent, start from plan_view where  start >= \'"
                    + fromTime + "\' and start <= \'" + toTime + "\' group by description, plant_id, start order by start";
            System.out.println(query);
        } else {
            query = "select description, plant_id, avg(percent) percent, start from plan_view group by description, plant_id, start order by start";
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

        return plantResponses;
    }

    @GetMapping("/{id}")
    public ResponseEntity<? extends BaseResponse> getPlant(@RequestParam(name = "from_time", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromTime,
                                                           @RequestParam(name = "to_time", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date  toTime,
                                                           @PathVariable("id") long id) {
        String query;
        if (fromTime != null && toTime != null) {
            query = "select * from plan_view where plant_id = " + id + " and start >= \'"
                    + fromTime + "\' and start <= \'" + toTime + "\'";
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
    }

    @GetMapping("/{id}/group")
    public ResponseEntity<? extends BaseResponse> getResourceInfo(@PathVariable("id") long id,
                                                                  @RequestParam(name = "time") @DateTimeFormat(pattern = "yyyy-MM-dd") Date time) {

        String query;
        if (time != null) {
            query = "select * from plan_view where plant_id = " + id + " and start = \'" + time + "\'";
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Map<String, Object>> queryRes = jdbcTemplate.queryForList(query);

        ResourceInfoResponse resourceInfoResponse = new ResourceInfoResponse();
        resourceInfoResponse.setAverage(queryRes.stream()
                .map(it -> ((BigDecimal)it.get("percent")).doubleValue() / queryRes.size())
                .mapToDouble(Double::doubleValue).sum());
        resourceInfoResponse.setTimeData(new ArrayList<>());
        queryRes.forEach(it -> {
            Map<String, Object> map = new HashMap<>();
            map.put("percent", it.get("percent"));
            map.put("resource_group_id", it.get("resource_group_id"));
            resourceInfoResponse.getTimeData().add(map);
        });

        return new ResponseEntity<>(resourceInfoResponse, HttpStatus.OK);
    }

    @GetMapping("/average")
    public ResponseEntity<? extends BaseResponse> getAverage(@RequestParam(name = "from_time", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromTime,
                                                             @RequestParam(name = "to_time", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toTime) {

        String query;
        if (fromTime != null && toTime != null) {
            query = "select start, avg(percent) from plan_view where start >= \'" + fromTime + "\' and start <= \'" + toTime + "\' group by start;";
        } else {
            query = "select start, avg(percent) from plan_view group by start";
        }

        List<Map<String, Object>> queryRes = jdbcTemplate.queryForList(query);

        AveragePercentageResponse averagePercentageResponse = new AveragePercentageResponse();
        averagePercentageResponse.setAverage(queryRes.stream()
                .map(item -> ((BigDecimal)item.get("avg")).doubleValue())
                .mapToDouble(Double::doubleValue).sum() / queryRes.size());

        averagePercentageResponse.setTimeData(queryRes);
        averagePercentageResponse.setTimeData(queryRes.stream().map(item -> {
            Map<String, Object> out = new HashMap<>();
            out.put("percent", item.get("avg"));
            out.put("start", item.get("start"));
            return out;
        }).collect(Collectors.toList()));
        return new ResponseEntity<>(averagePercentageResponse, HttpStatus.OK);
    }
}
