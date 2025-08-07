package com.example.melodysound.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.model.Song
import com.example.melodysound.data.model.TrackItem
import com.example.melodysound.databinding.PopularSongBinding

class PopularSongsAdapter(
    private val onItemClick: (TrackItem) -> Unit // Callback khi click vào item
) : RecyclerView.Adapter<PopularSongsAdapter.SongViewHolder>() {

    private var songs: List<TrackItem> = emptyList()

    fun submitList(newSongs: List<TrackItem>) {
        songs = newSongs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SongViewHolder {
        val binding = PopularSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SongViewHolder,
        position: Int
    ) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    inner class SongViewHolder(private val binding: PopularSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(track: TrackItem) {
            binding.txtSongName.text = track.name
            binding.txtArtistName.text = track.artists.firstOrNull()?.name ?: "Unknown Artist"

            val imageUrl = track.album.images.firstOrNull()?.url
            if (imageUrl != null) {
                Glide.with(binding.songThumbnail.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_round)
                    .into(binding.songThumbnail)
            } else {
                binding.songThumbnail.setImageResource(R.drawable.ic_launcher_round)
            }
            // Xử lý sự kiện click vào toàn bộ item
            binding.rootItemAlbum.setOnClickListener {
                onItemClick(track)
            }
        }
    }
}