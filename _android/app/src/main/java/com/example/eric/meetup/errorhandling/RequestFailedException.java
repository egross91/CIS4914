package com.example.eric.meetup.errorhandling;

public class RequestFailedException extends Exception {
    public RequestFailedException(String error) {
        super(error);
    }

    public RequestFailedException(Exception e) {
        super(e);
    }
}
