package com.example.melodysound.data.model

data class Podcast (
    val id: String,
    val title: String,
    val episodeTitle: String,
    val thumbnailUrl: String,
    val publishDate: String,
    val duration: String,
    val description: String,
    val isPlaying: Boolean = false,
    val isMuted: Boolean = false
)