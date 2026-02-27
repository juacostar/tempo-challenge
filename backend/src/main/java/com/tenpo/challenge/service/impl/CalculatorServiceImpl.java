package com.tenpo.challenge.service.impl;

import com.tenpo.challenge.dto.CalculateResponseDTO;
import com.tenpo.challenge.dto.CallDTO;
import com.tenpo.challenge.exception.PercentageUnavailableException;
import com.tenpo.challenge.service.CalculatorService;
import com.tenpo.challenge.service.CallService;
import com.tenpo.challenge.service.PercentageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculatorServiceImpl implements CalculatorService {

    private final PercentageService percentageService;
    private final CallService callService;
    @Override
    public CalculateResponseDTO calculatePercentage(Double num1, Double num2) {

        log.info("Begin process calculatePercentage with nums: {}, {}", num1, num2);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("num1", String.valueOf(num1));
        params.put("num2", String.valueOf(num2));

       try {
           double sum = num1 + num2;
           double percentage = percentageService.getPercentage();
           double percentageAmount = sum * percentage / 100;
           double result = sum + percentageAmount;

           percentageService.saveBackupPercentage(percentage);

           callService.saveCall(CallDTO.builder()
                           .id(UUID.randomUUID())
                           .timestamp(String.valueOf(LocalDateTime.now()))
                           .endpoint("/api/calculate")
                           .params(params)
                           .response(String.valueOf(result))
                           .success(true)
                   .build());

           return CalculateResponseDTO.builder()
                   .result(result)
                   .build();

       }catch (PercentageUnavailableException exception){

           callService.saveCall(CallDTO.builder()
                           .id(UUID.randomUUID())
                   .timestamp(String.valueOf(LocalDateTime.now()))
                   .endpoint("/api/calculate")
                   .params(params)
                   .response(exception.getMessage())
                   .success(false)
                   .build());

           throw exception;

       }
    }
}
