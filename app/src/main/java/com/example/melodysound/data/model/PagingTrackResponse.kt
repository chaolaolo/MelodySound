package com.example.melodysound.data.model

import com.google.gson.annotations.SerializedName

// PagingTrackResponse sẽ ánh xạ đối tượng "tracks" chính
data class PagingTrackResponse(
    @SerializedName("href")
    val href: String,
    @SerializedName("items")
    val items: List<PlaylistTrack>, // Danh sách các item wrapper
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("next")
    val next: String?,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("previous")
    val previous: String?,
    @SerializedName("total")
    val total: Int
)

// PlaylistTrack là đối tượng wrapper cho mỗi bài hát trong danh sách playlist
data class PlaylistTrack(
    @SerializedName("added_at")
    val addedAt: String,
    @SerializedName("added_by")
    val addedBy: Owner, // Tái sử dụng Owner
    @SerializedName("is_local")
    val isLocal: Boolean,
    @SerializedName("primary_color")
    val primaryColor: String?,
    @SerializedName("track")
    val track: TrackItem, // Đây là đối tượng track thực tế
    @SerializedName("video_thumbnail")
    val videoThumbnail: VideoThumbnail?
)

data class VideoThumbnail(
    @SerializedName("url")
    val url: String?
)