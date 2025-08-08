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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.repository.SpotifyRepository
import com.example.melodysound.databinding.ActivityHomeBinding
import com.example.melodysound.databinding.PlayerBarLayoutBinding
import com.example.melodysound.ui.common.AuthTokenManager
import com.example.melodysound.ui.home.fragments.HomeViewModelFactory
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.launch

interface OnTrackSelectedListener {
    fun onTrackSelected(trackId: String)
}

class HomeActivity : AppCompatActivity(), OnTrackSelectedListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var playerBarBinding: PlayerBarLayoutBinding
    private lateinit var navController: NavController
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(SpotifyRepository())
    }

    private var exoPlayer: SimpleExoPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playerBarBinding = PlayerBarLayoutBinding.bind(binding.playerBarContainer.getChildAt(0))

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupPlayerBarObserver()
        initializeExoPlayer()

        val actok = AuthTokenManager.getAccessToken(this)
        val retok = AuthTokenManager.getRefreshToken(this)

        Log.d("HomeActivityToken", "accesstoken: $actok \n refreshtoken: $retok")
        Log.d("HomeActivity", "open home fragment")

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
        val accessToken = AuthTokenManager.getAccessToken(this)
        if (accessToken != null) {
            viewModel.loadTrackDetails(accessToken, trackId)
        } else {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show()
        }
        binding.playerBarContainer.visibility = View.VISIBLE
    }

    private fun initializeExoPlayer() {
        exoPlayer = SimpleExoPlayer.Builder(this).build()
        exoPlayer?.addListener(object : Player.Listener {
            override fun onIsLoadingChanged(isPlaying: Boolean) {
                super.onIsLoadingChanged(isPlaying)
                if (isPlaying) {
                    playerBarBinding.btnPlayPause.setImageResource(R.drawable.baseline_pause)
                } else {
                    playerBarBinding.btnPlayPause.setImageResource(R.drawable.baseline_play_arrow)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(
                    this@HomeActivity,
                    "Lỗi phát nhạc: ${error.message}",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

        playerBarBinding.btnPlayPause.setOnClickListener {
            if (exoPlayer?.isPlaying == true) {
                exoPlayer?.pause()
            } else {
                exoPlayer?.play()
            }
        }
    }

    private fun setupPlayerBarObserver() {
        lifecycleScope.launch {
            viewModel.trackDetails.collect { trackItem ->
                trackItem?.let {
                    // Cập nhật giao diện player bar với dữ liệu mới
                    playerBarBinding.tvSongTitle.text = it.name
                    playerBarBinding.tvArtistName.text =
                        it.artists.joinToString(", ") { artist -> artist.name }

                    val imageUrl = it.album.images.firstOrNull()?.url
                    if (imageUrl != null) {
                        Glide.with(this@HomeActivity)
                            .load(imageUrl)
                            .placeholder(R.drawable.logo)
                            .into(playerBarBinding.ivAlbumArt)
                    } else {
                        playerBarBinding.ivAlbumArt.setImageResource(R.drawable.logo)
                    }


                    val previewUrl = it.previewUrl
                    if (!previewUrl.isNullOrEmpty()) {
                        val mediaItem = MediaItem.fromUri(previewUrl)
                        exoPlayer?.setMediaItem(mediaItem)
                        exoPlayer?.prepare()
                        exoPlayer?.play()
                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "Bài hát không có link preview",
                            Toast.LENGTH_SHORT
                        ).show()
                        exoPlayer?.stop()
                    }

                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        exoPlayer = null
    }

}