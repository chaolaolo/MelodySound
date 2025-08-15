package com.example.melodysound.ui.home

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.databinding.FragmentFullscreenPlayerBinding
import com.example.melodysound.ui.common.AuthTokenManager
import kotlinx.coroutines.launch

class FullScreenPlayerFragment(private val viewModel: HomeViewModel) :
    Fragment() {

    private var _binding: FragmentFullscreenPlayerBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBarTask = object : Runnable {
        override fun run() {
            // Get the current position and duration from the ViewModel
            val currentPosition = viewModel.currentPosition.value ?: 0
            val duration = viewModel.currentTrack.value?.durationMs ?: 0

            // Update the seekbar
            binding.seekbarProgress.max = duration
            binding.seekbarProgress.progress = currentPosition

            // Update the time labels
            binding.tvCurrentTime.text = formatTime(currentPosition)

            // Schedule the next update
            handler.postDelayed(this, 1000) // Update every second
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFullscreenPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.seekbarProgress.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Seek to the new position if the user moves the thumb
                    viewModel.seekToPosition(progress)
                    binding.tvCurrentTime.text = formatTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        observeViewModel()

        binding.closeButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnPlayPause.setOnClickListener {
            togglePlayback()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.currentTrack.collect { track ->
                        track?.let {
                            binding.tvSongName.text = it.name
                            binding.tvArtistName.text =
                                it.artists.joinToString(", ") { artist -> artist.name }
                            Glide.with(requireContext()).load(it.album.images.firstOrNull()?.url)
                                .into(binding.ivAlbumArt)

                            binding.tvDuration.text = formatTime(it.durationMs)
                            binding.seekbarProgress.max = it.durationMs

                            // Start the periodic UI update
                            handler.post(updateSeekBarTask)
                        }
                    }
                }

                launch {
                    viewModel.isPlaying.collect { isPlaying ->
                        updatePlayPauseButton(isPlaying)
                    }
                }

                launch {
                    viewModel.errorMessage.collect { errorMessage ->
                        if (errorMessage != null) {
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }

    private fun updatePlayPauseButton(isPlaying: Boolean) {
        val icon = if (isPlaying) R.drawable.baseline_pause else R.drawable.baseline_play_arrow
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

    private fun formatTime(millis: Int): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateSeekBarTask)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}