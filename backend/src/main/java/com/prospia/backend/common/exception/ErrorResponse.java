package com.prospia.backend.common.exception;

import java.util.List;
import java.util.Map;

public record ErrorResponse(
        String message,
        Map<String, List<String>> errors
) {
  public ErrorResponse(String message) {
    this(message, null);
  }
}