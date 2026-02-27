package com.tenpo.challenge.service.impl;

import com.tenpo.challenge.dto.CallDTO;
import com.tenpo.challenge.dto.mapper.CallMapper;
import com.tenpo.challenge.model.Call;
import com.tenpo.challenge.repository.CallRepository;
import com.tenpo.challenge.service.CallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallServiceImpl implements CallService {

    private final CallRepository callRepository;
    @Override
    public List<CallDTO> getAllCalls() {

        Iterable<Call> callIterable = callRepository.findAll();
        return StreamSupport.stream(callIterable.spliterator(), false)
                .map(CallMapper::entityToDTO)
                .toList();
    }

    @Override
    @Async("historyExecutor")
    public CompletableFuture<CallDTO> saveCall(CallDTO callDTO) {

        log.info("Saving call with response: {}", callDTO.getResponse());
        Call call = CallMapper.dtoToEntity(callDTO);
        Call savedCall = callRepository.save(call);
        return CompletableFuture.completedFuture(CallMapper.entityToDTO(savedCall));
    }

    @Override
    public Page<CallDTO> getPaginatedCalls(int page, int size, String sortBy) {
        log.info("Getting paginated calls with page {}, size {}, and sortBy{}", page, size, sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Call> callPage = callRepository.findAll(pageable);

        return callPage.map(CallMapper::entityToDTO);
    }
}
