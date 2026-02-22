package com.tenpo.challenge.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseDTO {

    private String timestamp;
    private String status;
    private String error;
    private String message;

}
