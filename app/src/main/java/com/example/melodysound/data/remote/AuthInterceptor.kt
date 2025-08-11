package com.example.melodysound.data.remote

import android.content.Context
import com.example.melodysound.ui.common.AuthTokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor (private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = AuthTokenManager.getAccessToken(context)

        val newRequest = if (accessToken != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}