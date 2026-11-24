package edu.calpoly.csc3100.smarthome.mcp;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Story 6 tests: status messages update the store only when topic and payload are valid.
 *
 * @author Marcus Hauen-Limkilde
 * @version 1.0
 */
class LightStatusListenerTest {
  private final LightStatusStore store = new LightStatusStore();
  private final LightStatusListener listener = new LightStatusListener(new Topics("smarthome"), store);

    @Test
  void validStatusIsStored() {
    assertTrue(listener.accept("smarthome/kitchen/light/status", "{\"state\":\"ON\"}"));
    assertEquals(Optional.of("ON"), store.latest("kitchen"));
  }

  @Test
  void newerStatusReplacesOlder() {
    listener.accept("smarthome/kitchen/light/status", "{\"state\":\"ON\"}");
    listener.accept("smarthome/kitchen/light/status", "{\"state\":\"OFF\"}");
    assertEquals(Optional.of("OFF"), store.latest("kitchen"));
  }

  @Test
  void unknownRoomOrOtherTopicIsIgnored() {
    assertFalse(listener.accept("smarthome/garage/light/status", "{\"state\":\"ON\"}"));
    assertFalse(listener.accept("smarthome/kitchen/light/set", "{\"state\":\"ON\"}"));  // a command, not a status
    assertEquals(Optional.empty(), store.latest("kitchen"));
  }

  @Test
  void malformedPayloadIsIgnored() {
    assertFalse(listener.accept("smarthome/kitchen/light/status", "not json"));
    assertFalse(listener.accept("smarthome/kitchen/light/status", "{}"));
    assertFalse(listener.accept("smarthome/kitchen/light/status", "{\"state\":\"DIM\"}"));
    assertEquals(Optional.empty(), store.latest("kitchen"));
  }
}
