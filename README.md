# Spring AI Chatbot

A production-ready chatbot built with **Spring Boot 3** and **Spring AI**, powered by OpenAI GPT.

## Features

- Single-turn and multi-turn conversation with memory
- Streaming responses via Server-Sent Events (SSE)
- Built-in chat UI (served at `http://localhost:8080`)
- REST API for integration with any frontend
- Conversation history management (clear/reset sessions)
- Global exception handling
- Actuator health endpoints

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.3.4 |
| AI Client | Spring AI 1.0.0-M3 |
| AI Provider | OpenAI (gpt-4o-mini) |
| Language | Java 17 |
| Build | Maven |

---

## Project Structure

```
src/
├── main/
│   ├── java/com/chatbot/
│   │   ├── ChatbotApplication.java        # Entry point
│   │   ├── controller/
│   │   │   └── ChatController.java        # REST endpoints
│   │   ├── service/
│   │   │   └── ChatService.java           # AI logic + memory
│   │   ├── model/
│   │   │   ├── ChatRequest.java
│   │   │   ├── ChatResponse.java
│   │   │   └── ChatMessage.java
│   │   └── config/
│   │       ├── ChatConfig.java            # Spring AI bean setup
│   │       └── GlobalExceptionHandler.java
│   └── resources/
│       ├── application.yml
│       └── static/index.html              # Chat UI
```

---

## Getting Started

### 1. Set your OpenAI API key

**Option A — Environment variable (recommended):**
```bash
export OPENAI_API_KEY=sk-your-key-here
```

**Option B — application.yml directly:**
```yaml
spring:
  ai:
    openai:
      api-key: sk-your-key-here
```

### 2. Run the app

```bash
mvn spring-boot:run
```

App starts at: `http://localhost:8080`

---

## REST API

### POST /api/chat — Send a message

```json
// Request
{
  "message": "What is Spring AI?",
  "conversationId": "abc-123"   // optional — omit for stateless
}

// Response
{
  "reply": "Spring AI is a framework that...",
  "conversationId": "abc-123",
  "timestamp": "2025-01-01T10:00:00"
}
```

### POST /api/chat/stream — Streaming via SSE

```json
// Request body
{ "message": "Tell me a story" }

// Response: stream of text tokens via Server-Sent Events
```

### GET /api/chat/{conversationId}/history

Returns the full message history for a session.

### DELETE /api/chat/{conversationId}

Clears conversation memory for a session.

---

## Changing the AI Model

In `application.yml`:

```yaml
spring:
  ai:
    openai:
      chat:
        options:
          model: gpt-4o          # or gpt-4o-mini, gpt-3.5-turbo
          temperature: 0.7
```

---

## Customising the System Prompt

In `application.yml`:

```yaml
chatbot:
  system-prompt: >
    You are a Java expert assistant specialising in Spring Boot.
    Always provide code examples when explaining concepts.
```

---

## Production Notes

- Replace the in-memory `conversationStore` in `ChatService` with **Redis** for scalability
- Add Spring Security for API authentication
- Use `@ConfigurationProperties` for typed config binding
- Add rate limiting with Spring's `RateLimiter` or Bucket4j
