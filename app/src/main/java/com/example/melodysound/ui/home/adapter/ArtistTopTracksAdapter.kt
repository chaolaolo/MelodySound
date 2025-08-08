package com.example.melodysound.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.model.Track
import com.example.melodysound.data.model.TrackItem
import com.example.melodysound.databinding.ItemTrackBinding
import java.text.DecimalFormat

class ArtistTopTracksAdapter(
    private val tracks: MutableList<Track> = mutableListOf(),
    private val onItemClick: (Track) -> Unit
) : RecyclerView.Adapter<ArtistTopTracksAdapter.TrackViewHolder>() {

    fun submitList(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

    inner class TrackViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(track: Track, position: Int) {
            binding.txtNumerOrder.text = (position + 1).toString()
            binding.txtTrackName.text = track.name
            val formatter = DecimalFormat("#,###,###")
            val formattedFollowers =
                formatter.format(track.popularity * 1000000L) // Giá trị giả lập
            binding.txtTotalStream.text = formattedFollowers

            val imageUrl = track.album.images.firstOrNull()?.url
            if (imageUrl != null) {
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.logo)
                    .into(binding.imgTrackThumbnail)
            } else {
                binding.imgTrackThumbnail.setImageResource(R.drawable.logo)
            }

            binding.root.setOnClickListener { onItemClick(track) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int
    ) {
        holder.bind(tracks[position], position)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}