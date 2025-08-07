package com.example.melodysound.ui.home.fragments.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.model.AlbumFull
import com.example.melodysound.data.model.Artist
import com.example.melodysound.data.repository.SpotifyRepository
import com.example.melodysound.databinding.FragmentAlbumDetailBinding
import com.example.melodysound.ui.common.AuthTokenManager
import com.example.melodysound.ui.home.HomeViewModel
import com.example.melodysound.ui.home.adapter.AlbumTrackAdapter
import com.example.melodysound.ui.home.fragments.HomeViewModelFactory
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AlbumDetailFragment : Fragment() {

    private var _binding: FragmentAlbumDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(SpotifyRepository())
    }

    private lateinit var albumTrackAdapter: AlbumTrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAlbumDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val accessToken = AuthTokenManager.getAccessToken(requireContext())
        val albumId = arguments?.getString("album_id") ?: ""
        albumTrackAdapter = AlbumTrackAdapter()
        binding.rcAlbumTrack.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = albumTrackAdapter
            isNestedScrollingEnabled = false
        }


        if (accessToken != null && albumId.isNotEmpty()) {
            Log.d("AlbumDetailFragment", "Album ID: $albumId")
            // Gọi phương thức để lấy dữ liệu với accessToken đã lấy được
            viewModel.loadAlbumDetails(accessToken, albumId)
        } else {
            // Xử lý trường hợp không có token hoặc albumId không hợp lệ
            Toast.makeText(requireContext(), "Token hoặc Album ID không hợp lệ", Toast.LENGTH_SHORT)
                .show()
            // Có thể chuyển hướng người dùng đến màn hình đăng nhập
        }

        setupObservers()

    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launch {
                launch {
                    viewModel.albumDetails.collect { album ->
                        album?.let {
                            updateUIWithAlbumData(it)
                            albumTrackAdapter.submitList(it.tracks.items)
                        }
                    }
                }
                lifecycleScope.launch {
                    viewModel.artistDetailsForAlbum.collect { artist ->
                        artist?.let {
                            updateArtistAvatar(it)
                        }
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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

    private fun updateUIWithAlbumData(album: AlbumFull) {
        binding.txtAlbumName.text = album.name
        binding.txtArtistName.text = album.artists.joinToString(", ") { it.name }
        binding.txtAlbumType.text = album.albumType.replaceFirstChar { it.uppercase() }
        binding.txtAlbumReleaseTime.text = album.releaseDate.split(" - ").firstOrNull()
        binding.txtTotalTracks.text = buildString {
            append(album.totalTracks)
            append(" bài hát")
        }
        binding.txtTotalTime.text = calculateTotalDuration(album)


        val imageUrl = album.images.firstOrNull()?.url
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(binding.imgAlbumThumbnail)
            Glide.with(this).load(imageUrl).into(binding.imgMiniAlbumThumbnail)
        }

    }

    private fun calculateTotalDuration(album: AlbumFull): String {
        val totalMillis = album.tracks.items.sumOf { it.durationMs }
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(totalMillis.toLong())
        val minutes = totalSeconds / 60
        val hours = minutes / 60
        val remainingMinutes = minutes % 60

        return if (hours > 0) {
            "${hours} giờ ${remainingMinutes} phút"
        } else {
            "${minutes} phút"
        }
    }

    private fun updateArtistAvatar(artist: Artist) {
        val artistAvatarUrl = artist.images.firstOrNull()?.url
        if (artistAvatarUrl != null) {
            Glide.with(this).load(artistAvatarUrl).circleCrop().into(binding.imgArtistAvatar)
        } else {
            // Đặt ảnh placeholder nếu không có ảnh
            binding.imgArtistAvatar.setImageResource(R.drawable.ic_launcher_round)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}