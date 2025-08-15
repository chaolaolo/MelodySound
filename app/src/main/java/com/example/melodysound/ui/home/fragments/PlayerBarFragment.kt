package com.example.melodysound.ui.home.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.model.TrackItem
import com.example.melodysound.data.repository.SpotifyRepository
import com.example.melodysound.databinding.PlayerBarLayoutBinding
import com.example.melodysound.ui.common.AuthTokenManager
import com.example.melodysound.ui.home.HomeViewModel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class PlayerBarFragment : Fragment() {

    private var _binding: PlayerBarLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels {
        HomeViewModelFactory(requireActivity().application, SpotifyRepository(requireContext()))
    }

    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBarTask = object : Runnable {
        override fun run() {
            // Get the current position and duration from the ViewModel
            val currentPosition = viewModel.currentPosition.value ?: 0
            val duration = viewModel.currentTrack.value?.durationMs ?: 0

            // Update the seekbar
            binding.seekbarProgress.max = duration
            binding.seekbarProgress.progress = currentPosition
            // Schedule the next update
            handler.postDelayed(this, 1000) // Update every second
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerBarLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        // Thiết lập các sự kiện click cho các nút điều khiển
        binding.btnPlayPause.setOnClickListener { togglePlayback() }
        binding.btnNext.setOnClickListener { skipToNext() }
        binding.btnPrevious.setOnClickListener { skipToPrevious() }

        binding.seekbarProgress.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.seekToPosition(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupObservers() {
        // Lắng nghe trạng thái bài hát hiện tại và cập nhật giao diện
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentTrack.collect { track ->
                updatePlayerUI(track)
                handler.post(updateSeekBarTask)
            }
        }

        // Lắng nghe trạng thái play/pause và cập nhật icon
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isPlaying.collect { isPlaying ->
                updatePlayPauseButton(isPlaying)
            }
        }
    }

    private fun updatePlayerUI(track: TrackItem?) {
        track?.let {
            binding.tvSongTitle.text = it.name
            binding.tvArtistName.text = it.artists.joinToString(", ") { artist -> artist.name }
            val imageUrl = it.album.images.firstOrNull()?.url
            if (imageUrl != null) {
                Glide.with(this).load(imageUrl).into(binding.ivAlbumArt)
            } else {
                binding.ivAlbumArt.setImageResource(R.drawable.logo)
            }
        }
    }

    private fun updatePlayPauseButton(isPlaying: Boolean) {
        val icon = if (isPlaying) R.drawable.baseline_pause else R.drawable.baseline_play_arrow
        // Cập nhật biểu tượng cho nút play/pause, bạn đang có 3 nút trong XML nên cần sửa lại
        // và đảm bảo drawable baseline_play_arrow tồn tại
        binding.btnPlayPause.setImageResource(icon)
    }

    private fun togglePlayback() {
        val accessToken = AuthTokenManager.getAccessToken(requireContext())
        if (accessToken == null) {
            Toast.makeText(context, "Không có access token.", Toast.LENGTH_SHORT).show()
            return
        }

        // Gọi các hàm điều khiển trong ViewModel
        if (viewModel.isPlaying.value) {
            viewModel.pausePlayback(accessToken)
        } else {
            // Để phát lại bài hát
            viewModel.resumePlayback(accessToken)
        }
    }

    private fun skipToNext() {
        viewModel.skipToNext()
    }

    private fun skipToPrevious() {
        viewModel.skipToPrevious()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
