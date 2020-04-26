package ru.zuma.mipthack.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceInfoResponse extends BaseResponse {
    private double average;
    private List<Map<String, Object>> timeData;
}
