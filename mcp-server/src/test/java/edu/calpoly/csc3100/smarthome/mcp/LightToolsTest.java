package edu.calpoly.csc3100.smarthome.mcp;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Story 5 tests: set_light publishes exactly one correct command for valid input and
 * nothing for invalid input. A recording lambda stands in for the MQTT broker.
 *
 * @author Marcus Hauen-Limkilde
 * @version 1.0
 */
class LightToolsTest {
  private record Sent(String topic, String payload) { }

  private final List<Sent> sent = new ArrayList<>();
  private final LightTools tools =
      new LightTools(new Topics("smarthome"), (topic, payload) -> sent.add(new Sent(topic, payload)));

  @Test
  void validCommandPublishesOneMessageToTheRoomTopic() {
    tools.setLight("livingroom", "ON");
    assertEquals(
      List.of(new Sent("smarthome/livingroom/light/set", "{\"state\":\"ON\"}")), sent);
  }

  @Test
  void inputIsNormalized() {
    tools.setLight(" LivingRoom ", "off");
    assertEquals(
      List.of(new Sent("smarthome/livingroom/light/set", "{\"state\":\"OFF\"}")), sent);
  }

  @Test
  void unknownRoomIsRejectedAndNothingIsPublished() {
    assertThrows(IllegalArgumentException.class, () -> tools.setLight("garage", "ON"));
    assertTrue(sent.isEmpty());
  }

  @Test
  void invalidStateIsRejectedAndNothingIsPublished() {
    assertThrows(IllegalArgumentException.class, () -> tools.setLight("livingroom", "DIM"));
    assertThrows(IllegalArgumentException.class, () -> tools.setLight("livingroom", 1));
    assertTrue(sent.isEmpty());
  }
}
