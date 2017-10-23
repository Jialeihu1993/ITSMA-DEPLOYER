package com.hpe.itsma.itsmaInstaller.exception;

/**
 * Created by tianlib on 6/16/2017.
 */
public class VaultSecretValueEmptyException extends Exception {

  /**
   * Constructs a {@code VaultSecretValueEmptyException} without specified detail message
   */
  public VaultSecretValueEmptyException() {}

  /**
   * Constructs a {@code VaultSecretValueEmptyException} with specified detail message
   *
   * @param message the detail message
   */
  public VaultSecretValueEmptyException(String message) { super(message); }
}
