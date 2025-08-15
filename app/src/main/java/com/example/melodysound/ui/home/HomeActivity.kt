package com.example.melodysound.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.melodysound.R
import com.example.melodysound.data.repository.SpotifyRepository
import com.example.melodysound.databinding.ActivityHomeBinding
import com.example.melodysound.ui.common.AuthTokenManager
import com.example.melodysound.ui.home.fragments.HomeViewModelFactory
import com.example.melodysound.ui.home.fragments.PlayerBarFragment


interface OnTrackSelectedListener {
    fun onTrackSelected(trackId: String)
}

class HomeActivity : AppCompatActivity(), OnTrackSelectedListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(application, SpotifyRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        setupPlayerBarObserver()
        setupNavigation()
        binding.navView.visibility = View.VISIBLE
        supportFragmentManager.addOnBackStackChangedListener {
            val backStackEntryCount = supportFragmentManager.backStackEntryCount
            if (backStackEntryCount == 0) {
                binding.navView.visibility = View.VISIBLE
            }
        }

        binding.playerBarContainer.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.slide_in_up, // animation khi mở
                R.anim.slide_out_down, // animation khi đóng
                R.anim.slide_in_up, // animation khi popBackStack
                R.anim.slide_out_down // animation khi popBackStack
            )
            val fullScreenPlayerFragment = FullScreenPlayerFragment(viewModel)
            transaction.replace(R.id.main, fullScreenPlayerFragment)

            transaction.addToBackStack(null)
            transaction.commit()
            binding.navView.visibility = View.GONE
        }

        val accessToken = AuthTokenManager.getAccessToken(this)
        val refreshToken = AuthTokenManager.getRefreshToken(this)

        Log.d("HomeActivityToken", "Access token exists: ${accessToken}")
        Log.d("HomeActivityToken", "Refresh token exists: ${refreshToken}")
    }


    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home) as NavHostFragment
        navController = navHostFragment.navController

        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.searchFragment -> {
                    navController.navigate(R.id.searchFragment)
                    true
                }

                R.id.libraryFragment -> {
                    navController.navigate(R.id.libraryFragment)
                    true
                }

                R.id.personalFragment -> {
                    navController.navigate(R.id.personalFragment)
                    true
                }

                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onTrackSelected(trackId: String) {
        Log.d("HomeActivity", "Track selected: $trackId")
        binding.playerBarContainer.visibility = View.VISIBLE

        val accessToken = AuthTokenManager.getAccessToken(this)
        if (accessToken != null) {
            showPlayerBar()
            viewModel.loadPlayerTrackDetails(accessToken, trackId)

            val trackUri = "spotify:track:$trackId"
            viewModel.playTrack(accessToken, trackUri)
        } else {
            Toast.makeText(
                this,
                "Không tìm thấy access token. Vui lòng đăng nhập lại.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showPlayerBar() {
        // Kiểm tra nếu fragment chưa được thêm thì thêm
        if (supportFragmentManager.findFragmentById(R.id.player_bar_container) == null) {
            binding.playerBarContainer.visibility = View.VISIBLE
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            // Không truyền trackId nữa, fragment sẽ tự lắng nghe ViewModel
            val playerBarFragment = PlayerBarFragment()
            fragmentTransaction.replace(binding.playerBarContainer.id, playerBarFragment)
            fragmentTransaction.commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}