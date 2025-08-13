package com.example.melodysound.ui.home.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.model.AlbumFull
import com.example.melodysound.data.model.Artist
import com.example.melodysound.data.model.TrackItem
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
        HomeViewModelFactory(
            application = requireActivity().application,
            SpotifyRepository(requireContext())
        )
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
        val id = arguments?.getString("id") ?: ""
        val type = arguments?.getString("type") ?: ""
        albumTrackAdapter = AlbumTrackAdapter()


        if (accessToken == null || id.isEmpty() || type.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Token, ID hoặc loại dữ liệu không hợp lệ",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Dựa vào type để gọi API tương ứng
        when (type) {
            "album" -> {
                viewModel.loadAlbumDetails(accessToken, id)
                binding.rcAlbumTrack.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = albumTrackAdapter
                    isNestedScrollingEnabled = false
                }
                binding.oneTrackLayout.visibility = View.GONE
            }

            "track" -> {
                viewModel.loadTrackDetails(accessToken, id)
                binding.rcAlbumTrack.visibility = View.GONE
                binding.oneTrackLayout.visibility = View.VISIBLE
            }

            else -> null
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
                launch {
                    viewModel.trackDetails.collect { track ->
                        track?.let {
                            updateUIWithTrackData(it)
                        }
                    }
                }
                lifecycleScope.launch {
                    viewModel.artistDetailsForAlbum.collect { artist ->
                        artist?.let {
                            updateArtistAvatar(it)
                            binding.txtArtistName
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

        binding.txtArtistName.setOnClickListener {
            val bundle = Bundle().apply {
                putString("id", album.artists.firstOrNull()?.id)
            }
            findNavController().navigate(R.id.artistDetailFragment, bundle)
        }
        binding.imgArtistAvatar.setOnClickListener {
            val bundle = Bundle().apply {
                putString("id", album.artists.firstOrNull()?.id)
            }
            findNavController().navigate(R.id.artistDetailFragment, bundle)
        }

        val imageUrl = album.images.firstOrNull()?.url
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(binding.imgAlbumThumbnail)
            Glide.with(this).load(imageUrl).into(binding.imgMiniAlbumThumbnail)
        }

    }

    private fun updateUIWithTrackData(track: TrackItem) {
        binding.txtAlbumName.text = track.name
        binding.txtArtistName.text = track.artists.joinToString(", ") { it.name }
        binding.txtAlbumType.text = track.album.albumType.replaceFirstChar { it.uppercase() }
        binding.txtAlbumReleaseTime.text = track.album.releaseDate.split(" - ").firstOrNull()
        binding.txtTotalTracks.text = buildString {
            append("1 bài hát")
        }
        val totalSeconds = track.durationMs / 1000
        val hours = totalSeconds / 3600
        val remainingMinutes = (totalSeconds % 3600) / 60
        val remainingSeconds = totalSeconds % 60

        val duration = if (hours > 0) {
            "${hours} giờ ${remainingMinutes} phút ${remainingSeconds} giây"
        } else if (remainingMinutes > 0) {
            "${remainingMinutes} phút ${remainingSeconds} giây"
        } else {
            "${remainingSeconds} giây"
        }
        binding.txtTotalTime.text = duration
        binding.txtTrackName.text = track.name
        binding.txtArtistsName.text = track.artists.joinToString(", ") { it.name }


        val imageUrl = track.album.images.firstOrNull()?.url
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