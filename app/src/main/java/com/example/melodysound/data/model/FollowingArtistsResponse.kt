package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class FollowingArtistsResponse(
    @SerializedName("artists")
    val artists: PagingArtists
)