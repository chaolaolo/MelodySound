package com.example.melodysound.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.model.AlbumItem
import com.example.melodysound.databinding.ItemArtistAlbumBinding

class ArtistAlbumsAdapter(
    private val albums: MutableList<AlbumItem> = mutableListOf(),
    private val onItemClick: (AlbumItem) -> Unit
) : RecyclerView.Adapter<ArtistAlbumsAdapter.AlbumViewHolder>() {

    inner class AlbumViewHolder(private val binding: ItemArtistAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: AlbumItem) {
            binding.txtAlbumName.text = album.name
            binding.txtAlbumType.text = album.albumType.replaceFirstChar { it.uppercase() }
            binding.txtTimeReleased.text = album.releaseDate.split("-").firstOrNull()

            val imageUrl = album.images.firstOrNull()?.url
            if (imageUrl != null) {
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.logo)
                    .into(binding.imgAlbumThumbnail)
            } else {
                binding.imgAlbumThumbnail.setImageResource(R.drawable.logo)
            }

            binding.root.setOnClickListener { onItemClick(album) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = ItemArtistAlbumBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(albums[position])
    }

    override fun getItemCount(): Int = albums.size

    fun submitList(newAlbums: List<AlbumItem>) {
        albums.clear()
        albums.addAll(newAlbums)
        notifyDataSetChanged()
    }


}