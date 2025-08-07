package com.example.melodysound.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitAuthInstance {
    private const val AUTH_BASE_URL = "https://accounts.spotify.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(AUTH_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getAuthApiService(): SpotifyAuthService = retrofit.create(SpotifyAuthService::class.java)
}