package com.example.marvelapp.framework.network.response

data class CharacterResponse(
    val id: Int,
    val name: String,
    val description: String,
    val modified: String,
    val thumbnailResponse: ThumbnailResponse
)