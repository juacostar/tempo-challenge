package com.tenpo.challenge.service;

import com.tenpo.challenge.dto.CallDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CallService {

    List<CallDTO> getAllCalls();

    CompletableFuture<CallDTO> saveCall(CallDTO callDTO);
}
