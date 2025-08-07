package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class Copyright(
    @SerializedName("text")
    val text: String,
    @SerializedName("type")
    val type: String
)