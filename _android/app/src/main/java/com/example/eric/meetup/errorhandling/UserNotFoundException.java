package com.example.eric.meetup.errorhandling;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(String error) {
        super(error);
    }

    public UserNotFoundException(Exception e) {
        super(e);
    }
}
