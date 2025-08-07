package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class Restrictions(
    @SerializedName("reason")
    val reason: String
)