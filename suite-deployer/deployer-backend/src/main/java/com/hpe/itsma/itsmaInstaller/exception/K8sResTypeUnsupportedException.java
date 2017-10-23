package com.hpe.itsma.itsmaInstaller.exception;

/**
 * Created by tianlib on 8/29/2017.
 */
public class K8sResTypeUnsupportedException extends Exception {
    /**
     * Constructs a {@code K8sResTypeUnsupportedException} without specified detail message
     */
    public K8sResTypeUnsupportedException() {}

    /**
     * Constructs a {@code K8sResTypeUnsupportedException} with specified detail message
     *
     * @param message the detail message
     */
    public K8sResTypeUnsupportedException(String message) { super(message); }
}
