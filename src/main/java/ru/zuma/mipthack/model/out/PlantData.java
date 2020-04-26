package ru.zuma.mipthack.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlantData extends BaseResponse {
    private LocalDate date;
    private int percentage;
    private boolean isFiniteCapacity;
}
