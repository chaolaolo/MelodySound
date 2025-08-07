package com.example.melodysound.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.model.AlbumItem
import com.example.melodysound.data.model.Artist
import com.example.melodysound.data.model.Song
import com.example.melodysound.databinding.ItemNewReleaseBinding

class NewReleaseAdapter(
    private val onItemClick: (AlbumItem) -> Unit
) :
    RecyclerView.Adapter<NewReleaseAdapter.NewReleaseViewHolder>() {

    fun submitList(list: List<AlbumItem>) {
        newReleases = list
        notifyDataSetChanged()
    }

    private var newReleases: List<AlbumItem> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewReleaseViewHolder {
        val binding = ItemNewReleaseBinding.inflate(LayoutInflater.from(parent.context))
        return NewReleaseViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NewReleaseViewHolder,
        position: Int
    ) {
        holder.bind(newReleases[position])
    }

    override fun getItemCount(): Int {
        return newReleases.size
    }

    inner class NewReleaseViewHolder(private val binding: ItemNewReleaseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(album: AlbumItem) {
            binding.txtSongName.text = album.name
            binding.txtArtistName.text = album.artists.firstOrNull()?.name ?: "Unknown Artist"
            val imageUrl = album.images.firstOrNull()?.url
            if (imageUrl != null) {
                Glide.with(binding.songThumbnail.context)
                    .load(imageUrl)
                    .into(binding.songThumbnail)
            } else {
                // Xử lý trường hợp không có ảnh
                binding.songThumbnail.setImageResource(R.drawable.ic_launcher_round)
            }

            binding.root.setOnClickListener {
                onItemClick(album)
            }
        }
    }
}