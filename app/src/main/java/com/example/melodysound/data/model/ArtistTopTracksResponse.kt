package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class ArtistTopTracksResponse(
    @SerializedName("tracks")
    val tracks: List<Track>
)