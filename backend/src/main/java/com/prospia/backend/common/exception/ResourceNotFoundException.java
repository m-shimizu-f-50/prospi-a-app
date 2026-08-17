package com.prospia.backend.common.exception;

/**
 * 指定されたリソースが存在しない場合にスローする例外.
 */
public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
