package com.prospia.backend.player;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 選手のAPIレスポンス.
 */
public record PlayerResponse(
    Long id,
    String name,
    String position,
    String series,
    String type,
    Integer spirit,
    Byte limitBreak,
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
    Integer skill3,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  /**
   * エンティティからレスポンスDTOを生成する.
   */
  public static PlayerResponse from(Player player) {
    return new PlayerResponse(
        player.getId(),
        player.getName(),
        player.getPosition(),
        player.getSeries(),
        player.getType(),
        player.getSpirit(),
        player.getLimitBreak(),
        player.getAverage(),
        player.getTrajectory(),
        player.getMeet(),
        player.getPower(),
        player.getSpeed(),
        player.getEra(),
        player.getVelocity(),
        player.getControl(),
        player.getStamina(),
        player.getSkill1(),
        player.getSkill2(),
        player.getSkill3(),
        player.getCreatedAt(),
        player.getUpdatedAt());
  }
}
