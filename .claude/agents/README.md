# Agents

Specialized subagents for the Coursivo backend. Invoke via Claude Code for focused review tasks.

## Available Agents

| Agent | Description |
|---|---|
| `backend-reviewer` | Reviews controllers, services, repositories for architecture, DTO boundary, validation, and API standard violations |
| `security-auditor` | Audits for hardcoded secrets, broken auth, insecure endpoints, OWASP Top 10 |
| `test-writer` | Writes and places JUnit 5 unit + MockMvc integration tests for any service or controller |
| `kafka-reviewer` | Reviews Kafka producers, consumers, event classes, and retry/DLQ config for correctness and idempotency |

## Usage

Ask Claude to use a specific agent by name, or Claude will select the appropriate one based on context.

Examples:
- "Review EnrollmentController with the backend-reviewer"
- "Audit KafkaRetryConfig with the kafka-reviewer"
- "Write tests for EnrollmentService using the test-writer"
- "Run a security audit on AuthController"
