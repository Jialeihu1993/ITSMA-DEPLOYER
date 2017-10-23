package com.hpe.itsma.itsmaInstaller.exception;

/**
 * Exception thrown when get the value of system environment variable failed
 *
 * Created by tianlib on 10/28/2016.
 */
public class SystemEnvMissingException extends Exception {

  private static final long serialVersionUID = 7270265752920332304L;

  /**
   * Constructs a {@code SystemEnvMissingException} without specified detail message
   */
  public SystemEnvMissingException() {}

  /**
   * Constructs a {@code SystemEnvMissingException} with specified detail message
   *
   * @param message the detail message
   */
  public SystemEnvMissingException(String message) { super(message); }
}
