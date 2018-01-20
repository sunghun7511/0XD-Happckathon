package com.SHGroup.cometooceantofish.api;

public class RequestException extends Exception{
    private final String message;
    private final Exception exception;

    public RequestException(final String message){
        this(message, null);
    }

    public RequestException(final String message, final Exception exception){
        this.message = message;
        this.exception = exception;
    }

    public final Exception getOriginalException(){
        return exception;
    }

    public final String getMessage(){
        return message;
    }
}