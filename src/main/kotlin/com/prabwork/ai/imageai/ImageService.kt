package com.prabwork.ai.imageai

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.ChatClient.PromptUserSpec
import org.springframework.ai.content.Media
import org.springframework.ai.converter.BeanOutputConverter
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.api.ResponseFormat
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.util.MimeTypeUtils
import org.springframework.web.multipart.MultipartFile
import java.net.MalformedURLException
import java.util.Locale
import java.util.function.Consumer


class ImageService(val chatClient: ChatClient) {

    fun analyzeImageFromFile(file: MultipartFile): ImageMetadataResponse {
        try {
            val imageMedia: Media = Media(
                MimeTypeUtils.parseMimeType(file.getContentType()!!),
                file.getResource()
            )

            return performAnalysis(imageMedia, IMAGE_METADATA_PROMPT)
        } catch (e: Exception) {
            throw RuntimeException("Failed to analyze image from file: " + e.message, e)
        }
    }

    fun analyzeImageFromUrl(imageUrl: String): ImageMetadataResponse {
        try {
            val imageResource: Resource = UrlResource(imageUrl)

            val mimeType = determineMimeType(imageUrl)
            val imageMedia: Media = Media(MimeTypeUtils.parseMimeType(mimeType), imageResource)

            return performAnalysis(imageMedia, IMAGE_METADATA_PROMPT)
        } catch (e: MalformedURLException) {
            throw RuntimeException("Invalid image URL: " + imageUrl, e)
        }
    }

    fun analyzeImageWithCustomQuery(file: MultipartFile, customQuery: String): String? {
        try {
            val imageMedia: Media = Media(
                MimeTypeUtils.parseMimeType(file.getContentType()!!),
                file.getResource()
            )

            return chatClient.prompt()
                .user(Consumer { userSpec: PromptUserSpec? ->
                    userSpec!!
                        .text(customQuery)
                        .media(imageMedia)
                })
                .call()
                .content()
        } catch (e: Exception) {
            throw RuntimeException("Failed to analyze image with custom query", e)
        }
    }

    fun analyzeImageFromUrlWithQuery(imageUrl: String, customQuery: String): String? {
        try {
            val imageResource: Resource = UrlResource(imageUrl)
            val mimeType = determineMimeType(imageUrl)
            val imageMedia: Media = Media(MimeTypeUtils.parseMimeType(mimeType), imageResource)

            return chatClient.prompt()
                .user(Consumer { userSpec: PromptUserSpec? ->
                    userSpec!!
                        .text(customQuery)
                        .media(imageMedia)
                })
                .call()
                .content()
        } catch (e: MalformedURLException) {
            throw RuntimeException("Invalid image URL: " + imageUrl, e)
        }
    }

    private fun performAnalysis(imageMedia: Media?, systemPrompt: String): ImageMetadataResponse {
        val responseFormat = ResponseFormat.builder()
            .type(ResponseFormat.Type.JSON_SCHEMA)
            .jsonSchema(jsonSchema)
            .build()

        val chatOptions = OpenAiChatOptions.builder()
            .responseFormat(responseFormat)
            .build()
        val response = chatClient.prompt()
            .system(systemPrompt)
            .user(Consumer { userSpec: PromptUserSpec ->
                userSpec
                    .text("Analyze this image and provide comprehensive metadata.")
                    .media(imageMedia)
            })
            .options(chatOptions)
            .call()
            .content()
        println(response)
        return parseResponse(response)
    }

    private fun parseResponse(response: String?): ImageMetadataResponse {
        if (response == null) {
            return ImageMetadataResponse()
        }
        val converter: BeanOutputConverter<ImageMetadataResponse> =
            BeanOutputConverter(ImageMetadataResponse::class.java)
        return converter.convert(response)!!
    }

    private fun determineMimeType(url: String): String {
        val lowerUrl = url.lowercase(Locale.getDefault())
        if (lowerUrl.endsWith(".png")) return "image/png"
        if (lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg")) return "image/jpeg"
        if (lowerUrl.endsWith(".gif")) return "image/gif"
        if (lowerUrl.endsWith(".webp")) return "image/webp"
        return "image/jpeg"
    }

    companion object {
        val jsonSchema = """
            {
              "type": "object",
              "properties": {
                "description": { "type": ["string", "null"] },
                "analysis": { "type": ["string", "null"] },
                "detectedElements": { "type": ["string", "null"] },
                "colors": { "type": ["string", "null"] },
                "composition": { "type": ["string", "null"] },
                "quality": { "type": ["string", "null"] },
                "contextualInfo": { "type": ["string", "null"] },
                "additionalDetails": { "type": ["string", "null"] }
              },
              "required": []
            }
        """.trimIndent()

        val IMAGE_METADATA_PROMPT = """
            You are an expert image analyst. Analyze the provided image and respond ONLY in raw JSON format, no markdown, no explanations.
        
            The JSON response must follow this exact structure:
        
            {
              "type": "object",
              "properties": {
                "description": { "type": ["string", "null"] },
                "analysis": { "type": ["string", "null"] },
                "detectedElements": { "type": ["string", "null"] },
                "colors": { "type": ["string", "null"] },
                "composition": { "type": ["string", "null"] },
                "quality": { "type": ["string", "null"] },
                "contextualInfo": { "type": ["string", "null"] },
                "additionalDetails": { "type": ["string", "null"] }
              },
              "required": []
            }
        
            Do NOT include any text or markdown before or after the JSON.
        """.trimIndent()
    }
}