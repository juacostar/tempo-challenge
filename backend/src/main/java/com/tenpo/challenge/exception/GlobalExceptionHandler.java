package com.tenpo.challenge.exception;

import com.tenpo.challenge.dto.CallDTO;
import com.tenpo.challenge.dto.ErrorResponseDTO;
import com.tenpo.challenge.service.CallService;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final CallService callService;
    @ExceptionHandler(PercentageUnavailableException.class)
    public ResponseEntity<ErrorResponseDTO> handlePercentageUnavailable(
            PercentageUnavailableException e) {

        log.error("Porcentaje no disponible: {}", e.getMessage());
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(
            MethodArgumentTypeMismatchException e) {

        String msg = "El parámetro '" + e.getName() + "' debe ser numérico";
        return buildError(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(Exception e) {
        log.error("Error inesperado: {}", e.getMessage(), e);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(ConstraintViolationException e){
        log.error("Error de validación_ {}", e.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "Error de validación");

    }


    private ResponseEntity<ErrorResponseDTO> buildError(HttpStatus status, String message){

        ErrorResponseDTO body = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(String.valueOf(status.value()))
                .error(status.getReasonPhrase())
                .message(message)
                .build();

        return ResponseEntity.status(status).body(body);

    }


}
