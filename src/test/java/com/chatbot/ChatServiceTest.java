package com.chatbot;

import com.chatbot.model.ChatRequest;
import com.chatbot.model.ChatResponse;
import com.chatbot.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ai.chat.client.ChatClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ChatServiceTest {

    @MockBean
    private ChatClient chatClient;

    @Autowired
    private ChatService chatService;

    @Test
    void contextLoads() {
        // Verify Spring context starts without errors
        assertThat(chatService).isNotNull();
    }
}
