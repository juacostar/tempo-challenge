package com.tenpo.challenge.controller;

import com.tenpo.challenge.dto.CalculateResponseDTO;
import com.tenpo.challenge.service.CalculatorService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tenpo/api/calculator")
@RequiredArgsConstructor
@Validated
public class CalculateController {

    private final CalculatorService calculatorService;

    @PostMapping("/calculate/{num1}/{num2}")
    public ResponseEntity<CalculateResponseDTO> calculate(
            @PathVariable @Min(0) Double num1,
            @PathVariable @Min(0) Double num2){
        return ResponseEntity.ok(calculatorService.calculatePercentage(num1, num2));
    }
}
