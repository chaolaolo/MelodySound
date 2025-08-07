package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class AlbumList(
    @SerializedName("href")
    val href: String,
    @SerializedName("items")
    val items: List<AlbumItem>,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("next")
    val next: String?, // Có thể null
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("previous")
    val previous: String?, // Có thể null
    @SerializedName("total")
    val total: Int
)
