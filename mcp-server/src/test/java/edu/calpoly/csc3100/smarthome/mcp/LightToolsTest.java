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
    // TODO: tools.setLight("livingroom", "ON");
    // TODO: assert exactly one message was sent, with
    //       topic   "smarthome/livingroom/light/set"
    //       payload {"state":"ON"}
  }

  @Test
  void inputIsNormalized() {
    // TODO: tools.setLight(" LivingRoom ", "off") should publish {"state":"OFF"}
    //       to smarthome/livingroom/light/set
  }

  @Test
  void unknownRoomIsRejectedAndNothingIsPublished() {
    // TODO: assertThrows(IllegalArgumentException.class, () -> tools.setLight("garage", "ON"));
    // TODO: assert sent is empty
  }

  @Test
  void invalidStateIsRejectedAndNothingIsPublished() {
    // TODO: try "DIM" and a non-String such as 1; both throw, sent stays empty
  }
}
