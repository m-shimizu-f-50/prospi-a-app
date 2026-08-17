package com.prospia.backend.common.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全Controllerで発生した例外を、共通のエラーレスポンス形式に変換する.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * リクエストのバリデーションエラーを422レスポンスに変換する.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, List<String>> errors = new HashMap<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      errors.computeIfAbsent(fieldError.getField(), key -> new ArrayList<>())
            .add(fieldError.getDefaultMessage());
    }
    ErrorResponse body = new ErrorResponse("入力内容に誤りがあります", errors);
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
  }

  /**
   * 存在しないリソースへのアクセスを404レスポンスに変換する.
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
  }

  /**
   * 想定外の例外を500レスポンスに変換する.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("予期しないエラーが発生しました"));
  }
}
