package ru.zuma.mipthack.model.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloWorldRequest {
    @NotEmpty
    private String name;
}
