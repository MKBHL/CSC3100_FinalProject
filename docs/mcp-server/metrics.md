# MCP Server — Metrics

Scope: module `mcp-server`, package `edu.calpoly.csc3100.smarthome.mcp`, production code only
(test sources excluded). Tool: **MetricsReloaded** (IntelliJ IDEA), profiles "Complexity metrics"
and "Martin packaging metrics". Owner: Marcus Hauen-Limkilde.

## Package metrics (Martin)

| Package | A | Ca | Ce | I | D |
|---|---|---|---|---|---|
| `edu.calpoly.csc3100.smarthome.mcp` | 0.11 | 0 | 0 | 1.00 | 0.89 |

- **A = 0.11:** 1 of 9 types is abstract (`LightCommandPublisher`, an interface).
- **Ca = 0, Ce = 0:** MetricsReloaded counts only dependencies between packages *inside the
  project*. The MCP server is a single package and no other project package depends on it yet,
  so both couplings are 0. (Its real dependencies are libraries: the MCP SDK, Jackson, and Paho.)
- **I and D:** with Ca = Ce = 0, instability Ce / (Ca + Ce) is 0 / 0, i.e. undefined. The tool
  displays I = 1.00 but computes D = |A + I − 1| as if I = 0, which gives D = 0.89. Read literally,
  D = 0.89 would put the package in the "zone of pain" (concrete and heavily depended on), but
  that is an artifact of the 0 / 0 case: nothing depends on this package. If library packages are
  counted as efferent coupling (Ce > 0, so I = 1), D = |0.11 + 1 − 1| = **0.11**, close to the main
  sequence — the expected result for a leaf application module.

## Method metrics

| Method | CogC | ev(G) | iv(G) | v(G) |
|---|---|---|---|---|
| LightStatusListener.accept(String, String) | 6 | 3 | 3 | **6** |
| LightStatusListener.stateFrom(String) | 3 | 1 | 3 | 4 |
| LightTools.state(Object) | 3 | 3 | 2 | 4 |
| LightTools.room(Object) | 2 | 3 | 1 | 3 |
| McpServerMain.main(String[]) | 2 | 1 | 2 | 2 |
| LightTools.getLightStatus(Object) | 1 | 2 | 2 | 2 |
| LightTools.handleGetLightStatus(...) | 1 | 1 | 2 | 2 |
| LightTools.handleSetLight(...) | 1 | 1 | 2 | 2 |
| MqttLightCommandPublisher.publish(String, String) | 1 | 1 | 2 | 2 |
| MqttLightCommandPublisher.close() | 1 | 1 | 2 | 2 |
| MqttStatusSubscriber.close() | 1 | 1 | 2 | 2 |
| 18 other methods and constructors | 0 | 1 | 1 | 1 |
| **Total (29 methods)** | 22 | 36 | 41 | **49** |
| **Average** | 0.76 | 1.24 | 1.41 | **1.69** |

v(G) = cyclomatic complexity (CC), ev(G) = essential complexity, iv(G) = design complexity,
CogC = cognitive complexity.

- **Highest CC:** `LightStatusListener.accept` (v(G) = 6): a loop over rooms plus the topic,
  room-found, and valid-state checks. Every branch is covered by `LightStatusListenerTest`.
- **Average CC 1.69**; no method is near 10, the usual threshold for refactoring.
