package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("height")
    val height: Int?, // Có thể null
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Int? // Có thể null
)
