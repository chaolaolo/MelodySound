package com.example.melodysound.ui.home.adapter.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.model.PlaylistResponse
import com.example.melodysound.databinding.ItemRecomendCardBinding // Đảm bảo đúng tên binding class

class PlaylistsAdapter : RecyclerView.Adapter<PlaylistsAdapter.PlaylistViewHolder>() {

    private var playlists: List<PlaylistResponse> = emptyList()

    fun submitList(newPlaylists: List<PlaylistResponse>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemRecomendCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
    }

    override fun getItemCount(): Int = playlists.size

    inner class PlaylistViewHolder(private val binding: ItemRecomendCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: PlaylistResponse) {
            binding.txtTrackName.text = playlist.name
            binding.txtArtistName.text = "By ${playlist.owner.display_name}"

            val imageUrl = playlist.images.firstOrNull()?.url
            if (imageUrl != null) {
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(binding.albumThumbnail)
            } else {
                binding.albumThumbnail.setImageResource(R.drawable.logo)
            }
        }
    }
}