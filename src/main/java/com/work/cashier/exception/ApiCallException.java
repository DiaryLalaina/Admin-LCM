package com.work.cashier.exception;

// ApiCallException.java
public class ApiCallException extends RuntimeException {
    private final int statusCode; // Si vous voulez capturer le statut HTTP de l'API externe

    public ApiCallException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApiCallException(String message) {
        super(message);
        this.statusCode = 0; // Ou une valeur par défaut
    }

    public int getStatusCode() {
        return statusCode;
    }
}
