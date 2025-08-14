package com.example.melodysound.ui.home.adapter.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.model.Artist
import com.example.melodysound.data.model.PlaylistResponse
import com.example.melodysound.databinding.ItemAllPlaylistsBinding
import com.example.melodysound.databinding.ItemRecomendCardBinding // Đảm bảo đúng tên binding class

class AllPlaylistsAdapter(
    private val onItemClick: (PlaylistResponse) -> Unit
) : RecyclerView.Adapter<AllPlaylistsAdapter.PlaylistViewHolder>() {

    private var playlists: List<PlaylistResponse> = emptyList()

    fun submitList(newPlaylists: List<PlaylistResponse>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemAllPlaylistsBinding.inflate(
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

    inner class PlaylistViewHolder(private val binding: ItemAllPlaylistsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: PlaylistResponse) {
            binding.txtPlaylistName.text = playlist.name
            binding.txtArtistName.text = "By ${playlist.owner.display_name}"

            val imageUrl = playlist.images.firstOrNull()?.url
            if (imageUrl != null) {
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(binding.imgPlaylistThumbnail)
            } else {
                binding.imgPlaylistThumbnail.setImageResource(R.drawable.logo)
            }
            binding.root.setOnClickListener {
                onItemClick(playlist)
            }
        }
    }
}