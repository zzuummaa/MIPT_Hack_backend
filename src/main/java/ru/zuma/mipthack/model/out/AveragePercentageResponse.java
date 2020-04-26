package ru.zuma.mipthack.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AveragePercentageResponse extends BaseResponse {
    private int average;
    private List<Map<String, Object>> timeData;
}
