package com.eric.taskflow.exception;


//Excepcion de negocio
public class ApiException extends RuntimeException {

        public ApiException(String message) {
            super(message);
        }
}

