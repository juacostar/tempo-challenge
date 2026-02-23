package com.tenpo.challenge.service;

import com.tenpo.challenge.dto.CallDTO;
import com.tenpo.challenge.dto.mapper.CallMapper;
import com.tenpo.challenge.model.Call;
import com.tenpo.challenge.repository.CallRepository;
import com.tenpo.challenge.service.impl.CallServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CallServiceImplTest {
    @Mock
    private CallRepository callRepository;

    @InjectMocks
    private CallServiceImpl callService;

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Call buildCall(UUID id, String response) {
        Call call = new Call();
        call.setId(id);
        call.setEndpoint("/api/calculate");
        call.setResponse(response);
        call.setSuccess(true);
        return call;
    }

    private CallDTO buildCallDTO(UUID id, String response) {
        return CallDTO.builder()
                .id(id)
                .endpoint("/api/calculate")
                .response(response)
                .success(true)
                .build();
    }

    // -------------------------------------------------------------------------
    // getAllCalls
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getAllCalls should return mapped DTOs for every entity in the repository")
    void getAllCalls_returnsMappedDTOs() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Call call1 = buildCall(id1, "165.0");
        Call call2 = buildCall(id2, "330.0");
        CallDTO dto1 = buildCallDTO(id1, "165.0");
        CallDTO dto2 = buildCallDTO(id2, "330.0");

        when(callRepository.findAll()).thenReturn(List.of(call1, call2));

        try (MockedStatic<CallMapper> mapper = mockStatic(CallMapper.class)) {
            mapper.when(() -> CallMapper.entityToDTO(call1)).thenReturn(dto1);
            mapper.when(() -> CallMapper.entityToDTO(call2)).thenReturn(dto2);

            List<CallDTO> result = callService.getAllCalls();

            assertThat(result).hasSize(2).containsExactly(dto1, dto2);
        }
    }

    @Test
    @DisplayName("getAllCalls should return an empty list when the repository is empty")
    void getAllCalls_returnsEmptyList_whenRepositoryIsEmpty() {
        when(callRepository.findAll()).thenReturn(List.of());

        List<CallDTO> result = callService.getAllCalls();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getAllCalls should call findAll exactly once")
    void getAllCalls_callsFindAllOnce() {
        when(callRepository.findAll()).thenReturn(List.of());

        callService.getAllCalls();

        verify(callRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllCalls should return a single DTO when only one entity exists")
    void getAllCalls_returnsSingleDTO() {
        UUID id = UUID.randomUUID();
        Call call = buildCall(id, "100.0");
        CallDTO dto = buildCallDTO(id, "100.0");

        when(callRepository.findAll()).thenReturn(List.of(call));

        try (MockedStatic<CallMapper> mapper = mockStatic(CallMapper.class)) {
            mapper.when(() -> CallMapper.entityToDTO(call)).thenReturn(dto);

            List<CallDTO> result = callService.getAllCalls();

            assertThat(result).hasSize(1).containsExactly(dto);
        }
    }

    // -------------------------------------------------------------------------
    // saveCall
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("saveCall should persist the entity and return mapped DTO inside CompletableFuture")
    void saveCall_persistsAndReturnsMappedDTO() throws ExecutionException, InterruptedException {
        UUID id = UUID.randomUUID();
        CallDTO inputDTO = buildCallDTO(id, "165.0");
        Call entity = buildCall(id, "165.0");
        Call savedEntity = buildCall(id, "165.0");
        CallDTO expectedDTO = buildCallDTO(id, "165.0");

        try (MockedStatic<CallMapper> mapper = mockStatic(CallMapper.class)) {
            mapper.when(() -> CallMapper.dtoToEntity(inputDTO)).thenReturn(entity);
            when(callRepository.save(entity)).thenReturn(savedEntity);
            mapper.when(() -> CallMapper.entityToDTO(savedEntity)).thenReturn(expectedDTO);

            CompletableFuture<CallDTO> future = callService.saveCall(inputDTO);
            CallDTO result = future.get();

            assertThat(result).isEqualTo(expectedDTO);
        }
    }

    @Test
    @DisplayName("saveCall should call repository.save exactly once with the mapped entity")
    void saveCall_callsRepositorySaveOnce() throws ExecutionException, InterruptedException {
        UUID id = UUID.randomUUID();
        CallDTO inputDTO = buildCallDTO(id, "200.0");
        Call entity = buildCall(id, "200.0");
        Call savedEntity = buildCall(id, "200.0");

        try (MockedStatic<CallMapper> mapper = mockStatic(CallMapper.class)) {
            mapper.when(() -> CallMapper.dtoToEntity(inputDTO)).thenReturn(entity);
            when(callRepository.save(entity)).thenReturn(savedEntity);
            mapper.when(() -> CallMapper.entityToDTO(savedEntity)).thenReturn(inputDTO);

            callService.saveCall(inputDTO).get();

            verify(callRepository, times(1)).save(entity);
        }
    }

    @Test
    @DisplayName("saveCall should return a completed (non-null) CompletableFuture")
    void saveCall_returnsCompletedFuture() {
        UUID id = UUID.randomUUID();
        CallDTO inputDTO = buildCallDTO(id, "50.0");
        Call entity = buildCall(id, "50.0");
        Call savedEntity = buildCall(id, "50.0");

        try (MockedStatic<CallMapper> mapper = mockStatic(CallMapper.class)) {
            mapper.when(() -> CallMapper.dtoToEntity(inputDTO)).thenReturn(entity);
            when(callRepository.save(entity)).thenReturn(savedEntity);
            mapper.when(() -> CallMapper.entityToDTO(savedEntity)).thenReturn(inputDTO);

            CompletableFuture<CallDTO> future = callService.saveCall(inputDTO);

            assertThat(future).isNotNull().isDone();
        }
    }

    @Test
    @DisplayName("saveCall should propagate exception when repository.save fails")
    void saveCall_propagatesException_whenRepositoryFails() {
        UUID id = UUID.randomUUID();
        CallDTO inputDTO = buildCallDTO(id, "error");
        Call entity = buildCall(id, "error");

        try (MockedStatic<CallMapper> mapper = mockStatic(CallMapper.class)) {
            mapper.when(() -> CallMapper.dtoToEntity(inputDTO)).thenReturn(entity);
            when(callRepository.save(entity)).thenThrow(new RuntimeException("DB error"));

            org.assertj.core.api.Assertions.assertThatThrownBy(
                    () -> callService.saveCall(inputDTO).get()
            ).isInstanceOf(RuntimeException.class);
        }
    }
}
