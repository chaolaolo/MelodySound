package com.example.melodysound.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.melodysound.R
import com.example.melodysound.constants.Constants
import com.example.melodysound.data.remote.RetrofitAuthInstance
import com.example.melodysound.databinding.ActivityAuthBinding
import com.example.melodysound.ui.auth.fragments.ChoiceLoginFragment
import com.example.melodysound.ui.auth.fragments.ChoiceSignUpFragment
import com.example.melodysound.ui.common.AuthTokenManager
import com.example.melodysound.ui.home.HomeActivity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Base64


interface AuthNavigator {
    fun navigateToChoiceLogin()
    fun navigateToChoiceSignUp()
    fun onSpotifyAuthRequested(request: AuthorizationRequest)
}

@Suppress("DEPRECATION")
class AuthActivity : AppCompatActivity(), AuthNavigator {
    private lateinit var binding: ActivityAuthBinding

    private val REDIRECT_URI = Constants.SPOTIFY_REDIRECT_URI
    private val CLIENT_ID = Constants.SPOTIFY_CLIENT_ID
    private val CLIENT_SECRET = Constants.SPOTIFY_CLIENT_SECRET
    // Coroutine scope để quản lý các tác vụ bất đồng bộ
    private var job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Kiểm tra xem đã có access token được lưu trữ chưa
        val savedAccessToken = AuthTokenManager.getAccessToken(this)
        if (savedAccessToken != null) {
            Log.d("AuthActivity", "Đã tìm thấy token. Chuyển đến HomeActivity.")
            navigateToHome()
        } else if (savedInstanceState == null) {
            // Nếu không có token, hiển thị màn hình đăng nhập như bình thường
            Log.d("AuthActivity", "Không có token. Hiển thị ChoiceLoginFragment.")
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, ChoiceLoginFragment())
                .commit()
        }
        handleIntent(intent)
//        if (savedInstanceState == null) {
//            Log.d("AuthActivity", "Hiển thị ChoiceAuthFragment.")
//            supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.fragment_container, ChoiceAuthFragment())
//                .commit()
//        }
    }

    private val spotifyAuthLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("AuthActivity", "ActivityResultLauncher đã nhận được kết quả.")
            val response = AuthorizationClient.getResponse(result.resultCode, result.data)
            handleSpotifyAuthResponse(response)
        }

    private fun handleSpotifyAuthResponse(response: AuthorizationResponse) {
        when (response.type) {
            AuthorizationResponse.Type.TOKEN -> {
                Log.d(
                    "AuthActivity",
                    "Xác thực Spotify thành công! Access Token: ${response.accessToken}"
                )
                val accessToken = response.accessToken ?: ""
                if (!accessToken.isNullOrEmpty()) {
                    AuthTokenManager.saveTokens(this, accessToken, "")
                }
                navigateToHome()
            }

            AuthorizationResponse.Type.CODE -> {
                Log.d("AuthActivity", "Xác thực Spotify thành công! Mã ủy quyền: ${response.code}")
                val code = response.code

                // Khởi chạy coroutine để thực hiện network call trên luồng IO
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val authHeader = "Basic " + Base64.getEncoder().encodeToString("$CLIENT_ID:$CLIENT_SECRET".toByteArray())

                        val apiService = RetrofitAuthInstance.getAuthApiService()
                        val tokenResponse = apiService.exchangeCodeForTokens(
                            authHeader,
                            "authorization_code",
                            code,
                            REDIRECT_URI
                        )

                        // Sau khi nhận được phản hồi, chuyển về luồng chính để cập nhật UI hoặc lưu trạng thái
                        launch(Dispatchers.Main) {
                            val accessToken = tokenResponse.accessToken
                            val refreshToken = tokenResponse.refreshToken
                            if (accessToken != null && refreshToken != null) {
                                Log.d("AuthActivity", "Đã nhận được Access Token và Refresh Token.")
                                AuthTokenManager.saveTokens(this@AuthActivity, accessToken, refreshToken)
                                navigateToHome()
                            } else {
                                Log.e("AuthActivity", "Phản hồi token không hợp lệ.")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("AuthActivity", "Lỗi khi trao đổi mã: ${e.message}")
                    }
                }
            }

            AuthorizationResponse.Type.ERROR -> {
                Log.e("AuthActivity", "Lỗi: ${response.error}")
            }

            else -> {
                Log.d(
                    "AuthActivity",
                    "Xác thực Spotify bị hủy hoặc không xác định: ${response.type}"
                )
            }
        }
    }

    private fun navigateToHome() {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(homeIntent)
        finish()
    }

    private fun handleIntent(intent: Intent?) {
        Log.d("AuthActivity", "Handle intent: ${intent?.data}")
        if (intent?.action == Intent.ACTION_VIEW) {
            val uri = intent.data
            if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
                val response = AuthorizationResponse.fromUri(uri)
                handleSpotifyAuthResponse(response)
            }
        }
    }

    // Thêm hàm này vào trong AuthActivity
    private fun refreshAccessToken() {
        val refreshToken = AuthTokenManager.getRefreshToken(this)

        if (refreshToken == null) {
            Log.e("AuthActivity", "Refresh Token không tồn tại. Yêu cầu đăng nhập lại.")
            // Chuyển người dùng về màn hình đăng nhập
            navigateToChoiceLogin()
            return
        }

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val authHeader = "Basic " + Base64.getEncoder().encodeToString("$CLIENT_ID:$CLIENT_SECRET".toByteArray())
                val apiService = RetrofitAuthInstance.getAuthApiService()

                val tokenResponse = apiService.refreshAccessToken(
                    authHeader,
                    "refresh_token",
                    refreshToken
                )

                launch(Dispatchers.Main) {
                    val newAccessToken = tokenResponse.accessToken
                    // Lưu ý: Spotify có thể gửi lại một Refresh Token mới, hoặc không.
                    // Nếu có, hãy lưu token mới. Nếu không, giữ nguyên token cũ.
                    val newRefreshToken = tokenResponse.refreshToken ?: refreshToken

                    if (newAccessToken != null) {
                        Log.d("AuthActivity", "Đã làm mới Access Token thành công.")
                        AuthTokenManager.saveTokens(this@AuthActivity, newAccessToken, newRefreshToken)
                        // Sau khi làm mới thành công, chuyển đến HomeActivity
                        navigateToHome()
                    } else {
                        Log.e("AuthActivity", "Không nhận được Access Token mới. Đăng nhập lại.")
                        AuthTokenManager.clearTokens(this@AuthActivity)
                        navigateToChoiceLogin()
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthActivity", "Lỗi khi làm mới token: ${e.message}")
                AuthTokenManager.clearTokens(this@AuthActivity)
                navigateToChoiceLogin()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun navigateToChoiceLogin() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, ChoiceLoginFragment())
            .addToBackStack("login_fragment")
            .commit()
    }

    override fun navigateToChoiceSignUp() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, ChoiceSignUpFragment())
            .addToBackStack("signup_fragment")
            .commit()
    }

    override fun onSpotifyAuthRequested(request: AuthorizationRequest) {
        val intent = AuthorizationClient.createLoginActivityIntent(this, request)
        spotifyAuthLauncher.launch(intent)

    }

}