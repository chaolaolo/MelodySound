package com.example.melodysound.ui.home.fragments.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide // Sử dụng Glide để tải ảnh
import com.example.melodysound.R
import com.example.melodysound.data.model.Artist
import com.example.melodysound.data.repository.SpotifyRepository
import com.example.melodysound.databinding.FragmentArtistDetailBinding
import com.example.melodysound.ui.common.AuthTokenManager
import com.example.melodysound.ui.home.HomeActivity
import com.example.melodysound.ui.home.HomeViewModel
import com.example.melodysound.ui.home.OnTrackSelectedListener
import com.example.melodysound.ui.home.adapter.ArtistAlbumsAdapter
import com.example.melodysound.ui.home.adapter.ArtistTopTracksAdapter
import com.example.melodysound.ui.home.fragments.HomeViewModelFactory
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.getValue


class ArtistDetailFragment : Fragment() {

    private var _binding: FragmentArtistDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            application = requireActivity().application,
            SpotifyRepository(requireContext())
        )
    }
    private lateinit var popularTracksAdapter: ArtistTopTracksAdapter
    private lateinit var albumsAdapter: ArtistAlbumsAdapter

    private var trackSelectedListener: OnTrackSelectedListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTrackSelectedListener) {
            trackSelectedListener = context
        } else {
            // Nếu không, ném ra ngoại lệ để báo lỗi
            throw RuntimeException("$context must implement OnTrackSelectedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setup toolbar
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val backIcon = ContextCompat.getDrawable(requireContext(), R.drawable.outline_arrow_back)
        backIcon?.setTint(ContextCompat.getColor(requireContext(), R.color.white))
        activity.supportActionBar?.setHomeAsUpIndicator(backIcon)
        binding.toolbar.setNavigationOnClickListener {
            activity.onBackPressed()
        }

        setupRecyclerViews()

        val accessToken = AuthTokenManager.getAccessToken(requireContext())
        val id = arguments?.getString("id") ?: ""

        if (accessToken != null && id.isNotEmpty()) {
            viewModel.loadArtistDetails(accessToken, id)
            viewModel.loadArtistTopTracks(accessToken, id)
            viewModel.loadArtistAlbums(accessToken, id)
        } else {
            Toast.makeText(
                requireContext(),
                "Bạn chưa đăng nhập, vui lòng đăng nhập!",
                Toast.LENGTH_LONG
            ).show()
        }

        setupObservers()

    }

    private fun setupRecyclerViews() {
        popularTracksAdapter = ArtistTopTracksAdapter(onItemClick = { track ->
            val accessToken = AuthTokenManager.getAccessToken(requireContext())
            if (accessToken != null) {
                // Gọi playTrack từ ViewModel
                viewModel.playTrack(accessToken, "spotify:track:${track.id}")
                // Cập nhật UI với thông tin bài hát mới
                viewModel.loadPlayerTrackDetails(accessToken, track.id)
                // Hiển thị thanh player
                trackSelectedListener?.onTrackSelected(track.id)
            } else {
                Toast.makeText(context, "Không có access token", Toast.LENGTH_SHORT).show()
            }
        })

        albumsAdapter = ArtistAlbumsAdapter(onItemClick = { album ->
            val bundle = Bundle().apply {
                putString("id", album.id)
                putString("type", "album")
            }

            // Lấy NavController từ parent fragment (HomeFragment)
            val navController = parentFragment?.findNavController()
            navController?.navigate(R.id.albumDetailFragment, bundle)
        })

        binding.popularTracksRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = popularTracksAdapter
            isNestedScrollingEnabled = false // Vô hiệu hóa cuộn lồng nhau
        }

        binding.albumsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = albumsAdapter
            isNestedScrollingEnabled = false // Vô hiệu hóa cuộn lồng nhau
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launch {
                launch {
                    viewModel.artistDetails.collect { artist ->
                        artist?.let {
                            updateArtistDetailData(it)
                        }
                    }
                }
                launch {
                    viewModel.artistTopTracks.collect { tracks ->
                        popularTracksAdapter.submitList(tracks)
                    }
                }
                launch {
                    viewModel.artistAlbums.collect { albums ->
                        albumsAdapter.submitList(albums)
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

    private fun updateArtistDetailData(artist: Artist) {
        binding.collapsingToolbar.title = artist.name
        val formatter = DecimalFormat("#,###")
        val formattedFollowers = formatter.format(artist.followers.total)
        binding.txtFollowersCount.text = "$formattedFollowers người theo dõi"

        val formattedListeners = formatter.format(artist.popularity * 100000) // Giả lập
        binding.txtMonthlyListeners.text = "$formattedListeners người nghe hàng tháng"

        val artistAvatarUrl = artist.images.firstOrNull()?.url
        if (artistAvatarUrl != null) {
            Glide.with(this).load(artistAvatarUrl).into(binding.artistImageView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        trackSelectedListener = null
    }
}