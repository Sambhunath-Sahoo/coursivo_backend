# Agents

Place custom subagent definition files here (`.md` files).

Each agent file defines a specialized AI agent with a specific role, persona, and set of tools it can use. Claude Code can spawn these agents for focused tasks.

## Example: `backend-reviewer.md`

```markdown
---
name: backend-reviewer
description: Reviews Spring Boot code for architecture violations, security issues, and API standards
tools: Read, Grep, Glob
---

You are a Spring Boot code reviewer for the Coursivo backend.
Check for:
- Layer violations (controller calling repository directly)
- Missing @Valid on request bodies
- Entities exposed directly in responses (should use DTOs)
- Hardcoded secrets or config values
- Missing error handling
```

## Usage

Once defined, Claude Code can use these agents automatically or you can invoke them.
