package edu.calpoly.csc3100.smarthome.mcp;

import java.util.concurrent.CountDownLatch;

/**
 * Entry point launched by the AI client (e.g. Claude Desktop) as a local process.
 * Environment: MQTT_BROKER (default public HiveMQ), TOPIC_ROOT (default "smarthome").
 *
 * @author Marcus Hauen-Limkilde
 * @version 1.0
 */
public final class McpServerMain {
  private McpServerMain() { }

  public static void main(String[] args) throws Exception {
    String broker = System.getenv().getOrDefault("MQTT_BROKER", "tcp://broker.hivemq.com:1883");
    String root = System.getenv().getOrDefault("TOPIC_ROOT", Topics.DEFAULT_ROOT);

    Topics topics = new Topics(root);
    LightStatusStore statuses = new LightStatusStore();
    MqttLightCommandPublisher publisher = new MqttLightCommandPublisher(broker);
    MqttStatusSubscriber subscriber =
        new MqttStatusSubscriber(broker, topics, new LightStatusListener(topics, statuses));
    LightTools tools = new LightTools(topics, publisher, statuses);
    var server = new SmartHomeMcpHandler(tools).run();
    System.err.println("smart-home MCP server ready (broker=" + broker + ", root=" + root + ")");

    // Keep the process alive until the client closes it, then release the broker connection.
    CountDownLatch stopped = new CountDownLatch(1);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      server.close();
      try {
        subscriber.close();
        publisher.close();
      } catch (Exception e) {
        System.err.println("MQTT close failed: " + e.getMessage());
      }
      stopped.countDown();
    }));
    stopped.await();
  }
}
