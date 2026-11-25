# MCP Server — Metrics

Scope: package `edu.calpoly.csc3100.smarthome.mcp` (module `mcp-server`), production code only.
Owner: Marcus Hauen-Limkilde.

## Package metrics (A, I, D)

| Metric | Formula | Value |
|---|---|---|
| Abstractness **A** | abstract types / all types = 1 / 9 (`LightCommandPublisher` is the only interface) | **0.11** |
| Afferent coupling **Ca** | other project packages that depend on this one | 0 |
| Efferent coupling **Ce** | outside packages this one depends on (MCP SDK, Jackson, Paho) | > 0 |
| Instability **I** | Ce / (Ca + Ce) | **1.00** |
| Distance **D** | \|A + I − 1\| | **0.11** |

**Reading it:** D close to 0 means the package sits near the "main sequence." It is concrete
(low A) and unstable (I = 1), which is expected for an application leaf module: nothing else
in the project depends on it, so changing it can't break other modules. The one abstraction,
`LightCommandPublisher`, exists so `LightTools` depends on an interface rather than on Paho,
which is what lets the tests run without a broker.

## Method metrics (cyclomatic complexity, CC)

CC = 1 + decision points (`if`, `for`, `while`, `case`, `catch`, `&&`, `||`, `?:`).

| Class | Method | CC |
|---|---|---|
| LightStatusListener | accept | 6 |
| LightStatusListener | stateFrom | 4 |
| LightTools | state | 4 |
| LightTools | room | 3 |
| LightTools | handleSetLight | 2 |
| LightTools | handleGetLightStatus | 2 |
| LightTools | getLightStatus | 2 |
| MqttLightCommandPublisher | publish | 2 |
| MqttLightCommandPublisher | close | 2 |
| MqttStatusSubscriber | close | 2 |
| all other methods and constructors (19) | | 1 |

- **Highest:** `LightStatusListener.accept` (CC 6): one loop over rooms plus the topic,
  room-found, and valid-state checks. Each branch is covered by `LightStatusListenerTest`.
- **Average:** about 1.6 per method. No method exceeds 10, the usual threshold for refactoring.

TODO (Marcus): confirm these numbers with the metrics tool used in class and note the tool here.
