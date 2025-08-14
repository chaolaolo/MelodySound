package com.example.melodysound.ui.home.adapter.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.model.Artist
import com.example.melodysound.data.model.TrackItem
import com.example.melodysound.databinding.ItemAllArtistBinding
import com.example.melodysound.databinding.ItemArtistBinding

class AllFollowingAdapter(
    private val onItemClick: (Artist) -> Unit
) : RecyclerView.Adapter<AllFollowingAdapter.ArtistViewHolder>() {

    private var artists: List<Artist> = emptyList()

    fun submitList(newArtists: List<Artist>) {
        artists = newArtists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val binding = ItemAllArtistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArtistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artist = artists[position]
        holder.bind(artist)
    }

    override fun getItemCount(): Int = artists.size

    inner class ArtistViewHolder(private val binding: ItemAllArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(artist: Artist) {
            binding.txtArtistName.text = artist.name

            // Load ảnh của nghệ sĩ bằng Glide
            val imageUrl = artist.images.firstOrNull()?.url
            if (imageUrl != null) {
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(binding.imgArtistAvatar)
            } else {
                binding.imgArtistAvatar.setImageResource(R.drawable.logo)
            }

            binding.root.setOnClickListener {
                onItemClick(artist)
            }
        }
    }
}