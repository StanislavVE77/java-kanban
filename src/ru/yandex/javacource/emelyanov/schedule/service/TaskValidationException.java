package ru.yandex.javacource.emelyanov.schedule.service;

public class TaskValidationException extends RuntimeException {
    public TaskValidationException(final String message){
        super(message);
    }
}
