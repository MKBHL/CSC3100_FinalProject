package edu.calpoly.csc3100.smarthome.mcp;

/**
 * Sends one command message. The MQTT implementation talks to the broker; tests pass a
 * lambda that records what would have been sent, so no broker is needed.
 *
 * @author Marcus Hauen-Limkilde
 * @version 1.0
 */
@FunctionalInterface
public interface LightCommandPublisher {
  void publish(String topic, String payload);
}
