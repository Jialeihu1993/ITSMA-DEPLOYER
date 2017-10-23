package com.hpe.itsma.itsmaInstaller.exception;

/**
 * Created by tianlib on 6/9/2017.
 */
public class K8sResourceFailToCreateException extends Exception {

  /**
   * Constructs a {@code K8sResourceFailToCreateException} without specified detail message
   */
  public K8sResourceFailToCreateException() {}

  /**
   * Constructs a {@code K8sResourceFailToCreateException} with specified detail message
   *
   * @param message the detail message
   */
  public K8sResourceFailToCreateException(String message) { super(message); }
}
