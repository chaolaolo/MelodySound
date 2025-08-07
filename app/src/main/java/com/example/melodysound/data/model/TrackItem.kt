package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class TrackItem(
    @SerializedName("album")
    val album: AlbumItem,
    @SerializedName("artists")
    val artists: List<Artist>, // Có thể tái sử dụng Artist data class đã tạo
    @SerializedName("available_markets")
    val availableMarkets: List<String>,
    @SerializedName("disc_number")
    val discNumber: Int,
    @SerializedName("duration_ms")
    val durationMs: Int,
    @SerializedName("explicit")
    val explicit: Boolean,
    @SerializedName("external_urls")
    val externalUrls: ExternalUrls, // Tái sử dụng ExternalUrls
    @SerializedName("href")
    val href: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("linked_from")
    val linkedFrom: LinkedFrom?, // Có thể null
    @SerializedName("name")
    val name: String,
    @SerializedName("preview_url")
    val previewUrl: String?, // Có thể null
    @SerializedName("track_number")
    val trackNumber: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("uri")
    val uri: String,
    @SerializedName("is_local")
    val isLocal: Boolean,
    @SerializedName("restrictions")
    val restrictions: Restrictions? // Có thể null
)