package edu.calpoly.csc3100.smarthome.mcp;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.Objects;

/**
 * Turns one MQTT status message into a store update. Kept separate from Paho so tests can
 * call {@link #accept(String, String)} directly with a topic and payload, no broker needed.
 *
 * <p>Expected input: topic {@code <root>/<room>/light/status}, payload {@code {"state":"ON"}}.
 * Anything else is ignored (and never throws), because an exception here would break the
 * MQTT callback thread and stop all later status updates.
 *
 * @author Marcus Hauen-Limkilde
 * @version 1.0
 */
public final class LightStatusListener {
  private static final JsonMapper JSON = JsonMapper.builder().build();

  private final Topics topics;
  private final LightStatusStore store;

  public LightStatusListener(Topics topics, LightStatusStore store) {
    this.topics = Objects.requireNonNull(topics);
    this.store = Objects.requireNonNull(store);
  }

  /** Returns true if the message was a valid status and the store was updated. */
    public boolean accept(String topic, String payload) {
    String room = null;
    for (String candidate : Topics.ROOMS) {
      if (topic.equals(topics.lightStatus(candidate))) {
        room = candidate;
      }
    }
    if (room == null) { return false; }

    String state = stateFrom(payload);

    if (!"ON".equals(state) && !"OFF".equals(state)) {
      return false;
    }
    store.update(room, state);
    return true;
  }

  /** Pulls "state" out of {"state":"ON"}; returns null for bad JSON or a missing field. */
  static String stateFrom(String payload) {
    try {
      JsonNode state = JSON.readTree(payload).get("state");
      return state != null && state.isString() ? state.asString().trim().toUpperCase(java.util.Locale.ROOT) : null;
    } catch (JacksonException e) {
      return null;
    }
  }
}
