package com.prabwork.ai.imageai

data class ImageMetadataResponse(
    var description: String? = null,
    var analysis: String? = null,
    var detectedElements: String? = null,
    var colors: String? = null,
    var composition: String? = null,
    var quality: String? = null,
    var contextualInfo: String? = null,
    var additionalDetails: String? = null,
)

data class ImageAnalysisRequest (
    val imageUrl: String? = null,
    val customQuery: String? = null,
    val detailedAnalysis: Boolean = false,
)