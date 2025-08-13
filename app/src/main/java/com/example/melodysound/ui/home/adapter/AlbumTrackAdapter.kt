package com.example.melodysound.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.melodysound.data.model.Artist
import com.example.melodysound.data.model.TrackItem
import com.example.melodysound.databinding.ItemAlbumTrackBinding

class AlbumTrackAdapter(
    private val onItemClick: (TrackItem) -> Unit
) : RecyclerView.Adapter<AlbumTrackAdapter.TrackViewHolder>() {

    private var tracks: List<TrackItem> = emptyList()

    fun submitList(newTracks: List<TrackItem>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder {
        val binding = ItemAlbumTrackBinding.inflate(
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
        val track = tracks[position]
        holder.bind(track, position)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }


    inner class TrackViewHolder(private val binding: ItemAlbumTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(track: TrackItem, position: Int) {
            // Hiển thị tên bài hát
            binding.txtTrackName.text = track.name

            // Hiển thị tên nghệ sĩ
            binding.txtArtistsName.text = track.artists.joinToString(", ") { it.name }

            // Xử lý sự kiện click vào bài hát nếu cần
            binding.root.setOnClickListener {
                onItemClick(track)
            }
        }

    }
}