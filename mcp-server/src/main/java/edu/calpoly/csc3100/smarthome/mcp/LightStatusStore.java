package edu.calpoly.csc3100.smarthome.mcp;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Remembers the last light state the simulator reported for each room.
 * Written by the MQTT thread, read by the MCP thread, so it uses a ConcurrentHashMap.
 *
 * @author Marcus Hauen-Limkilde
 * @version 1.0
 */
public final class LightStatusStore {
  private final Map<String, String> latest = new ConcurrentHashMap<>();

  /** Records that the simulator reported {@code state} ("ON"/"OFF") for {@code room}. */
  public void update(String room, String state) {
    latest.put(room, state);
  }

  /** The last reported state for a room, or empty if the simulator never reported it. */
  public Optional<String> latest(String room) {
    return Optional.ofNullable(latest.get(room));
  }
}
