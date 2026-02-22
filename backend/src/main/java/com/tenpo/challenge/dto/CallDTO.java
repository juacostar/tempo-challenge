package com.tenpo.challenge.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class CallDTO {

    UUID id;
    private String timestamp;
    private String endpoint;
    private Map<String, String> params;
    private String response;
    private Boolean success;
}
