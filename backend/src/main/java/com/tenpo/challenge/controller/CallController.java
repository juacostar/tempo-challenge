package com.tenpo.challenge.controller;

import com.tenpo.challenge.dto.CallDTO;
import com.tenpo.challenge.service.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("tenpo/api/calls")
@RequiredArgsConstructor
public class CallController {

    private final CallService callService;

    @GetMapping()
    public ResponseEntity<List<CallDTO>> getAllCalls(){
        return ResponseEntity.ok(callService.getAllCalls());
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<CallDTO>> getPaginatedCalls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ){
        return ResponseEntity.ok(callService.getPaginatedCalls(page, size, sortBy));
    }

}
