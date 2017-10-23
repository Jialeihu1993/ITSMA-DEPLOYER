package com.hpe.itsma.itsmaInstaller.exception;

/**
 * Created by tianlib on 9/1/2017.
 */
public class InvalidStateException extends Exception {
    /**
     * Constructs a {@code InvalidStateException} without specified detail message
     */
    public InvalidStateException() {}

    /**
     * Constructs a {@code InvalidStateException} with specified detail message
     *
     * @param message the detail message
     */
    public InvalidStateException(String message) { super(message); }
}
