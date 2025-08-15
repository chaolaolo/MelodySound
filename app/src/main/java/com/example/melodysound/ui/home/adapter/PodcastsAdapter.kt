package com.example.melodysound.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.model.Podcast
import com.example.melodysound.databinding.ItemPodcastsBinding

class PodcastsAdapter(
    private val podcasts: MutableList<Podcast>, // Dùng MutableList để có thể thay đổi trạng thái
    private val onItemClick: (Podcast) -> Unit,
    private val onAddToEpisodeClick: (Podcast) -> Unit,
    private val onPlayPauseClick: (Podcast, Int) -> Unit,
    private val onVolumeClick: (Podcast, Int) -> Unit
) : RecyclerView.Adapter<PodcastsAdapter.PodcastViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PodcastViewHolder {
        val binding = ItemPodcastsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PodcastViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PodcastViewHolder,
        position: Int
    ) {
        holder.bind(podcasts[position], position)
    }

    override fun getItemCount(): Int {
        return podcasts.size
    }


    inner class PodcastViewHolder(private val binding: ItemPodcastsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(podcast: Podcast, position: Int) {
            binding.root.setOnClickListener {
                onItemClick(podcast)
            }

            binding.apply {
                // Đặt dữ liệu vào các TextView
                txtPodcastTitle.text = podcast.title
                txtEpisodeTitle.text = podcast.episodeTitle

                txtTime.text = "${podcast.publishDate} - ${podcast.duration}"
                txtPodcastsDescription.text = podcast.description

                // Tải ảnh bìa podcast bằng Glide
                Glide.with(imgThumbnail.context)
                    .load(podcast.thumbnailUrl)
                    .placeholder(R.drawable.logo) // Thay bằng drawable placeholder của bạn
                    .error(R.drawable.logo) // Thay bằng drawable lỗi của bạn
                    .into(imgThumbnail)

                // Cập nhật biểu tượng Play/Pause
                if (podcast.isPlaying) {
                    btnPause.setImageResource(R.drawable.outline_pause_circle) // Icon Pause
                } else {
                    btnPause.setImageResource(R.drawable.outline_play_circle) // Icon Play
                }

                // Cập nhật biểu tượng của volume
                if (podcast.isMuted) {
                    btnVolumn.setImageResource(R.drawable.outline_volume_off) // Icon tắt tiếng
                } else {
                    btnVolumn.setImageResource(R.drawable.outline_volume_up) // Icon bật tiếng (Đảm bảo bạn có icon này trong drawable)
                }

                // Xử lý sự kiện click cho các nút
                btnAddToEpisode.setOnClickListener {
                    onAddToEpisodeClick(podcast)
                }

                btnPause.setOnClickListener {
                    onPlayPauseClick(podcast, position)
                }

                btnMore.setOnClickListener {
                }

                btnVolumn.setOnClickListener {
                    onVolumeClick(podcast, position)
                }
            }
        }
    }

    fun updatePodcastPlayingState(position: Int, isPlaying: Boolean) {
        if (position >= 0 && position < podcasts.size) {
            val updatedPodcast = podcasts[position].copy(isPlaying = isPlaying)
            podcasts[position] = updatedPodcast
            notifyItemChanged(position)
        }
    }

    fun updatePodcastMuteState(position: Int, isMuted: Boolean) {
        if (position >= 0 && position < podcasts.size) {
            val updatedPodcast = podcasts[position].copy(isMuted = isMuted)
            podcasts[position] = updatedPodcast
            notifyItemChanged(position)
        }
    }

    // Phương thức để cập nhật toàn bộ danh sách (nếu cần)
    fun updateData(newPodcasts: List<Podcast>) {
        podcasts.clear()
        podcasts.addAll(newPodcasts)
        notifyDataSetChanged()
    }
}