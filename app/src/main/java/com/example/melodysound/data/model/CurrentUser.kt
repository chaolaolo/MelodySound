package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class CurrentUser(
    @SerializedName("display_name")
    val displayName: String,
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
    @SerializedName("type")
    val type: String,
    @SerializedName("uri")
    val uri: String
)
