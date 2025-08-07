package com.example.melodysound.ui.home.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.melodysound.databinding.FragmentHomeBinding
import com.example.melodysound.ui.home.adapter.HomeViewPagerAdapter
import com.example.melodysound.ui.home.fragments.home.MusicFragment
import com.example.melodysound.ui.home.fragments.home.PodcastsFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentList = listOf<Fragment>(
            MusicFragment(),
            PodcastsFragment()
        )

        val pagerAdapter = HomeViewPagerAdapter(childFragmentManager, lifecycle, fragmentList)

        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.isUserInputEnabled = false

        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Nhạc"
                }

                1 -> {
                    tab.text = "Podcasts"
                }
            }
        }.attach()

    }

}