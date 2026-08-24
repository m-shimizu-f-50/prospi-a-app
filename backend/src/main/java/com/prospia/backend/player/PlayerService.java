package com.prospia.backend.player;

import com.prospia.backend.common.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 選手に関するビジネスロジックを扱うサービス.
 */
@Service
@Transactional
public class PlayerService {

  private final PlayerRepository playerRepository;

  public PlayerService(PlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  /**
   * 選手一覧を取得する.
   */
  @Transactional(readOnly = true)
  public List<Player> findAll(String type) {
    if (type == null) {
      return playerRepository.findAll();
    }
    return playerRepository.findByType(type);
  }

  @Transactional(readOnly = true)
  public Player findById(Long id) {
    return playerRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("選手が見つかりません: id=" + id));
  }

  /**
   * 選手を新規登録する.
   */
  public Player create(PlayerRequest request) {
    Player player = new Player(
        request.name(), request.position(), request.series(), request.type(),
        request.spirit(), request.limitBreak(), request.average(), request.trajectory(),
        request.meet(), request.power(), request.speed(), request.era(), request.velocity(),
        request.control(), request.stamina(), request.skill1(), request.skill2(),
        request.skill3());
    return playerRepository.save(player);
  }

  /**
   * 選手を更新する.
   */
  public Player update(Long id, PlayerRequest request) {
    Player player = findById(id);
    player.setName(request.name());
    player.setPosition(request.position());
    player.setSeries(request.series());
    player.setType(request.type());
    player.setSpirit(request.spirit());
    player.setLimitBreak(request.limitBreak());
    player.setAverage(request.average());
    player.setTrajectory(request.trajectory());
    player.setMeet(request.meet());
    player.setPower(request.power());
    player.setSpeed(request.speed());
    player.setEra(request.era());
    player.setVelocity(request.velocity());
    player.setControl(request.control());
    player.setStamina(request.stamina());
    player.setSkill1(request.skill1());
    player.setSkill2(request.skill2());
    player.setSkill3(request.skill3());
    return player;
  }

  /**
   * 選手を削除する.
   */
  public void delete(Long id) {
    playerRepository.delete(findById(id));
  }
}
