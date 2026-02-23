package com.tenpo.challenge.controller;

import com.tenpo.challenge.dto.CalculateResponseDTO;
import com.tenpo.challenge.exception.PercentageUnavailableException;
import com.tenpo.challenge.service.CalculatorService;
import com.tenpo.challenge.service.CallService;
import com.tenpo.challenge.service.PercentageService;
import com.tenpo.challenge.service.impl.CalculatorServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculateController.class)
public class CalculateControllerTest {

    private static final String BASE_URL = "/tenpo/api/calculator/calculate";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PercentageService percentageService;

    @MockitoBean
    private CallService callService;

    @MockitoBean
    private CalculatorServiceImpl calculatorService;

    // -------------------------------------------------------------------------
    // Happy path
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /calculate/{num1}/{num2} returns 200 with result in body")
    void calculate_returns200WithResult() throws Exception {
        when(calculatorService.calculatePercentage(100.0, 50.0))
                .thenReturn(CalculateResponseDTO.builder().result(165.0).build());

        mockMvc.perform(post(BASE_URL + "/100.0/50.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(165.0));
    }

    @Test
    @DisplayName("POST /calculate/{num1}/{num2} calls calculatorService with correct params")
    void calculate_callsServiceWithCorrectParams() throws Exception {
        when(calculatorService.calculatePercentage(200.0, 300.0))
                .thenReturn(CalculateResponseDTO.builder().result(750.0).build());

        mockMvc.perform(post(BASE_URL + "/200.0/300.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(calculatorService, times(1)).calculatePercentage(200.0, 300.0);
    }

    @Test
    @DisplayName("POST /calculate/{num1}/{num2} returns correct result with zero values")
    void calculate_withZeroValues_returns200() throws Exception {
        when(calculatorService.calculatePercentage(0.0, 0.0))
                .thenReturn(CalculateResponseDTO.builder().result(0.0).build());

        mockMvc.perform(post(BASE_URL + "/0.0/0.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(0.0));
    }

    @Test
    @DisplayName("POST /calculate/{num1}/{num2} returns correct result with negative numbers")
    void calculate_withNegativeNumbers_returns200() throws Exception {
        when(calculatorService.calculatePercentage(-100.0, -50.0))
                .thenReturn(CalculateResponseDTO.builder().result(-165.0).build());

        mockMvc.perform(post(BASE_URL + "/-100.0/-50.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(-165.0));
    }

    @Test
    @DisplayName("POST /calculate/{num1}/{num2} response body contains 'result' field")
    void calculate_responseBodyHasResultField() throws Exception {
        when(calculatorService.calculatePercentage(10.0, 20.0))
                .thenReturn(CalculateResponseDTO.builder().result(33.0).build());

        mockMvc.perform(post(BASE_URL + "/10.0/20.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    @DisplayName("POST /calculate/{num1}/{num2} returns correct content type")
    void calculate_returnsJsonContentType() throws Exception {
        when(calculatorService.calculatePercentage(1.0, 2.0))
                .thenReturn(CalculateResponseDTO.builder().result(3.3).build());

        mockMvc.perform(post(BASE_URL + "/1.0/2.0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // -------------------------------------------------------------------------
    // Failure path
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /calculate/{num1}/{num2} returns 503 when PercentageUnavailableException is thrown")
    void calculate_returns503_whenPercentageUnavailable() throws Exception {
        when(calculatorService.calculatePercentage(anyDouble(), anyDouble()))
                .thenThrow(new PercentageUnavailableException("Percentage service unavailable"));

        mockMvc.perform(post(BASE_URL + "/10.0/20.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("POST /calculate/{num1}/{num2} returns 400 when path variables are not numeric")
    void calculate_returns400_whenPathVariablesAreNotNumeric() throws Exception {
        mockMvc.perform(post(BASE_URL + "/abc/xyz")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
