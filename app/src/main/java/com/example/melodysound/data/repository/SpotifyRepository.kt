package com.example.melodysound.data.repository

import com.example.melodysound.data.model.AlbumFull
import com.example.melodysound.data.remote.RetrofitInstance
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.melodysound.data.model.Artist
import com.example.melodysound.data.model.PagingAlbums
import com.example.melodysound.data.model.PagingArtists
import com.example.melodysound.data.model.PagingTracks
import com.example.melodysound.data.model.TrackItem
import com.example.melodysound.data.remote.SpotifyApiService

class SpotifyRepository(
    private val apiService: SpotifyApiService = RetrofitInstance.api
) {
    suspend fun getNewReleases(
        accessToken: String,
        country: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): Result<PagingAlbums> {
        return try {
            // Định dạng token đúng chuẩn
            val authorizationHeader = "Bearer $accessToken"

            // Gọi API
            val response = apiService.getNewReleases(
                authorization = authorizationHeader,
                country = country,
                limit = limit,
                offset = offset
            )

            // Xử lý kết quả trả về từ API
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response body")
            } else {
                Result.Error("API call failed with code: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("An error occurred: ${e.message}")
        }
    }

    // getUserTopTracks
    suspend fun getUserTopTracks(
        accessToken: String,
        timeRange: String = "medium_term",
        limit: Int = 20,
        offset: Int = 0
    ): Result<PagingTracks> {
        return try {
            val authorizationHeader = "Bearer $accessToken"

            val response = apiService.getUserTopTracks(
                authorization = authorizationHeader,
                timeRange = timeRange,
                limit = limit,
                offset = offset
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response body")
            } else {
                Result.Error(
                    "API call failed with code: ${response.code()} - ${
                        response.errorBody()?.string()
                    }"
                )
            }
        } catch (e: Exception) {
            Result.Error("An error occurred: ${e.message}")
        }
    }

    //    getUserTopArtists
    suspend fun getUserTopArtists(
        accessToken: String,
        timeRange: String = "medium_term",
        limit: Int = 20,
        offset: Int = 0
    ): Result<PagingArtists> {
        return try {
            val authorizationHeader = "Bearer $accessToken"
            val response = apiService.getUserTopArtists(
                authorization = authorizationHeader,
                timeRange = timeRange,
                limit = limit,
                offset = offset
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response body for top artists")
            } else {
                Result.Error(
                    "API call failed for top artists with code: ${response.code()} - ${
                        response.errorBody()?.string()
                    }"
                )
            }
        } catch (e: Exception) {
            Result.Error("An error occurred while getting top artists: ${e.message}")
        }
    }


    // getAlbum theo ID
    suspend fun getAlbum(
        accessToken: String,
        id: String
    ): Result<AlbumFull> {
        return try {
            val authorizationHeader = "Bearer $accessToken"
            val response = apiService.getAlbum(
                authorization = authorizationHeader,
                id = id
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response body for album details")
            } else {
                Result.Error("API call failed for album details with code: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("An error occurred while getting album: ${e.message}")
        }
    }

    // getArtist theo ID
    suspend fun getArtist(
        accessToken: String,
        id: String
    ): Result<Artist> {
        return try {
            val authorizationHeader = "Bearer $accessToken"
            val response = apiService.getArtist(
                authorization = authorizationHeader,
                id = id
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response body for artist details")
            } else {
                Result.Error("API call failed for artist details with code: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("An error occurred while getting artist: ${e.message}")
        }
    }

    //    getTrack theo ID
    suspend fun getTrack(
        accessToken: String,
        id: String
    ): Result<TrackItem> {
        return try {
            val authorizationHeader = "Bearer $accessToken"
            val response = apiService.geTrack(
                authorization = authorizationHeader,
                id = id
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response body for track details")
            } else {
                Result.Error("API call failed for track details with code: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("An error occurred while getting track: ${e.message}")
        }
    }

}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}