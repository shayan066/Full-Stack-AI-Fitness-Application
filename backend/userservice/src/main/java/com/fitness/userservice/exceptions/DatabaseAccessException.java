package com.fitness.userservice.exceptions;

public class DatabaseAccessException extends RuntimeException{
    public DatabaseAccessException(String message){
        super(message);
    }
}
