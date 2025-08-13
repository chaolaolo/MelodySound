package com.example.melodysound.ui.home.fragments.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.model.Artist
import com.example.melodysound.data.model.Chart
import com.example.melodysound.data.model.PlaylistResponse
import com.example.melodysound.data.repository.SpotifyRepository
import com.example.melodysound.databinding.FragmentTop50Binding
import com.example.melodysound.ui.common.AuthTokenManager
import com.example.melodysound.ui.home.HomeViewModel
import com.example.melodysound.ui.home.OnTrackSelectedListener
import com.example.melodysound.ui.home.adapter.AlbumTrackAdapter
import com.example.melodysound.ui.home.adapter.ArtistAlbumsAdapter
import com.example.melodysound.ui.home.adapter.ArtistTopTracksAdapter
import com.example.melodysound.ui.home.adapter.Top50Adapter
import com.example.melodysound.ui.home.fragments.HomeViewModelFactory
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.getValue

class Top50Fragment : Fragment() {

    private var _binding: FragmentTop50Binding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            application = requireActivity().application,
            SpotifyRepository(requireContext())
        )
    }
    private lateinit var top50Adapter: Top50Adapter

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
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTop50Binding.inflate(inflater, container, false)
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
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isCollapsed = Math.abs(verticalOffset) >= appBarLayout.totalScrollRange
//            binding.collapsingToolbar.title = if (isCollapsed) binding.txtTitle.text else ""
            binding.imgTopChart.visibility = if (isCollapsed) View.GONE else View.VISIBLE
        })

        val id = arguments?.getString("id") ?: ""
        val accessToken = AuthTokenManager.getAccessToken(requireContext())


        if (accessToken != null && id.isNotEmpty()) {
            viewModel.loadTop50PlaylistDetails(accessToken, id)
            viewModel.loadTop50Tracks(accessToken, id)
        } else {
            Toast.makeText(
                requireContext(),
                "Bạn chưa đăng nhập, vui lòng đăng nhập!",
                Toast.LENGTH_LONG
            ).show()
        }

        setupRecyclerViews()
        setupObservers()
    }


    private fun setupRecyclerViews() {
        top50Adapter = Top50Adapter(onItemClick = { track ->
            val accessToken = AuthTokenManager.getAccessToken(requireContext())
            if (accessToken != null) {
                viewModel.playTrack(accessToken, "spotify:track:${track.id}")
                viewModel.loadPlayerTrackDetails(accessToken, track.id)
                trackSelectedListener?.onTrackSelected(track.id)
            } else {
                Toast.makeText(context, "Không có access token", Toast.LENGTH_SHORT).show()
            }
        })

        binding.rcTop50.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = top50Adapter
            isNestedScrollingEnabled = false // Vô hiệu hóa cuộn lồng nhau
        }

    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.top50Playlist.collect { playlist ->
                        playlist?.let {
                            updatePlaylistUI(it)
                        }
                    }
                }
                launch {
                    viewModel.top50Tracks.collect { tracks ->
                        if (tracks.isNotEmpty()) {
                            top50Adapter.submitList(tracks)
                        } else {
                            Log.d("Top50Fragment", "No tracks received or list is empty.")
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
                            Log.e("Top50Fragment", "Error: $errorMessage")
                        }
                    }
                }
            }
        }
    }

    private fun updatePlaylistUI(playlist: PlaylistResponse) {
        binding.collapsingToolbar.title = playlist.name
        binding.txtDescriptionChart.text = playlist.description ?: "Bảng xếp hạng hàng đầu"
        val imageUrl = playlist.images.firstOrNull()?.url
        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .into(binding.imgTopChart)
            Glide.with(this)
                .load(imageUrl)
                .into(binding.imgMiniTopChartThumbnail)
        } else {
            binding.imgTopChart.setImageResource(R.drawable.top100vn)
            binding.imgMiniTopChartThumbnail.setImageResource(R.drawable.top100vn)
        }
        binding.txtTopChartReleaseTime.text = "Cập nhật hàng ngày"
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