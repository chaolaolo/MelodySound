package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName
data class AlbumFull(
    @SerializedName("album_type")
    val albumType: String,
    @SerializedName("total_tracks")
    val totalTracks: Int,
    @SerializedName("available_markets")
    val availableMarkets: List<String>,
    @SerializedName("external_urls")
    val externalUrls: ExternalUrls, // Tái sử dụng ExternalUrls
    @SerializedName("href")
    val href: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("images")
    val images: List<Image>, // Tái sử dụng Image
    @SerializedName("name")
    val name: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("release_date_precision")
    val releaseDatePrecision: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("uri")
    val uri: String,
    @SerializedName("artists")
    val artists: List<Artist>, // Tái sử dụng Artist
    @SerializedName("tracks")
    val tracks: PagingTracks,
    @SerializedName("copyrights")
    val copyrights: List<Copyright>,
    @SerializedName("external_ids")
    val externalIds: ExternalIdsAlbum,
    @SerializedName("genres")
    val genres: List<String>,
    @SerializedName("label")
    val label: String,
    @SerializedName("popularity")
    val popularity: Int
)


