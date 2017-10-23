package com.hpe.itsma.itsmaInstaller.exception;

/**
 * Created by tianlib on 1/4/2017.
 */
public class ItsmaServiceNotFoundException extends Exception {

  /**
   * Constructs a {@code ItsmaServiceNotFoundException} without specified detail message
   */
  public ItsmaServiceNotFoundException() {}

  /**
   * Constructs a {@code ItsmaServiceNotFoundException} with specified detail message
   *
   * @param message the detail message
   */
  public ItsmaServiceNotFoundException(String message) { super(message); }
}