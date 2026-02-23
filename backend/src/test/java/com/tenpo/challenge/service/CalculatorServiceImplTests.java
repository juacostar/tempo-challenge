package com.tenpo.challenge.service;


import com.tenpo.challenge.dto.CalculateResponseDTO;
import com.tenpo.challenge.dto.CallDTO;
import com.tenpo.challenge.exception.PercentageUnavailableException;
import com.tenpo.challenge.service.impl.CalculatorServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CalculatorServiceImplTests {

    @Mock
    private PercentageService percentageService;

    @Mock
    private CallService callService;

    @InjectMocks
    private CalculatorServiceImpl calculatorService;

    @Test
    @DisplayName("Should return correct result when percentage service returns a valid value")
    void calculatePercentage_success() {

        double num1 = 100.0;
        double num2 = 50.0;
        double percentage = 10.0;

        when(percentageService.getPercentage()).thenReturn(percentage);


        CalculateResponseDTO response = calculatorService.calculatePercentage(num1, num2);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isEqualTo(165.0);
    }

    @Test
    @DisplayName("Should save backup percentage after successful calculation")
    void calculatePercentage_savesBackupPercentage() {
        double percentage = 20.0;
        when(percentageService.getPercentage()).thenReturn(percentage);

        calculatorService.calculatePercentage(10.0, 20.0);

        verify(percentageService, times(1)).saveBackupPercentage(percentage);
    }

    @Test
    @DisplayName("Should save a successful call log after calculation")
    void calculatePercentage_savesSuccessCallLog() {
        when(percentageService.getPercentage()).thenReturn(10.0);

        calculatorService.calculatePercentage(100.0, 200.0);

        ArgumentCaptor<CallDTO> callCaptor = ArgumentCaptor.forClass(CallDTO.class);
        verify(callService, times(1)).saveCall(callCaptor.capture());

        CallDTO savedCall = callCaptor.getValue();
        assertThat(savedCall.getEndpoint()).isEqualTo("/api/calculate");
        assertThat(savedCall.getSuccess()).isTrue();
        assertThat(savedCall.getParams()).containsEntry("num1", "100.0")
                .containsEntry("num2", "200.0");
        assertThat(savedCall.getResponse()).isNotNull();
    }

    @Test
    @DisplayName("Should set a random UUID on the success call log")
    void calculatePercentage_setsIdOnSuccessCall() {
        when(percentageService.getPercentage()).thenReturn(5.0);

        calculatorService.calculatePercentage(1.0, 2.0);

        ArgumentCaptor<CallDTO> captor = ArgumentCaptor.forClass(CallDTO.class);
        verify(callService).saveCall(captor.capture());

        assertThat(captor.getValue().getId()).isNotNull();
    }

    @Test
    @DisplayName("Result should equal (num1 + num2) * (1 + percentage/100)")
    void calculatePercentage_mathIsCorrect() {
        double num1 = 200.0;
        double num2 = 300.0;
        double percentage = 50.0;
        double expected = (num1 + num2) * (1 + percentage / 100); // 750

        when(percentageService.getPercentage()).thenReturn(percentage);

        CalculateResponseDTO response = calculatorService.calculatePercentage(num1, num2);

        assertThat(response.getResult()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should work correctly with zero percentage")
    void calculatePercentage_zeroPercentage() {
        when(percentageService.getPercentage()).thenReturn(0.0);

        CalculateResponseDTO response = calculatorService.calculatePercentage(50.0, 50.0);

        assertThat(response.getResult()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Should work correctly with negative numbers")
    void calculatePercentage_negativeNumbers() {
        double percentage = 10.0;
        when(percentageService.getPercentage()).thenReturn(percentage);

        CalculateResponseDTO response = calculatorService.calculatePercentage(-100.0, -50.0);

        // sum = -150, result = -150 + (-15) = -165
        assertThat(response.getResult()).isEqualTo(-165.0);
    }

    // -------------------------------------------------------------------------
    // Failure path
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should rethrow PercentageUnavailableException when percentage service fails")
    void calculatePercentage_throwsWhenPercentageUnavailable() {
        when(percentageService.getPercentage())
                .thenThrow(new PercentageUnavailableException("Percentage service unavailable"));

        assertThatThrownBy(() -> calculatorService.calculatePercentage(10.0, 20.0))
                .isInstanceOf(PercentageUnavailableException.class)
                .hasMessageContaining("Percentage service unavailable");
    }

    @Test
    @DisplayName("Should save a failed call log when PercentageUnavailableException is thrown")
    void calculatePercentage_savesFailureCallLog_onPercentageUnavailable() {
        String errorMessage = "Percentage service unavailable";
        when(percentageService.getPercentage())
                .thenThrow(new PercentageUnavailableException(errorMessage));

        assertThatThrownBy(() -> calculatorService.calculatePercentage(10.0, 20.0))
                .isInstanceOf(PercentageUnavailableException.class);

        ArgumentCaptor<CallDTO> captor = ArgumentCaptor.forClass(CallDTO.class);
        verify(callService, times(1)).saveCall(captor.capture());

        CallDTO savedCall = captor.getValue();
        assertThat(savedCall.getEndpoint()).isEqualTo("/api/calculate");
        assertThat(savedCall.getSuccess()).isFalse();
        assertThat(savedCall.getResponse()).isEqualTo(errorMessage);
        assertThat(savedCall.getParams()).containsEntry("num1", "10.0")
                .containsEntry("num2", "20.0");
    }

    @Test
    @DisplayName("Should not save backup percentage when exception is thrown")
    void calculatePercentage_doesNotSaveBackup_onPercentageUnavailable() {
        when(percentageService.getPercentage())
                .thenThrow(new PercentageUnavailableException("error"));

        assertThatThrownBy(() -> calculatorService.calculatePercentage(1.0, 2.0))
                .isInstanceOf(PercentageUnavailableException.class);

        verify(percentageService, never()).saveBackupPercentage(anyDouble());
    }

    @Test
    @DisplayName("Should not set UUID on failure call log")
    void calculatePercentage_noIdOnFailureCall() {
        when(percentageService.getPercentage())
                .thenThrow(new PercentageUnavailableException("error"));

        assertThatThrownBy(() -> calculatorService.calculatePercentage(1.0, 2.0))
                .isInstanceOf(PercentageUnavailableException.class);

        ArgumentCaptor<CallDTO> captor = ArgumentCaptor.forClass(CallDTO.class);
        verify(callService).saveCall(captor.capture());

        // UUID is not set on the error path (no .id(...) call in the catch block)
        assertThat(captor.getValue().getId()).isNull();
    }

}
