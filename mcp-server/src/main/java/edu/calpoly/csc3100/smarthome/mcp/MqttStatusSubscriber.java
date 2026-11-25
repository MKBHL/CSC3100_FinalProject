package edu.calpoly.csc3100.smarthome.mcp;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Subscribes to every room's light status and hands each message to a LightStatusListener.
 *
 * @author Marcus Hauen-Limkilde
 * @version 1.0
 */
public final class MqttStatusSubscriber implements AutoCloseable {
  private final MqttClient client;

  public MqttStatusSubscriber(String broker, Topics topics, LightStatusListener listener) throws MqttException {
    client = new MqttClient(broker, "smarthome-mcp-status-" + UUID.randomUUID(), new MemoryPersistence());
    MqttConnectOptions options = new MqttConnectOptions();
    options.setCleanSession(true);
    options.setAutomaticReconnect(true);
    client.connect(options);

    // Wildcard "+" matches one level, so this receives livingroom, bedroom and kitchen status.
    // If the simulator publishes status as RETAINED, we also get the current state right away.
    client.subscribe(topics.allLightStatus(), 1, (topic, message) ->
        listener.accept(topic, new String(message.getPayload(), StandardCharsets.UTF_8)));
  }

  @Override
  public void close() throws MqttException {
    if (client.isConnected()) {
      client.disconnect();
    }
    client.close();
  }
}
