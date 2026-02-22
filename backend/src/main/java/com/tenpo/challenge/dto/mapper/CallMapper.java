package com.tenpo.challenge.dto.mapper;

import com.tenpo.challenge.dto.CallDTO;
import com.tenpo.challenge.model.Call;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CallMapper {

    public static Call dtoToEntity(CallDTO callDTO){
        return Call.builder()
                .timestamp(parseDate(callDTO.getTimestamp()))
                .endpoint(callDTO.getEndpoint())
                .params(callDTO.getParams())
                .response(callDTO.getResponse())
                .success(callDTO.getSuccess())
                .build();
    }

    public static CallDTO entityToDTO(Call call){

        return CallDTO.builder()
                .timestamp(call.getTimestamp().toString())
                .endpoint(call.getEndpoint())
                .params(call.getParams())
                .response(call.getResponse())
                .success(call.getSuccess())
                .build();

    }

    private static LocalDateTime parseDate(String dateString){
        return LocalDateTime.parse(dateString);
    }
}
