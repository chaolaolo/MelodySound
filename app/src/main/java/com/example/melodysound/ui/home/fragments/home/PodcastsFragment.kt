package com.example.melodysound.ui.home.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.melodysound.data.model.Podcast
import com.example.melodysound.databinding.FragmentPodcastsBinding
import com.example.melodysound.ui.home.adapter.PodcastsAdapter

class PodcastsFragment : Fragment() {
    private var _binding: FragmentPodcastsBinding? = null
    private val binding get() = _binding!!

    private val podcastList = mutableListOf<Podcast>()
    private lateinit var podcastsAdapter: PodcastsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPodcastsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPodcastsRecyclerView()
        loadSamplePodcasts()
    }

    private fun setupPodcastsRecyclerView() {
        podcastsAdapter = PodcastsAdapter(
            podcasts = podcastList,
            onItemClick = { podcast ->
                Toast.makeText(context, "Clicked podcast: ${podcast.title}", Toast.LENGTH_SHORT)
                    .show()
            },
            onAddToEpisodeClick = { podcast ->
                Toast.makeText(
                    context,
                    "Added '${podcast.title}' to episode list",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onPlayPauseClick = { podcast, position ->
                // Xử lý khi click vào nút Play/Pause
                val currentlyPlayingIndex = podcastList.indexOfFirst { it.isPlaying }
                if (currentlyPlayingIndex != -1 && currentlyPlayingIndex != position) {
                    // Dừng podcast đang phát cũ
                    podcastsAdapter.updatePodcastPlayingState(currentlyPlayingIndex, false)
                }
                val newPlayingState = !podcast.isPlaying
                podcastsAdapter.updatePodcastPlayingState(position, newPlayingState)

                Toast.makeText(
                    context,
                    "${if (newPlayingState) "Playing" else "Paused"}: ${podcast.title}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onVolumeClick = { podcast, position ->
                val newMuteState = !podcast.isMuted
                podcastsAdapter.updatePodcastMuteState(
                    position,
                    newMuteState
                ) // Gọi phương thức mới
                Toast.makeText(
                    context,
                    "${if (newMuteState) "Muted" else "Unmuted"}: ${podcast.title}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.rcPodcasts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = podcastsAdapter
        }
    }

    private fun loadSamplePodcasts() {
        val samplePodcasts = listOf(
            Podcast(
                "p1",
                "Podcast Cuộc Sống Tươi Đẹp",
                "Episode 1 - Sức mạnh của tư duy tích cực",
                "https://picsum.photos/seed/podcast1/640/640",
                "Jul 24", "7min",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
            ),
            Podcast(
                "p2",
                "Câu Chuyện Khởi Nghiệp",
                "Episode 5 - Bài học từ thất bại",
                "https://picsum.photos/seed/podcast2/640/640",
                "Aug 01", "12min",
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
            ),
            Podcast(
                "p3",
                "Khoa Học & Đời Sống",
                "Episode 10 - Bí ẩn của vũ trụ",
                "https://picsum.photos/seed/podcast3/640/640",
                "Aug 05", "15min",
                "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo."
            ),
        )
        // Cập nhật dữ liệu vào Adapter
        podcastList.addAll(samplePodcasts)
        podcastsAdapter.notifyDataSetChanged()
    }


}