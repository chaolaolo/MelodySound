package com.example.melodysound.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.model.TrackItem
import com.example.melodysound.databinding.ItemTrackBinding
import com.example.melodysound.ui.home.adapter.ArtistTopTracksAdapter.TrackViewHolder
import java.text.DecimalFormat

class Top50Adapter (
    private val onItemClick: (TrackItem) -> Unit
) : RecyclerView.Adapter<Top50Adapter.Top50ViewHolder>() {

    private val tracks = mutableListOf<TrackItem>()

    fun submitList(newTracks: List<TrackItem>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Top50ViewHolder {
        val binding = ItemTrackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return Top50ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: Top50ViewHolder,
        position: Int
    ) {
        val track = tracks[position]
        holder.bind(track, position)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    inner class Top50ViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(track: TrackItem, position: Int) {
            // Hiển thị thứ tự (position + 1)
            binding.txtNumerOrder.text = (position + 1).toString()

            // Tên bài hát
            binding.txtTrackName.text = track.name

            // Ảnh bìa
            val imageUrl = track.album?.images?.firstOrNull()?.url
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.logo)
                    .into(binding.imgTrackThumbnail)
            } else {
                binding.imgTrackThumbnail.setImageResource(R.drawable.logo)
            }

            // Số lượng stream (popularity)
            val formatter = DecimalFormat("#,###,###")
            val formattedPopularity = formatter.format(track.popularity)
            binding.txtTotalStream.text = formattedPopularity

            // Xử lý click
            binding.root.setOnClickListener { onItemClick(track) }
        }
    }
}