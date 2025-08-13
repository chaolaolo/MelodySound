package com.example.melodysound.data.remote

import com.example.melodysound.data.model.AlbumFull
import com.example.melodysound.data.model.Artist
import com.example.melodysound.data.model.ArtistTopTracksResponse
import com.example.melodysound.data.model.CurrentlyPlayingResponse
import com.example.melodysound.data.model.PagingAlbums
import com.example.melodysound.data.model.PagingArtists
import com.example.melodysound.data.model.PagingNewRelease
import com.example.melodysound.data.model.PagingTracks
import com.example.melodysound.data.model.PlayRequestBody
import com.example.melodysound.data.model.TopTracksResponse
import com.example.melodysound.data.model.Track
import com.example.melodysound.data.model.TrackItem
import com.example.melodysound.data.model.UserTopTracksResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyApiService {

    @GET("browse/new-releases")
    suspend fun getNewReleases(
        @Header("Authorization") authorization: String,
        @Query("country") country: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<PagingNewRelease>

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


    //    get one track
    @GET("tracks/{id}")
    suspend fun geTrack(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Response<TrackItem>

    //    get artist
    @GET("artists/{id}")
    suspend fun geArtist(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Response<Artist>

    //    get artist top tracks
    @GET("artists/{id}/top-tracks")
    suspend fun geArtistTopTracks(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
//        @Query("market") market: String = "VN"
    ): Response<ArtistTopTracksResponse>

    //    get artist albums
    @GET("artists/{id}/albums")
    suspend fun getArtistAlbums(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Query("include_groups") includeGroups: String = "album",
//        @Query("market") market: String = "VN",
        @Query("limit") limit: Int = 20
    ): Response<PagingAlbums>

    // Endpoint để phát nhạc
    @PUT("me/player/play")
    suspend fun playTrack(
        @Header("Authorization") authorization: String,
        @Body requestBody: PlayRequestBody
    ): Response<Unit>

    // Endpoint để tạm dừng nhạc
    @PUT("me/player/pause")
    suspend fun pausePlayback(
        @Header("Authorization") authorization: String
    ): Response<Unit>

    @PUT("me/player/play")
    suspend fun resumePlayback(
        @Header("Authorization") authorization: String
    ): Response<Unit>

    // Endpoint để chuyển bài hát tiếp theo
    @POST("me/player/next")
    suspend fun skipToNext(
        @Header("Authorization") authorization: String
    ): Response<Unit>

    // Endpoint để chuyển bài hát trước đó
    @POST("me/player/previous")
    suspend fun skipToPrevious(
        @Header("Authorization") authorization: String
    ): Response<Unit>

    @GET("me/player/currently-playing")
    suspend fun getCurrentlyPlayingTrack(
        @Header("Authorization") authorization: String
    ): Response<CurrentlyPlayingResponse>
}