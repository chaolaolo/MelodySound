package com.example.melodysound.data.repository

import CurrentUser
import android.content.Context
import com.example.melodysound.data.model.AlbumFull
import com.example.melodysound.data.remote.RetrofitInstance
import com.example.melodysound.data.model.Artist
import com.example.melodysound.data.model.ArtistTopTracksResponse
import com.example.melodysound.data.model.FollowingArtistsResponse
import com.example.melodysound.data.model.PagingAlbums
import com.example.melodysound.data.model.PagingArtists
import com.example.melodysound.data.model.PagingNewRelease
import com.example.melodysound.data.model.PagingPlaylistsResponse
import com.example.melodysound.data.model.PagingTopChartTracks
import com.example.melodysound.data.model.PagingTracks
import com.example.melodysound.data.model.PlayRequestBody
import com.example.melodysound.data.model.PlaylistResponse
import com.example.melodysound.data.model.TrackItem
import com.example.melodysound.data.remote.SpotifyApiService

class SpotifyRepository(
    private val context: Context
) {
    private val apiService: SpotifyApiService = RetrofitInstance.getSpotifyApiService(context)

    suspend fun getNewReleases(
        accessToken: String,
        country: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): Result<PagingNewRelease> {
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

    suspend fun getArtistTopTracks(
        accessToken: String,
        id: String
    ): Result<ArtistTopTracksResponse> {
        return try {
            val authorizationHeader = "Bearer $accessToken"
            val response = apiService.geArtistTopTracks(
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
            Result.Error("An error occurred: ${e.message}")
        }
    }

    suspend fun getArtistAlbums(accessToken: String, id: String): Result<PagingAlbums> {
        return try {
            val authorizationHeader = "Bearer $accessToken"
            val response = apiService.getArtistAlbums(
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
            Result.Error("An error occurred: ${e.message}")
        }
    }

    // các phương thức để phát nhạc
    suspend fun playTrack(accessToken: String, trackUri: String): Result<Unit> {
        return try {
            val requestBody = PlayRequestBody(uris = listOf(trackUri))
            val response = apiService.playTrack("Bearer $accessToken", requestBody)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("API error: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    suspend fun pausePlayback(accessToken: String): Result<Unit> {
        return try {
            val response = apiService.pausePlayback("Bearer $accessToken")
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("API error: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    suspend fun playPlaylist(accessToken: String, playlistUri: String): Result<Unit> {
        return try {
            // Thêm logic gọi API play với context_uri
            // Ví dụ: val requestBody = PlayContextRequestBody(context_uri = playlistUri)
            // ...
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    suspend fun resumePlayback(accessToken: String): Result<Unit> {
        return try {
            val response = apiService.resumePlayback("Bearer $accessToken")
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("API error: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    suspend fun skipToNext(accessToken: String): Result<Unit> {
        return try {
            val response = apiService.skipToNext("Bearer $accessToken")
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("API error: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Result.Error("An error occurred: ${e.message}")
        }
    }

    suspend fun skipToPrevious(accessToken: String): Result<Unit> {
        return try {
            val response = apiService.skipToPrevious("Bearer $accessToken")
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("API error: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Result.Error("An error occurred: ${e.message}")
        }
    }

    suspend fun getCurrentlyPlayingTrack(accessToken: String): Result<TrackItem> {
        return try {
            val response = apiService.getCurrentlyPlayingTrack("Bearer $accessToken")
            if (response.isSuccessful) {
                response.body()?.track?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty track item in response.")
            } else {
                Result.Error("API error: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }


    suspend fun getPlaylistTracks(
        accessToken: String,
        playlistId: String,
        limit: Int = 50,
        offset: Int = 0
    ): Result<PagingTopChartTracks> {
        return try {
            val authorizationHeader = "Bearer $accessToken"
            val response = apiService.getPlaylistTracks(
                authorization = authorizationHeader,
                playlistId = playlistId,
                limit = limit,
                offset = offset
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response body for top 50 tracks")
            } else {
                Result.Error("API call failed for top 50 with code: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Result.Error("An error occurred while getting top 50 tracks: ${e.message}")
        }
    }

    suspend fun getPlaylistDetails(
        accessToken: String,
        playlistId: String
    ): Result<PlaylistResponse> {
        return try {
            val authorizationHeader = "Bearer $accessToken"
            val response = apiService.getPlaylistDetails(
                authorization = authorizationHeader,
                playlistId = playlistId
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response body for playlist details")
            } else {
                Result.Error("API call failed for playlist details with code: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Result.Error("An error occurred while getting playlist details: ${e.message}")
        }
    }


    suspend fun getCurrentUser(accessToken: String): Result<CurrentUser> {
        return try {
            val authorizationHeader = "Bearer $accessToken"
            val response = apiService.getCurrentUser(authorizationHeader)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response body for current user")
            } else {
                Result.Error("API call failed for current user with code: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Result.Error("An error occurred while getting current user: ${e.message}")
        }
    }

    suspend fun getFollowingArtists(accessToken: String): Result<FollowingArtistsResponse> {
        return try {
            val authorizationHeader = "Bearer $accessToken"
            val response = apiService.getFollowingArtists(authorizationHeader)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response body for following artists")
            } else {
                Result.Error("API call failed for following artists with code: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Result.Error("An error occurred while getting following artists: ${e.message}")
        }
    }

    suspend fun getCurrentUserPlaylists(accessToken: String): Result<PagingPlaylistsResponse> {
        return try {
            val authorizationHeader = "Bearer $accessToken"
            val response = apiService.getCurrentUserPlaylists(authorizationHeader)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty response body for user playlists")
            } else {
                Result.Error("API call failed for user playlists with code: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Result.Error("An error occurred while getting user playlists: ${e.message}")
        }
    }

    suspend fun seekToPosition(accessToken: String, positionMs: Int): Result<Unit> {
        return try {
            val response = apiService.seekToPosition(
                authorization = "Bearer $accessToken",
                position_ms = positionMs
            )
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("API error: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }


}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}