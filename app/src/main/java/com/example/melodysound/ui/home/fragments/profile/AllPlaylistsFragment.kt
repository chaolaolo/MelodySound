package com.example.melodysound.ui.home.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.melodysound.R
import com.example.melodysound.data.repository.SpotifyRepository
import com.example.melodysound.databinding.FragmentAllPlaylistsBinding
import com.example.melodysound.ui.common.AuthTokenManager
import com.example.melodysound.ui.home.HomeViewModel
import com.example.melodysound.ui.home.adapter.profile.AllFollowingAdapter
import com.example.melodysound.ui.home.adapter.profile.AllPlaylistsAdapter
import com.example.melodysound.ui.home.fragments.HomeViewModelFactory
import kotlinx.coroutines.launch
import kotlin.getValue

class AllPlaylistsFragment : Fragment() {

    private var _binding: FragmentAllPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            application = requireActivity().application,
            SpotifyRepository(requireContext())
        )
    }
    private lateinit var allPlaylistsAdapter: AllPlaylistsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAllPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        allPlaylistsAdapter = AllPlaylistsAdapter(
            onItemClick = { playlist ->
                val bundle = Bundle().apply {
                    putString("id", playlist.id)
                }
                val navController = parentFragment?.findNavController()
                navController?.navigate(R.id.playlistDetailFragment, bundle)
            }
        )
        binding.rcAllPlaylist.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = allPlaylistsAdapter
        }

        val accessToken = AuthTokenManager.getAccessToken(requireContext())
        if (accessToken != null) {
            viewModel.loadCurrentUserPlaylists(accessToken)
        } else {
            Toast.makeText(requireContext(), "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userPlaylists.collect { playlists ->
                        playlists?.let {
                            allPlaylistsAdapter.submitList(playlists)
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}