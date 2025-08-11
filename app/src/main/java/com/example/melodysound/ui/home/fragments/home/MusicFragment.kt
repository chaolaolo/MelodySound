package com.example.melodysound.ui.home.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.melodysound.R
import com.example.melodysound.data.model.Artist
import com.example.melodysound.data.model.Chart
import com.example.melodysound.data.model.Song
import com.example.melodysound.data.repository.SpotifyRepository
import com.example.melodysound.databinding.FragmentMusicBinding
import com.example.melodysound.ui.common.AuthTokenManager
import com.example.melodysound.ui.home.HomeViewModel
import com.example.melodysound.ui.home.adapter.ArtistAdapter
import com.example.melodysound.ui.home.adapter.ChartAdapter
import com.example.melodysound.ui.home.adapter.NewReleaseAdapter
import com.example.melodysound.ui.home.adapter.PopularArtistAdapter
import com.example.melodysound.ui.home.adapter.PopularSongsAdapter
import com.example.melodysound.ui.home.fragments.HomeViewModelFactory
import kotlinx.coroutines.launch

class MusicFragment : Fragment() {
    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(SpotifyRepository(requireContext()))
    }

    private lateinit var newReleaseAdapter: NewReleaseAdapter
    private lateinit var popularSongsAdapter: PopularSongsAdapter
    private lateinit var popularArtistAdapter: PopularArtistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        val accessToken = AuthTokenManager.getAccessToken(requireContext())
        if (accessToken != null) {
            // Nếu có token, gọi ViewModel để lấy dữ liệu
            viewModel.loadNewReleases(accessToken)
            viewModel.loadUserTopTracks(accessToken)
            viewModel.loadUserTopArtists(accessToken)
        } else {
            // Nếu không có token, hiển thị thông báo hoặc chuyển hướng đến màn hình đăng nhập
            Toast.makeText(
                requireContext(),
                "Bạn chưa đăng nhập, vui lòng đăng nhập!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setupRecyclerView() {
        newReleaseAdapter = NewReleaseAdapter { album ->
            val bundle = Bundle().apply {
                putString("id", album.id)
                putString("type", "album")
            }

            // Lấy NavController từ parent fragment (HomeFragment)
            val navController = parentFragment?.findNavController()
            navController?.navigate(R.id.albumDetailFragment, bundle)
        }
        popularSongsAdapter =
            PopularSongsAdapter { track ->
                val bundle = Bundle().apply {
                    putString("id", track.id)
                    putString("type", "track")
                }

                // Lấy NavController từ parent fragment (HomeFragment)
                val navController = parentFragment?.findNavController()
                navController?.navigate(R.id.albumDetailFragment, bundle)
            }

        popularArtistAdapter =
            PopularArtistAdapter { artist ->
                val bundle = Bundle().apply {
                    putString("id", artist.id)
                }
                val navController = parentFragment?.findNavController()
                navController?.navigate(R.id.artistDetailFragment, bundle)
            }


        val charts = listOf(
            Chart("ch1", "Top 50 Hits Việt Nam", R.drawable.img_50_hits_vietnam),
            Chart("ch2", "Top 100 Hits Thế giới", R.drawable.img_50_hits_thegioi),
        )

        binding.newReleasesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = newReleaseAdapter
        }

        // Cấu hình RecyclerView cho "Bài hát phổ biến"
        binding.popularSongsRecyclerView.apply {
            layoutManager = GridLayoutManager(
                context,
                2,
                GridLayoutManager.HORIZONTAL,
                false
            ) // 2 cột, scroll ngang
            adapter = popularSongsAdapter
        }

        // Cấu hình RecyclerView cho "Nghệ sĩ phổ biến"
        binding.popularArtistsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = popularArtistAdapter
        }

        // Cấu hình RecyclerView cho "Bảng xếp hạng"
        binding.chartsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = ChartAdapter(charts, onItemClick = { chart ->
                Toast.makeText(
                    context,
                    "Clicked on Chart: ${chart.description}",
                    Toast.LENGTH_SHORT
                ).show()
            })
        }

    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.newReleases.collect { newReleases ->
                        newReleaseAdapter.submitList(newReleases)
                    }
                }
                launch {
                    viewModel.topTracks.collect { topTracks ->
                        popularSongsAdapter.submitList(topTracks)
                    }
                }
                launch {
                    viewModel.topArtists.collect { artists ->
                        popularArtistAdapter.submitList(artists)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}