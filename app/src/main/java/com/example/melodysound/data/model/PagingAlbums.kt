package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

data class PagingAlbums(
    @SerializedName("albums")
    val albums: AlbumList
)