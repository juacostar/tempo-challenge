package com.tenpo.challenge.service;

import com.tenpo.challenge.dto.CallDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CallService {

    List<CallDTO> getAllCalls();

    CompletableFuture<CallDTO> saveCall(CallDTO callDTO);

    Page<CallDTO> getPaginatedCalls(int page, int size, String sortBy);
}
