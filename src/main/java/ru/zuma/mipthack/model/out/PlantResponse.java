package ru.zuma.mipthack.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlantResponse {
    private Long id;
    private String fullName;
    private String shortName;
    private ArrayList<PlantData> timeData;
}
