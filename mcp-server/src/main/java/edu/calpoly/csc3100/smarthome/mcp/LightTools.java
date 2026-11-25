package edu.calpoly.csc3100.smarthome.mcp;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * The work behind each MCP tool. The handle* methods adapt MCP requests; the plain methods
 * hold the logic so JUnit can test them without an MCP client or a broker.
 *
 * @author Marcus Hauen-Limkilde
 * @version 1.0
 */
public final class LightTools {
  private final Topics topics;
  private final LightCommandPublisher publisher;
  private final LightStatusStore statuses;

  public LightTools(Topics topics, LightCommandPublisher publisher, LightStatusStore statuses) {
    this.topics = Objects.requireNonNull(topics);
    this.publisher = Objects.requireNonNull(publisher);
    this.statuses = Objects.requireNonNull(statuses);
  }

  /** Convenience for code that only sends commands (keeps the Story 5 tests unchanged). */
  public LightTools(Topics topics, LightCommandPublisher publisher) {
    this(topics, publisher, new LightStatusStore());
  }

  /** MCP entry point for set_light. Bad input becomes an error result, never an exception. */
  public CallToolResult handleSetLight(McpSyncServerExchange exchange, CallToolRequest request) {
    Map<String, Object> arguments = request.arguments();
    try {
      return result(setLight(arguments.get("room"), arguments.get("state")), false);
    } catch (IllegalArgumentException | IllegalStateException e) {
      return result(e.getMessage(), true);
    }
  }

  /**
   * Validates the arguments, publishes exactly one command, and returns a sentence for the AI. -> Throws IllegalArgumentException (and publishes nothing) when an argument is invalid.
   */
  String setLight(Object room, Object state) {
    String validRoom = room(room);
    String validState = state(state);
    publisher.publish(topics.lightSet(validRoom), "{\"state\":\"" + validState + "\"}");
    return "Sent " + validState + " to the " + validRoom + " light.";
  }

  /** MCP entry point for get_light_status. A room with no report yet is an answer, not an error. */
  public CallToolResult handleGetLightStatus(McpSyncServerExchange exchange, CallToolRequest request) {
    try {
      return result(getLightStatus(request.arguments().get("room")), false);
    } catch (IllegalArgumentException e) {
      return result(e.getMessage(), true);
    }
  }

  /** Reports the last state the simulator confirmed for a room (never a guessed value). */
  String getLightStatus(Object room) {
    String validRoom = room(room);
    Optional<String> latestState = statuses.latest(validRoom);
    
    if (latestState.isPresent()) {
      return "The " + validRoom + " light is " + latestState.get() + " (last reported by the simulator).";
    }
    return "No status has been reported for the " + validRoom + " light yet.";
  }

  /** Accepts "LivingRoom", " livingroom " etc.; returns the lowercase room id. */
  static String room(Object value) {
    if (!(value instanceof String s)) throw new IllegalArgumentException("Room must be text.");
    
    String room = s.trim().toLowerCase(java.util.Locale.ROOT);

    if (!Topics.ROOMS.contains(room)) {
      throw new IllegalArgumentException("Invalid room: " + room + ". Valid rooms: " + Topics.ROOMS);
    }
    return room;
  }

  /** Accepts "on", "Off" etc.; returns "ON" or "OFF". */
  static String state(Object value) {
     if (!(value instanceof String s)) {
      throw new IllegalArgumentException("State must be ON or OFF.");
    }
    String state = s.trim().toUpperCase(java.util.Locale.ROOT);
    
    if (!state.equals("ON") && !state.equals("OFF")) {
      throw new IllegalArgumentException("State must be ON or OFF, Invalid state: " + state);
    }
    return state;
  }

  private static CallToolResult result(String message, boolean isError) {
    return CallToolResult.builder()
        .content(List.of(new TextContent(message)))
        .isError(isError)
        .build();
  }
}
