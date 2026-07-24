package com.example.mcpserver.config;

import com.example.mcpserver.todo.TodoService;
import com.example.mcpserver.weather.WeatherService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolConfig {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(
            WeatherService weatherService,
            TodoService todoService) {

        return MethodToolCallbackProvider.builder()
                .toolObjects(weatherService, todoService)
                .build();
    }
}
