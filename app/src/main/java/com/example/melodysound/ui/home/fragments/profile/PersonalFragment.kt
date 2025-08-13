package com.example.melodysound.ui.home.fragments.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.repository.SpotifyRepository
import com.example.melodysound.databinding.FragmentPersonalBinding
import com.example.melodysound.ui.auth.AuthActivity
import com.example.melodysound.ui.common.AuthTokenManager
import com.example.melodysound.ui.home.HomeViewModel
import com.example.melodysound.ui.home.adapter.profile.PlaylistsAdapter
import com.example.melodysound.ui.home.adapter.profile.ProfileAdapter
import com.example.melodysound.ui.home.fragments.HomeViewModelFactory
import kotlinx.coroutines.launch

class PersonalFragment : Fragment() {

    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            application = requireActivity().application,
            SpotifyRepository(requireContext())
        )
    }

    private lateinit var followingArtistsAdapter: ProfileAdapter
    private lateinit var playlistsAdapter: PlaylistsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()

        val accessToken = AuthTokenManager.getAccessToken(requireContext())
        if (accessToken != null) {
            viewModel.loadCurrentUser(accessToken)
            viewModel.loadFollowingArtists(accessToken)
            viewModel.loadCurrentUserPlaylists(accessToken)

            // đăng xuất
            binding.btnLogout.setOnClickListener {
                AlertDialog.Builder(requireActivity())
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn sẽ không thể nghe nhạc khi đăng xuất ,bạn có chắc chắn đăng xuất?")
                    .setPositiveButton("Đăng xuất") { dialog, which ->
                        AuthTokenManager.clearTokens(requireActivity())
                        // Chuyển đến màn hình đăng nhập
                        val intent = Intent(requireActivity(), AuthActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    .setNegativeButton("Hủy") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        } else {
            Toast.makeText(requireContext(), "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show()
        }

        observeViewModel()

    }

    private fun setupRecyclerViews() {
        followingArtistsAdapter = ProfileAdapter(
            onItemClick = { artist ->
                Toast.makeText(requireContext(), "clicked ${artist.name}", Toast.LENGTH_SHORT)
                    .show()
            }
        )
        binding.rcFollowing.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = followingArtistsAdapter
        }
        playlistsAdapter = PlaylistsAdapter()
        binding.rcMyPlaylist.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = playlistsAdapter
        }

        // Tương tự cho rc_my_playlist nếu bạn cần
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.currentUser.collect { user ->
                        user?.let {
                            // Cập nhật giao diện với thông tin người dùng
                            binding.txtDisplayName.text = user.displayName
                            if (user.followers.total == 0) {
                                binding.txtFollowersCount.visibility = View.GONE
                            } else {
                                binding.txtFollowersCount.text =
                                    "${user.followers.total} người đang theo dõi"
                            }

                            // Load ảnh đại diện
                            val imageUrl = user.images.firstOrNull()?.url
                            if (imageUrl != null) {
                                Glide.with(this@PersonalFragment)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.logo) // Ảnh placeholder khi đang load
                                    .error(R.drawable.logo) // Ảnh khi load lỗi
                                    .into(binding.imgAvatar)
                            } else {
                                binding.imgAvatar.setImageResource(R.drawable.logo)
                            }
                        }
                    }
                }

                launch {
                    viewModel.followingArtists.collect { followingArtists ->
                        followingArtists?.let {
                            binding.txtFollowingCount.text = "${it.artists.total} người đang follow"
                            val limitedArtists = it.artists.items.take(3)
                            followingArtistsAdapter.submitList(limitedArtists)
                        }
                    }
                }

                launch {
                    viewModel.userPlaylists.collect { playlists ->
                        val limitedPlaylists = playlists.take(3)
                        playlistsAdapter.submitList(limitedPlaylists)
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