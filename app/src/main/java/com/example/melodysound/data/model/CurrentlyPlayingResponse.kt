package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class CurrentlyPlayingResponse(
    @SerializedName("item") val track: TrackItem?
)
