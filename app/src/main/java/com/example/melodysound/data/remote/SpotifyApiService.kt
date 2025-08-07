package com.example.melodysound.data.remote

import com.example.melodysound.data.model.AlbumFull
import com.example.melodysound.data.model.Artist
import com.example.melodysound.data.model.PagingAlbums
import com.example.melodysound.data.model.PagingArtists
import com.example.melodysound.data.model.PagingTracks
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyApiService {

    @GET("browse/new-releases")
    suspend fun getNewReleases(
        @Header("Authorization") authorization: String,
        @Query("country") country: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<PagingAlbums>

    @GET("me/top/tracks")
    suspend fun getUserTopTracks(
        @Header("Authorization") authorization: String,
        @Query("time_range") timeRange: String? = "medium_term",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<PagingTracks>

    @GET("me/top/artists")
    suspend fun getUserTopArtists(
        @Header("Authorization") authorization: String,
        @Query("time_range") timeRange: String? = "medium_term",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<PagingArtists>

    // Thêm phương thức mới để lấy thông tin chi tiết của một album
    @GET("albums/{id}")
    suspend fun getAlbum(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Response<AlbumFull>

    // Thêm phương thức mới để lấy thông tin chi tiết của một artist
    @GET("artists/{id}")
    suspend fun getArtist(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Response<Artist>

}