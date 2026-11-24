package edu.calpoly.csc3100.smarthome.mcp;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
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
    MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));

    message.setQos(1);
    message.setRetained(false);

    try {
      client.publish(topic, message);
    } 
    catch (MqttException e) {
      throw new IllegalStateException("Could not reach the MQTT broker: " + e.getMessage(), e);
    }
  }

  @Override
  public void close() throws MqttException {
    if (client.isConnected()) {
      client.disconnect();
    }
    client.close();
  }
}
