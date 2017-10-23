package com.hpe.itsma.itsmaInstaller.exception;

/**
 * Created by tianlib on 5/16/2017.
 */
public class ItsmaServiceNotReadyToInstallException extends Exception {

  /**
   * Constructs a {@code ItsmaServiceNotReadyToInstallException} without specified detail message
   */
  public ItsmaServiceNotReadyToInstallException() {}

  /**
   * Constructs a {@code ItsmaServiceNotReadyToInstallException} with specified detail message
   *
   * @param message the detail message
   */
  public ItsmaServiceNotReadyToInstallException(String message) { super(message); }
}