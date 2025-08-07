package com.example.melodysound.ui.auth.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.melodysound.R
import com.example.melodysound.databinding.FragmentChoiceAuthBinding
import com.example.melodysound.ui.auth.AuthNavigator

class ChoiceAuthFragment : Fragment() {
    private var _binding: FragmentChoiceAuthBinding? = null
    private val binding get() = _binding!!

    private var authNavigator: AuthNavigator? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AuthNavigator) {
            authNavigator = context
        } else {
            throw RuntimeException("$context must implement AuthNavigator")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChoiceAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSignUpFree.setOnClickListener {
            authNavigator?.navigateToChoiceSignUp()
        }
        binding.btnSignIn.setOnClickListener {
            authNavigator?.navigateToChoiceLogin()
        }
    }

    override fun onDetach() {
        super.onDetach()
        authNavigator = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}