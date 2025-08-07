package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class UserTopTracksResponse(
    @SerializedName("items")
    val tracks: List<Track>
)