package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class PagingArtists(
    @SerializedName("href")
    val href: String,
    @SerializedName("items")
    val items: List<Artist>, // Danh sách các nghệ sĩ
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("next")
    val next: String?,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("previous")
    val previous: String?,
    @SerializedName("total")
    val total: Int
)
