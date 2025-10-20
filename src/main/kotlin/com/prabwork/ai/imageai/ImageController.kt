package com.prabwork.ai.imageai

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/image")
class ImageController(private val imageService: ImageService) {

    @PostMapping("/upload")
    fun analyzeUploadedImage(
        @RequestParam("image") file: MultipartFile
    ): ResponseEntity<ImageMetadataResponse?> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest().build()
        }

        val metadata: ImageMetadataResponse? = imageService.analyzeImageFromFile(file)
        return ResponseEntity.ok(metadata)
    }

    @PostMapping("/url")
    fun analyzeImageFromUrl(
        @RequestBody request: ImageAnalysisRequest
    ): ResponseEntity<ImageMetadataResponse> {
        if (request.imageUrl.isNullOrEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        val metadata: ImageMetadataResponse = imageService.analyzeImageFromUrl(
            request.imageUrl
        )
        return ResponseEntity.ok(metadata)
    }

    @PostMapping("/custom")
    fun analyzeWithCustomQuery(
        @RequestParam("image") file: MultipartFile,
        @RequestParam("query") customQuery: String?
    ): ResponseEntity<String?> {
        if (file.isEmpty || customQuery == null || customQuery.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        val analysis: String? = imageService.analyzeImageWithCustomQuery(file, customQuery)
        return ResponseEntity.ok(analysis)
    }

    @PostMapping("/url-custom")
    fun analyzeUrlWithCustomQuery(
        @RequestBody request: ImageAnalysisRequest
    ): ResponseEntity<String?> {
        if (request.imageUrl == null || request.customQuery == null) {
            return ResponseEntity.badRequest().build()
        }

        val analysis: String? = imageService.analyzeImageFromUrlWithQuery(
            request.imageUrl,
            request.customQuery
        )
        return ResponseEntity.ok(analysis)
    }
}