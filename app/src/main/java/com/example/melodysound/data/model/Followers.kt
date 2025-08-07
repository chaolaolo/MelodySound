package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class Followers(
    @SerializedName("href")
    val href: String?, // Có thể null
    @SerializedName("total")
    val total: Int
)
