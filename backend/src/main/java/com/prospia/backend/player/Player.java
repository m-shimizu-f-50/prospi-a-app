package com.prospia.backend.player;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 選手を表すエンティティ.
 */
@Entity
@Table(name = "players")
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, length = 10)
  private String position;

  private String series;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private Integer spirit;

  @Column(name = "limit_break", nullable = false)
  private Byte limitBreak;

  private BigDecimal average;

  private String trajectory;

  private Integer meet;

  private Integer power;

  private Integer speed;

  private BigDecimal era;

  private Integer velocity;

  private Integer control;

  private Integer stamina;

  private Integer skill1;

  private Integer skill2;

  private Integer skill3;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  protected Player() {
  }

  /**
   * 選手エンティティを生成する.
   */
  public Player(String name, String position, String series, String type, Integer spirit,
    Byte limitBreak, BigDecimal average, String trajectory, Integer meet, Integer power,
      Integer speed, BigDecimal era, Integer velocity, Integer control, Integer stamina,
      Integer skill1, Integer skill2, Integer skill3) {
    this.name = name;
    this.position = position;
    this.series = series;
    this.type = type;
    this.spirit = spirit;
    this.limitBreak = limitBreak;
    this.average = average;
    this.trajectory = trajectory;
    this.meet = meet;
    this.power = power;
    this.speed = speed;
    this.era = era;
    this.velocity = velocity;
    this.control = control;
    this.stamina = stamina;
    this.skill1 = skill1;
    this.skill2 = skill2;
    this.skill3 = skill3;
  }

  @PrePersist
  void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public String getSeries() {
    return series;
  }

  public void setSeries(String series) {
    this.series = series;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getSpirit() {
    return spirit;
  }

  public void setSpirit(Integer spirit) {
    this.spirit = spirit;
  }

  public Byte getLimitBreak() {
    return limitBreak;
  }

  public void setLimitBreak(Byte limitBreak) {
    this.limitBreak = limitBreak;
  }

  public BigDecimal getAverage() {
    return average;
  }

  public void setAverage(BigDecimal average) {
    this.average = average;
  }

  public String getTrajectory() {
    return trajectory;
  }

  public void setTrajectory(String trajectory) {
    this.trajectory = trajectory;
  }

  public Integer getMeet() {
    return meet;
  }

  public void setMeet(Integer meet) {
    this.meet = meet;
  }

  public Integer getPower() {
    return power;
  }

  public void setPower(Integer power) {
    this.power = power;
  }

  public Integer getSpeed() {
    return speed;
  }

  public void setSpeed(Integer speed) {
    this.speed = speed;
  }

  public BigDecimal getEra() {
    return era;
  }

  public void setEra(BigDecimal era) {
    this.era = era;
  }

  public Integer getVelocity() {
    return velocity;
  }

  public void setVelocity(Integer velocity) {
    this.velocity = velocity;
  }

  public Integer getControl() {
    return control;
  }

  public void setControl(Integer control) {
    this.control = control;
  }

  public Integer getStamina() {
    return stamina;
  }

  public void setStamina(Integer stamina) {
    this.stamina = stamina;
  }

  public Integer getSkill1() {
    return skill1;
  }

  public void setSkill1(Integer skill1) {
    this.skill1 = skill1;
  }

  public Integer getSkill2() {
    return skill2;
  }

  public void setSkill2(Integer skill2) {
    this.skill2 = skill2;
  }

  public Integer getSkill3() {
    return skill3;
  }

  public void setSkill3(Integer skill3) {
    this.skill3 = skill3;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
