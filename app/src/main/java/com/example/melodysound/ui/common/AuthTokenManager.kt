package com.example.melodysound.ui.common

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object AuthTokenManager {
    private const val PREFS_FILE_NAME = "spotify_auth_prefs"
    private const val ACCESS_TOKEN_KEY = "access_token"
    private const val REFRESH_TOKEN_KEY = "refresh_token"

    // Hàm khởi tạo EncryptedSharedPreferences
    private fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        // Tạo khóa mã hóa chính (master key)
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
            PREFS_FILE_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveTokens(context: Context, accessToken: String, refreshToken: String) {
        val prefs = getEncryptedSharedPreferences(context)
        with(prefs.edit()) {
            putString(ACCESS_TOKEN_KEY, accessToken)
            putString(REFRESH_TOKEN_KEY, refreshToken)
            apply()
        }
    }

    fun getAccessToken(context: Context): String? {
        val prefs = getEncryptedSharedPreferences(context)
        return prefs.getString(ACCESS_TOKEN_KEY, null)
    }

    fun getRefreshToken(context: Context): String? {
        val prefs = getEncryptedSharedPreferences(context)
        return prefs.getString(REFRESH_TOKEN_KEY, null)
    }

    fun clearTokens(context: Context) {
        val prefs = getEncryptedSharedPreferences(context)
        prefs.edit().clear().apply()
    }
}