package com.example.melodysound.data.remote

import android.content.Context
import com.example.melodysound.constants.Constants
import com.example.melodysound.ui.common.AuthTokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.Base64

class TokenAuthenticator (private val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // Kiểm tra xem refresh token có tồn tại không
        val refreshToken = AuthTokenManager.getRefreshToken(context)
        if (refreshToken == null) {
            // Không có refresh token, không thể làm mới. Trả về null để lỗi 401 được truyền đi.
            return null
        }

        // Tạo một client riêng để gọi API refresh token, tránh lặp vô hạn
        val authService = RetrofitAuthInstance.getAuthApiService()

        // Gọi API refresh token một cách đồng bộ (sync)
        val tokenResponse = runBlocking {
            try {
                val authHeader = "Basic " + Base64.getEncoder().encodeToString(
                    "${Constants.SPOTIFY_CLIENT_ID}:${Constants.SPOTIFY_CLIENT_SECRET}".toByteArray()
                )
                authService.refreshAccessToken(authHeader, "refresh_token", refreshToken)
            } catch (e: Exception) {
                null
            }
        }

        return if (tokenResponse != null && tokenResponse.accessToken != null) {
            // Lưu access token và refresh token mới
            AuthTokenManager.saveTokens(context, tokenResponse.accessToken, tokenResponse.refreshToken ?: refreshToken)

            // Cập nhật request ban đầu với access token mới và thử lại
            response.request.newBuilder()
                .header("Authorization", "Bearer ${tokenResponse.accessToken}")
                .build()
        } else {
            // Làm mới token thất bại, có thể refresh token đã hết hạn
            AuthTokenManager.clearTokens(context)
            // Trả về null để hủy request và có thể chuyển người dùng về màn hình đăng nhập
            null
        }
    }
}