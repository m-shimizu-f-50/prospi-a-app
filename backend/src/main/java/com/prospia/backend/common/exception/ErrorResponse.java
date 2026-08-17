package com.prospia.backend.common.exception;

import java.util.List;
import java.util.Map;

/**
 * APIのエラーレスポンス共通形式.
 *
 * @param message エラーの概要メッセージ
 * @param errors  バリデーションエラーの詳細(フィールド名 -> エラーメッセージ一覧)
 */
public record ErrorResponse(
        String message,
        Map<String, List<String>> errors
) {
  public ErrorResponse(String message) {
    this(message, null);
  }
}