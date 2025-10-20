package com.prabwork.ai.imageai

import org.springframework.ai.chat.client.ChatClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ImageAIConfiguration {

    @Bean
    fun chatClient(chatClientBuilder: ChatClient.Builder): ChatClient {
        return chatClientBuilder.build()
    }

    @Bean
    fun imageService(chatClient: ChatClient): ImageService {
        return ImageService(chatClient)
    }

}