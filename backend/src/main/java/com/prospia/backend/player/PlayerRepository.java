package com.prospia.backend.player;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 選手のデータアクセスを行うリポジトリ.
 */
public interface PlayerRepository extends JpaRepository<Player, Long> {
  List<Player> findByType(String type);
}
