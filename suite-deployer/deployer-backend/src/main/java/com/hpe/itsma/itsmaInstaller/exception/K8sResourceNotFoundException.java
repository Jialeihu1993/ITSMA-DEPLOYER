package com.hpe.itsma.itsmaInstaller.exception;

/**
 * Created by tianlib on 1/4/2017.
 */
public class K8sResourceNotFoundException extends Exception {

  /**
   * Constructs a {@code K8sResourceNotFoundException} without specified detail message
   */
  public K8sResourceNotFoundException() {}

  /**
   * Constructs a {@code K8sResourceNotFoundException} with specified detail message
   *
   * @param message the detail message
   */
  public K8sResourceNotFoundException(String message) { super(message); }
}
