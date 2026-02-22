package com.tenpo.challenge.controller;

import com.tenpo.challenge.dto.CalculateResponseDTO;
import com.tenpo.challenge.service.CalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tenpo/api/calculator")
@RequiredArgsConstructor
public class CalculateController {

    private final CalculatorService calculatorService;

    @PostMapping("/calculate/{num1}/{num2}")
    public ResponseEntity<CalculateResponseDTO> calculate(@PathVariable Double num1, @PathVariable Double num2){
        return ResponseEntity.ok(calculatorService.calculatePercentage(num1, num2));
    }
}
