package com.chatbot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring AI configuration.
 * Builds a ChatClient with a default system prompt.
 */
@Configuration
public class ChatConfig {

    @Value("${chatbot.system-prompt}")
    private String systemPrompt;

    /**
     * ChatClient is the main entry point for interacting with AI models in Spring AI.
     * We set a default system prompt here so every conversation starts with context.
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .build();
    }

    /**
     * CORS config — allows the frontend (served from the same origin or localhost:3000)
     * to call the REST API.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
