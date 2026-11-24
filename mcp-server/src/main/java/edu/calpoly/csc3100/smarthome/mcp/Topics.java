package edu.calpoly.csc3100.smarthome.mcp;

import java.util.Objects;
import java.util.Set;

/**
 * The MQTT topic contract shared by the dashboard, the simulator, and this MCP server.
 * Commands go to {@code <root>/<room>/light/set}; the simulator answers on
 * {@code <root>/<room>/light/status}. Payloads are JSON such as {@code {"state":"ON"}}.
 *
 * @author Marcus Hauen-Limkilde
 * @version 1.0
 */
public final class Topics {
  /** Matches the proposal. On the public broker, prefer a team-unique root (set TOPIC_ROOT). */
  public static final String DEFAULT_ROOT = "smarthome";
  /** Rooms the simulator knows about. Keep in sync with the team. */
  public static final Set<String> ROOMS = Set.of("livingroom", "bedroom", "kitchen");

  private final String root;

  public Topics(String root) {
    this.root = Objects.requireNonNull(root);
  }

  /** Where commands for one room's light are published. */
  public String lightSet(String room) {
    return root + "/" + room + "/light/set";
  }

  /** Where the simulator reports one room's light state. */
  public String lightStatus(String room) {
    return root + "/" + room + "/light/status";
  }

  /** Subscribes to every room's light status at once (used in Story 6). */
  public String allLightStatus() {
    return root + "/+/light/status";
  }
}
