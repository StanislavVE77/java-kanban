package ru.yandex.javacource.emelyanov.schedule.service;

import java.io.IOException;

public class FileException extends RuntimeException {

    public FileException(final String message, final IOException cause) {
        super(message, cause);
    }
}
