package com.example.melodysound.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melodysound.data.model.AlbumFull
import com.example.melodysound.data.repository.SpotifyRepository
import com.example.melodysound.data.repository.Result
import com.example.melodysound.data.model.AlbumItem
import com.example.melodysound.data.model.Artist
import com.example.melodysound.data.model.Track
import com.example.melodysound.data.model.TrackItem
import com.example.melodysound.ui.common.AuthTokenManager
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application,
    private val repository: SpotifyRepository
) : AndroidViewModel(application) {

    private val _newReleases = MutableStateFlow<List<AlbumItem>>(emptyList())
    val newReleases: StateFlow<List<AlbumItem>> = _newReleases.asStateFlow()
    private val _topTracks = MutableStateFlow<List<TrackItem>>(emptyList())
    val topTracks: StateFlow<List<TrackItem>> = _topTracks.asStateFlow()
    private val _topArtists = MutableStateFlow<List<Artist>>(emptyList())
    val topArtists: StateFlow<List<Artist>> = _topArtists.asStateFlow()
    private val _albumDetails = MutableStateFlow<AlbumFull?>(null)
    val albumDetails: StateFlow<AlbumFull?> = _albumDetails.asStateFlow()
    private val _trackDetails = MutableStateFlow<TrackItem?>(null)
    val trackDetails: StateFlow<TrackItem?> = _trackDetails.asStateFlow()
    private val _artistDetails = MutableStateFlow<Artist?>(null)
    val artistDetails: StateFlow<Artist?> = _artistDetails.asStateFlow()
    private val _artistTopTracks = MutableStateFlow<List<TrackItem>>(emptyList())
    val artistTopTracks: StateFlow<List<TrackItem>> = _artistTopTracks.asStateFlow()
    private val _artistAlbums = MutableStateFlow<List<AlbumItem>>(emptyList())
    val artistAlbums: StateFlow<List<AlbumItem>> = _artistAlbums.asStateFlow()
    private val _currentTrack = MutableStateFlow<TrackItem?>(null)
    val currentTrack: StateFlow<TrackItem?> = _currentTrack.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _artistDetailsForAlbum = MutableStateFlow<Artist?>(null)
    val artistDetailsForAlbum: StateFlow<Artist?> = _artistDetailsForAlbum.asStateFlow()


    // Trạng thái loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Trạng thái lỗi
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    //    loadNewReleases
    fun loadNewReleases(accessToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null // Xóa lỗi cũ

            when (val result = repository.getNewReleases(accessToken)) {
                is Result.Success -> {
                    // Cập nhật StateFlow với dữ liệu mới
                    _newReleases.value = result.data.albums.items
                }

                is Result.Error -> {
                    // Cập nhật StateFlow với thông báo lỗi
                    _errorMessage.value = result.message
                }
            }
            _isLoading.value = false
        }
    }

    //    loadUserTopTracks
    fun loadUserTopTracks(accessToken: String) {
        viewModelScope.launch {
            // Cập nhật trạng thái loading
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = repository.getUserTopTracks(accessToken)) {
                is Result.Success -> {
                    _topTracks.value = result.data.items
                }

                is Result.Error -> {
                    _errorMessage.value = result.message
                }
            }
            // Kết thúc loading
            _isLoading.value = false
        }
    }

    //    loadUserTopArtists
    fun loadUserTopArtists(accessToken: String) {
        viewModelScope.launch {
            // Cập nhật trạng thái loading
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = repository.getUserTopArtists(accessToken)) {
                is Result.Success -> {
                    _topArtists.value = result.data.items
                }

                is Result.Error -> {
                    _errorMessage.value = result.message
                }
            }
            // Kết thúc loading
            _isLoading.value = false
        }
    }

    // Thêm phương thức mới để tải chi tiết album
    fun loadAlbumDetails(accessToken: String, albumId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _albumDetails.value = null
            _artistDetailsForAlbum.value = null

            when (val result = repository.getAlbum(accessToken, albumId)) {
                is Result.Success -> {
                    _albumDetails.value = result.data
                    result.data.artists.firstOrNull()?.id?.let { artistId ->
                        loadArtistDetailsForAlbum(accessToken, artistId)
                    }
                }

                is Result.Error -> {
                    _errorMessage.value = "Failed to load album details: ${result.message}"
                }
            }
            _isLoading.value = false
        }
    }

    private fun loadArtistDetailsForAlbum(accessToken: String, artistId: String) {
        viewModelScope.launch {
            when (val result = repository.getArtist(accessToken, artistId)) {
                is Result.Success -> {
                    _artistDetailsForAlbum.value = result.data
                }

                is Result.Error -> {
                    Log.e(
                        "HomeViewModel",
                        "Failed to load artist details for album: ${result.message}"
                    )
                    // Không cần hiện toast lỗi ra màn hình vì đây là dữ liệu phụ
                }
            }
        }
    }

    fun loadTrackDetails(accessToken: String, trackId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _trackDetails.value = null

            when (val result = repository.getTrack(accessToken, trackId)) {
                is Result.Success -> {
                    _trackDetails.value = result.data
                    result.data.artists.firstOrNull()?.id?.let { artistId ->
                        loadArtistDetailsForAlbum(accessToken, artistId)
                    }
                }

                is Result.Error -> {
                    _errorMessage.value = "Failed to load track details: ${result.message}"
                }
            }
            _isLoading.value = false
        }
    }

    // Thêm phương thức mới để tải chi tiết artist
    fun loadArtistDetails(accessToken: String, artistId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _artistDetails.value = null // Xóa dữ liệu cũ

            when (val result = repository.getArtist(accessToken, artistId)) {
                is Result.Success -> {
                    _artistDetails.value = result.data
                }

                is Result.Error -> {
                    _errorMessage.value = "Failed to load artist details: ${result.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun loadArtistTopTracks(accessToken: String, artistId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _artistTopTracks.value = emptyList()

            when (val result = repository.getArtistTopTracks(accessToken, artistId)) {
                is Result.Success -> {
                    _artistTopTracks.value = result.data.tracks
                }

                is Result.Error -> {
                    _errorMessage.value =
                        "Failed to load artist's top tracks: ${result.message}"
                    _artistTopTracks.value = emptyList()
                }
            }
            _isLoading.value = false
        }
    }

    fun loadArtistAlbums(accessToken: String, artistId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _artistAlbums.value = emptyList()

            when (val result = repository.getArtistAlbums(accessToken, artistId)) {
                is Result.Success -> {
//                    _artistAlbums.value = result.data.albums.items
                    _artistAlbums.value = result.data.items ?: emptyList()
                }

                is Result.Error -> {
                    _errorMessage.value =
                        "Failed to load artist's albums: ${result.message}"
                    _artistAlbums.value = emptyList()
                }
            }
            _isLoading.value = false
        }
    }

    fun loadPlayerTrackDetails(accessToken: String, trackId: String) {
        viewModelScope.launch {
            when (val result = repository.getTrack(accessToken, trackId)) {
                is Result.Success -> {
                    _currentTrack.value = result.data
                }

                is Result.Error -> {
                    _errorMessage.value =
                        "Failed to load track details for player: ${result.message}"
                    Log.e(
                        "HomeViewModel",
                        "Failed to load track details for player: ${result.message}"
                    )
                }
            }
        }
    }

    fun playTrack(accessToken: String, trackUri: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.playTrack(accessToken, trackUri)) {
                is Result.Success -> {
                    _isPlaying.value = true
                    _errorMessage.value = null
                }

                is Result.Error -> {
                    _errorMessage.value = "Failed to play track: ${result.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun playPlaylist(accessToken: String, playlistUri: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.playPlaylist(accessToken, playlistUri)) {
                is Result.Success -> {
                    _isPlaying.value = true
                    _errorMessage.value = null
                }
                is Result.Error -> {
                    _errorMessage.value = "Failed to play playlist: ${result.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun resumePlayback(accessToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.resumePlayback(accessToken)) {
                is Result.Success -> {
                    _isPlaying.value = true
                    _errorMessage.value = null
                }

                is Result.Error -> {
                    _errorMessage.value = "Failed to resume playback: ${result.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun pausePlayback(accessToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.pausePlayback(accessToken)) {
                is Result.Success -> {
                    _isPlaying.value = false
                    _errorMessage.value = null
                }

                is Result.Error -> {
                    _errorMessage.value = "Failed to pause playback: ${result.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun updateCurrentlyPlayingTrack(accessToken: String) {
        viewModelScope.launch {
            // Đợi một chút để Spotify có thời gian xử lý lệnh skip
            delay(1000) // Đợi 1 giây

            when (val result = repository.getCurrentlyPlayingTrack(accessToken)) {
                is Result.Success -> {
                    _currentTrack.value = result.data
                    _isPlaying.value = true // Giả định khi có bài hát, nó đang phát
                }
                is Result.Error -> {
                    _errorMessage.value = "Failed to update current track: ${result.message}"
                }
            }
        }
    }

    fun skipToNext() {
        val accessToken = AuthTokenManager.getAccessToken(getApplication()) ?: return
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.skipToNext(accessToken)) {
                is Result.Success -> {
                    updateCurrentlyPlayingTrack(accessToken)
                }
                is Result.Error -> {
                    _errorMessage.value = "Failed to skip to next track: ${result.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun skipToPrevious() {
        val accessToken = AuthTokenManager.getAccessToken(getApplication()) ?: return
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.skipToPrevious(accessToken)) {
                is Result.Success -> {
                    updateCurrentlyPlayingTrack(accessToken)
                }
                is Result.Error -> {
                    _errorMessage.value = "Failed to skip to previous track: ${result.message}"
                }
            }
            _isLoading.value = false
        }
    }


}
