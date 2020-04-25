package ru.zuma.mipthack.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlantData extends BaseResponse {
    private LocalDate date;
    private int percentage;
}
