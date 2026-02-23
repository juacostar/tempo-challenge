package com.tenpo.challenge.controller;

import com.tenpo.challenge.dto.CallDTO;
import com.tenpo.challenge.service.CallService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CallController.class)
public class CallControllerTest {

    private static final String BASE_URL = "/tenpo/api/calls";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CallService callService;

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private CallDTO buildCallDTO(String response, boolean success) {
        return CallDTO.builder()
                .id(UUID.randomUUID())
                .endpoint("/api/calculate")
                .timestamp("2024-01-01T10:00:00")
                .response(response)
                .success(success)
                .build();
    }

    // -------------------------------------------------------------------------
    // Happy path
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /tenpo/api/calls returns 200 with list of calls")
    void getAllCalls_returns200WithList() throws Exception {
        List<CallDTO> calls = List.of(
                buildCallDTO("165.0", true),
                buildCallDTO("330.0", true)
        );

        when(callService.getAllCalls()).thenReturn(calls);

        mockMvc.perform(get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /tenpo/api/calls returns correct fields in each call")
    void getAllCalls_returnsCorrectFields() throws Exception {
        CallDTO call = buildCallDTO("100.0", true);
        when(callService.getAllCalls()).thenReturn(List.of(call));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].endpoint").value("/api/calculate"))
                .andExpect(jsonPath("$[0].response").value("100.0"))
                .andExpect(jsonPath("$[0].success").value(true))
                .andExpect(jsonPath("$[0].timestamp").value("2024-01-01T10:00:00"));
    }

    @Test
    @DisplayName("GET /tenpo/api/calls returns empty list when no calls exist")
    void getAllCalls_returnsEmptyList() throws Exception {
        when(callService.getAllCalls()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /tenpo/api/calls returns application/json content type")
    void getAllCalls_returnsJsonContentType() throws Exception {
        when(callService.getAllCalls()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /tenpo/api/calls calls service exactly once")
    void getAllCalls_callsServiceOnce() throws Exception {
        when(callService.getAllCalls()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL));

        verify(callService, times(1)).getAllCalls();
    }

    @Test
    @DisplayName("GET /tenpo/api/calls returns both successful and failed calls")
    void getAllCalls_returnsMixedSuccessAndFailedCalls() throws Exception {
        List<CallDTO> calls = List.of(
                buildCallDTO("165.0", true),
                buildCallDTO("Percentage service unavailable", false)
        );

        when(callService.getAllCalls()).thenReturn(calls);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].success").value(true))
                .andExpect(jsonPath("$[1].success").value(false));
    }

    @Test
    @DisplayName("GET /tenpo/api/calls returns 200 with single call")
    void getAllCalls_returnsSingleCall() throws Exception {
        CallDTO call = buildCallDTO("50.0", true);
        when(callService.getAllCalls()).thenReturn(List.of(call));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // -------------------------------------------------------------------------
    // Failure path
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /tenpo/api/calls returns 500 when service throws unexpected exception")
    void getAllCalls_returns500_whenServiceThrowsException() throws Exception {
        when(callService.getAllCalls()).thenThrow(new RuntimeException("Unexpected DB error"));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isInternalServerError());
    }

}
