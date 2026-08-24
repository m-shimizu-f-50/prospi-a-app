package com.prospia.backend.player;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * 選手の登録・更新リクエスト.
 */
public record PlayerRequest(
    @NotBlank String name,
    @NotBlank String position,
    String series,
    @NotBlank @Pattern(regexp = "batter|pitcher") String type,
    @NotNull Integer spirit,
    @NotNull Byte limitBreak,
    BigDecimal average,
    String trajectory,
    Integer meet,
    Integer power,
    Integer speed,
    BigDecimal era,
    Integer velocity,
    Integer control,
    Integer stamina,
    Integer skill1,
    Integer skill2,
    Integer skill3) {
}
