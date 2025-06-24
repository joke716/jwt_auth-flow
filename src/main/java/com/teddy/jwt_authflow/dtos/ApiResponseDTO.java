package com.teddy.jwt_authflow.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // JSON 필드명은 snake_case 예: status_code
@Schema(name = "ApiResponse", description = "표준 API 응답 포맷")
public class ApiResponseDTO<T> {
    private int statusCode;
    private String message;
    private T data;
}
