package edu.calpoly.csc3100.smarthome.mcp;

import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServer.SingleSessionSyncSpecification;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.JsonSchema;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

/**
 * Starts the MCP server over stdin/stdout and advertises the smart home tools.
 * Same structure as the course's mcp-paint example (MCPHandler).
 *
 * <p>IMPORTANT: stdout carries the JSON-RPC conversation with the AI client. Never print to
 * System.out in this program; log with System.err instead or the client will disconnect.
 *
 * @author Marcus Hauen-Limkilde
 * @version 1.0
 */
public final class SmartHomeMcpHandler {
  private final LightTools tools;

  public SmartHomeMcpHandler(LightTools tools) {
    this.tools = tools;
  }

  public McpSyncServer run() {
    JsonMapper jsonMapper = JsonMapper.builder().build();
    StdioServerTransportProvider transport =
        new StdioServerTransportProvider(new JacksonMcpJsonMapper(jsonMapper));
    SingleSessionSyncSpecification session = McpServer.sync(transport);
    session
        .serverInfo("smart-home", "1.0.0")
        .capabilities(ServerCapabilities.builder().tools(true).build());
    registerTools(session);
    return session.build();
  }

  private void registerTools(SingleSessionSyncSpecification session) {
    // The description is the "menu" the AI reads to decide when to call the tool.
    Tool setLight = createTool(
        "set_light",
        "Turn a room's light ON or OFF in the smart home simulator. "
            + "Sends a real MQTT command to the device simulator.",
        Map.of(
            "room", Map.of(
                "type", "string",
                "enum", List.copyOf(Topics.ROOMS),
                "description", "Room id"),
            "state", Map.of(
                "type", "string",
                "enum", List.of("ON", "OFF"),
                "description", "Desired light state")));
    session.toolCall(setLight, tools::handleSetLight);

    Tool getLightStatus = createTool(
        "get_light_status",
        "Get the last light state (ON/OFF) the smart home simulator reported for a room. "
            + "Use after set_light to confirm the change actually happened.",
        Map.of(
            "room", Map.of(
                "type", "string",
                "enum", List.copyOf(Topics.ROOMS),
                "description", "Room id")));
    session.toolCall(getLightStatus, tools::handleGetLightStatus);
  }

  private static Tool createTool(String name, String description, Map<String, Object> properties) {
    JsonSchema schema = new JsonSchema("object", properties, List.copyOf(properties.keySet()), false, null, null);
    return Tool.builder().name(name).description(description).inputSchema(schema).build();
  }
}
