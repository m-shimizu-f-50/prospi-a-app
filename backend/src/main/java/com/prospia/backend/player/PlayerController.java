package com.prospia.backend.player;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 選手に関するAPIエンドポイント.
 */
@RestController
public class PlayerController {

  private final PlayerService playerService;

  public PlayerController(PlayerService playerService) {
    this.playerService = playerService;
  }

  /**
   * 選手一覧を取得する.
   */
  @GetMapping("/api/players")
  public List<PlayerResponse> index(@RequestParam(required = false) String type) {
    return playerService.findAll(type).stream()
        .map(PlayerResponse::from)
        .toList();
  }

  @PostMapping("/api/players")
  @ResponseStatus(HttpStatus.CREATED)
  public PlayerResponse create(@Valid @RequestBody PlayerRequest request) {
    return PlayerResponse.from(playerService.create(request));
  }

  @GetMapping("/api/players/{id}")
  public PlayerResponse show(@PathVariable Long id) {
    return PlayerResponse.from(playerService.findById(id));
  }

  @PutMapping("/api/players/{id}")
  public PlayerResponse update(@PathVariable Long id, @Valid @RequestBody PlayerRequest request) {
    return PlayerResponse.from(playerService.update(id, request));
  }

  @DeleteMapping("/api/players/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void destroy(@PathVariable Long id) {
    playerService.delete(id);
  }
}
