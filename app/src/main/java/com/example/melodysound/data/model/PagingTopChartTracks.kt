package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class PagingTopChartTracks (
    @SerializedName("href")
    val href: String,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("next")
    val next: String?, // Có thể null
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("previous")
    val previous: String?, // Có thể null
    @SerializedName("total")
    val total: Int,
    @SerializedName("items")
    val items: List<PlaylistTrack> // Danh sách các bài hát
)