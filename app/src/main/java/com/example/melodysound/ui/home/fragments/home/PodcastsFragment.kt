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
                "https://static-images.vnncdn.net/images/2023/3/25/0008Q7/song-tre.jpg",
                "Jul 24", "7min",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
            ),
            Podcast(
                "p2",
                "Câu Chuyện Khởi Nghiệp",
                "Episode 5 - Bài học từ thất bại",
                "https://i.scdn.co/image/ab67656300005f1ff3d20e6d9213237db21d8a02",
                "Aug 01", "12min",
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
            ),
            Podcast(
                "p3",
                "Khoa Học & Đời Sống",
                "Episode 10 - Bí ẩn của vũ trụ",
                "https://i.scdn.co/image/ab6765630000ba8aec512df3930dcefb140d60a8",
                "Aug 05", "15min",
                "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo."
            ),
            Podcast(
                "p5",
                "Tản Mạn Văn Học",
                "Episode 3 - Sức sống trong thơ ca Nguyễn Du",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSS7T6dJEAm5zwof5sRTgjJ_TMW1yHam5jGfg&s",
                "Aug 10", "10min",
                "Nghiên cứu và chia sẻ về các tác phẩm văn học kinh điển, đặc biệt là tác phẩm Truyện Kiều, khám phá giá trị nghệ thuật và nhân văn sâu sắc."
            ),
            Podcast(
                "p6",
                "Yoga & Thiền Định",
                "Episode 2 - 10 phút để thư giãn và tập trung",
                "https://i.scdn.co/image/ab67656300005f1f1af7023ac59342711bc11a70",
                "Aug 12", "8min",
                "Hướng dẫn các bài tập thiền đơn giản, giúp bạn giải tỏa căng thẳng, tìm lại sự bình yên và tăng cường sự tập trung trong cuộc sống."
            ),
            Podcast(
                "p7",
                "Hành Trình Khám Phá",
                "Episode 8 - Vẻ đẹp của Vịnh Hạ Long",
                "https://cdn1.fahasa.com/media/catalog/product/c/o/combo-3003202301_2_.jpg",
                "Aug 15", "14min",
                "Chia sẻ những kinh nghiệm du lịch, khám phá các điểm đến nổi tiếng, văn hóa độc đáo và ẩm thực phong phú trên khắp Việt Nam."
            ),
            Podcast(
                "p8",
                "Phát Triển Bản Thân",
                "Episode 6 - Xây dựng thói quen tốt",
                "https://i.scdn.co/image/ab6765630000ba8a47e28e49b5bb45ad94cea8b6",
                "Aug 18", "11min",
                "Cung cấp các công cụ và lời khuyên thiết thực để bạn phát triển bản thân, cải thiện kỹ năng giao tiếp và đạt được mục tiêu trong cuộc sống."
            ),
            Podcast(
                "p9",
                "Kinh Tế Vĩ Mô",
                "Episode 4 - Tác động của lạm phát đến nền kinh tế",
                "https://static.ybox.vn/2020/6/2/1592291734419-TOP%209%20PODCAST%20TI%E1%BA%BENG%20VI%E1%BB%86T%20B%E1%BA%AET%20NH%E1%BB%8AP%20V%E1%BB%9AI%20N%E1%BB%80N%20KINH%20T%E1%BA%BE%20TO%C3%80N%20C%E1%BA%A6U%20H%E1%BA%ACU%20COVID-19%20WAVES15922917031592291708.png",
                "Aug 20", "20min",
                "Phân tích chuyên sâu về các vấn đề kinh tế vĩ mô, giúp bạn hiểu rõ hơn về cách thức hoạt động của thị trường và các chính sách tài chính."
            ),
            Podcast(
                "p10",
                "Bên Tách Cà Phê",
                "Episode 9 - Câu chuyện về sự kiên trì",
                "https://i.scdn.co/image/ab67656300005f1f6c46051205567c9faf763309",
                "Aug 22", "9min",
                "Một series podcast nhẹ nhàng, kể những câu chuyện đời thường, truyền cảm hứng về sự nỗ lực, hy vọng và sức mạnh của tình người."
            ),
            Podcast(
                "p11",
                "Lịch Sử Việt Nam",
                "Episode 15 - Các triều đại phong kiến",
                "https://cdn.tgdd.vn/Files/2023/01/31/1506294/kham-pha-lich-su-viet-nam-the-gioi-voi-7-kenh-podcast-sieu-cuon-202301311707130516.jpg",
                "Aug 25", "18min",
                "Tóm tắt và phân tích các giai đoạn lịch sử quan trọng của Việt Nam, từ thời kỳ Hùng Vương đến các triều đại phong kiến cuối cùng."
            ),
            Podcast(
                "p12",
                "Âm Nhạc & Cuộc Sống",
                "Episode 7 - Sức ảnh hưởng của nhạc Rock",
                "https://i.ytimg.com/vi/QhSI2zWKRtY/maxresdefault.jpg",
                "Aug 28", "13min",
                "Khám phá các thể loại âm nhạc khác nhau, từ nguồn gốc, sự phát triển đến tác động của chúng đối với văn hóa và xã hội."
            ),
            Podcast(
                "p13",
                "Khoa Học Máy Tính",
                "Episode 11 - Trí tuệ nhân tạo (AI)",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQcaMX7M-M5xt3XNlnhVkAXqXmVoT0Z0TvW5g&s",
                "Aug 30", "16min",
                "Giải thích các khái niệm phức tạp trong khoa học máy tính và công nghệ, với những ví dụ dễ hiểu về Trí tuệ nhân tạo và Machine Learning."
            ),
            Podcast(
                "p14",
                "Chăm Sóc Sức Khỏe",
                "Episode 10 - Ngủ ngon và lợi ích bất ngờ",
                "https://www.wowweekend.vn/document_root/upload/articles/image/BrowseContent/New%20Lifestyle/Straight%20No%20Mixer/202212/7%20podcast%20v%E1%BB%81%20s%E1%BB%A9c%20kho%E1%BA%BB/wwk-ted-health-podcast.jpg",
                "Sep 02", "9min",
                "Những lời khuyên hữu ích về sức khỏe thể chất và tinh thần, từ chế độ ăn uống, tập luyện đến cách cải thiện giấc ngủ."
            )
        )
        // Cập nhật dữ liệu vào Adapter
        podcastList.addAll(samplePodcasts)
        podcastsAdapter.notifyDataSetChanged()
    }


}