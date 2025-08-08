package com.example.melodysound.ui.auth

import androidx.lifecycle.ViewModel
import com.example.melodysound.constants.Constants
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class AuthViewModel : ViewModel() {

    fun createSpotifyAuthRequest(): AuthorizationRequest {
        val scopes = arrayOf(
            "user-read-private",
            "user-read-email",
            "playlist-read-private",
            "user-library-read",
            "user-top-read",
            "user-read-playback-state",
            "user-modify-playback-state",
            "streaming"
        )
        val builder =
            AuthorizationRequest.Builder(Constants.SPOTIFY_CLIENT_ID, AuthorizationResponse.Type.CODE, Constants.SPOTIFY_REDIRECT_URI)
                .setScopes(scopes)
                .setShowDialog(true)

        return builder.build()
    }

}