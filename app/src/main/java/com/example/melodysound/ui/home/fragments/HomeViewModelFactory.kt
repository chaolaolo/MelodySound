package com.example.melodysound.ui.home.fragments

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.melodysound.data.repository.SpotifyRepository
import com.example.melodysound.ui.home.HomeViewModel
import dagger.hilt.android.internal.Contexts.getApplication


class HomeViewModelFactory(
    private val application: Application,
    private val repository: SpotifyRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}