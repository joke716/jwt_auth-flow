package com.teddy.jwt_authflow.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(value = PropertyNamingStrategies.UpperCamelCaseStrategy.class)
@Schema(title = "Error", accessMode = Schema.AccessMode.READ_ONLY)
public class ExceptionResponseDTO<T> {

    private String status;
    private T description;

}
