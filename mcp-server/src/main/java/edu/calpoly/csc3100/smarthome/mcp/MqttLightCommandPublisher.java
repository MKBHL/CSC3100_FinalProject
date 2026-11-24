package edu.calpoly.csc3100.smarthome.mcp;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

/**
 * Publishes light commands to the MQTT broker with Paho.
 *
 * @author Marcus Hauen-Limkilde
 * @version 1.0
 */
public final class MqttLightCommandPublisher implements LightCommandPublisher, AutoCloseable {
  private final MqttClient client;

  public MqttLightCommandPublisher(String broker) throws MqttException {
    // Unique client id: two clients with the same id kick each other off the broker.
    client = new MqttClient(broker, "smarthome-mcp-" + UUID.randomUUID(), new MemoryPersistence());
    MqttConnectOptions options = new MqttConnectOptions();
    options.setCleanSession(true);
    options.setAutomaticReconnect(true);
    client.connect(options);
  }

  @Override
  public void publish(String topic, String payload) {
    // TODO (Story 5):
    //  1. Build an MqttMessage from payload's UTF-8 bytes.
    //  2. QoS 1 (delivered at least once), NOT retained: a command is a one-time action,
    //     and a retained "ON" would re-fire every time the simulator reconnects.
    //  3. client.publish(topic, message)
    //  4. Catch MqttException and rethrow as IllegalStateException("Could not reach the broker: ...")
    //     so the MCP tool can report the failure to the AI instead of crashing.
    throw new UnsupportedOperationException("TODO: publish " + topic);
  }

  @Override
  public void close() throws MqttException {
    if (client.isConnected()) {
      client.disconnect();
    }
    client.close();
  }
}
