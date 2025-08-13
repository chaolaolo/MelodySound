package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class PlaylistResponse(
    @SerializedName("collaborative")
    val collaborative: Boolean,
    @SerializedName("description")
    val description: String,
    @SerializedName("external_urls")
    val externalUrls: ExternalUrls,
    @SerializedName("followers")
    val followers: Followers,
    @SerializedName("href")
    val href: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("images")
    val images: List<Image>,
    @SerializedName("name")
    val name: String,
    @SerializedName("owner")
    val owner: Owner,
    @SerializedName("primary_color")
    val primaryColor: String?, // Trường này có thể null
    @SerializedName("public")
    val public: Boolean,
    @SerializedName("snapshot_id")
    val snapshotId: String,
    @SerializedName("tracks")
    val tracks: PagingTrackResponse, // Sử dụng data class mới
    @SerializedName("type")
    val type: String,
    @SerializedName("uri")
    val uri: String
)

data class Owner(
    val display_name: String,
    val external_urls: ExternalUrls,
    val href: String,
    val id: String,
    val type: String,
    val uri: String
)