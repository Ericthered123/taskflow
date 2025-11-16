package com.eric.taskflow.exception;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
//Clase para que GlobalExceptionHandler devuelva errores uniformes.
@Data
@Builder
public class ErrorResponse {
    private Instant timestamp; //cuando ocurrio
    private int status;// HTTP status code
    private String error;// nombre de error
    private String message;// explicacion
    private String path;// endpoint que fallo
}
