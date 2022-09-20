package com.example.demo.documents.exceptions;

public class DocumentManagerException extends RuntimeException{
    public DocumentManagerException() {
        super();
    }

    public DocumentManagerException(String message) {
        super(message);
    }

    public DocumentManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentManagerException(Throwable cause) {
        super(cause);
    }

    protected DocumentManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
