package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class PagingPlaylistsResponse(
    @SerializedName("items")
    val items: List<PlaylistResponse>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("previous")
    val previous: String?,
    @SerializedName("next")
    val next: String?,
    @SerializedName("href")
    val href: String?,
)