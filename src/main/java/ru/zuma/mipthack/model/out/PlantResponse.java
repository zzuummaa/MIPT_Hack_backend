package ru.zuma.mipthack.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlantResponse extends BaseResponse {
    private Long id;
    private String fullName;
    private String shortName;
    private ArrayList<PlantData> timeData;
}
