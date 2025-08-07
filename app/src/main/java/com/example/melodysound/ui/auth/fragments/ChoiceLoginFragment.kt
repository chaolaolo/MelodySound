package com.example.melodysound.ui.auth.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.melodysound.R
import com.example.melodysound.constants.Constants
import com.example.melodysound.databinding.FragmentChoiceLoginBinding
import com.example.melodysound.ui.auth.AuthNavigator
import com.example.melodysound.ui.auth.AuthViewModel
import com.example.melodysound.ui.home.HomeActivity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

@Suppress("DEPRECATION")
class ChoiceLoginFragment : Fragment() {
    private var _binding: FragmentChoiceLoginBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChoiceLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLoginSpotify.setOnClickListener {
            Log.d("ChoiceLoginFragment", "Button login Spotify clicked")
            val request = authViewModel.createSpotifyAuthRequest()
            (activity as? AuthNavigator)?.onSpotifyAuthRequested(request)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}