package com.example.melodysound.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.melodysound.R
import com.example.melodysound.data.model.Artist
import com.example.melodysound.data.model.TrackItem
import com.example.melodysound.databinding.ItemArtistBinding

class PopularArtistAdapter(
    private val onItemClick: (Artist) -> Unit
): RecyclerView.Adapter<PopularArtistAdapter.ArtistViewHolder>() {

    private var artists: List<Artist> = emptyList()

    fun submitList(newArtists: List<Artist>) {
        artists = newArtists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PopularArtistAdapter.ArtistViewHolder {
        val binding = ItemArtistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArtistViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ArtistViewHolder,
        position: Int
    ) {
        holder.bind(artists[position])
    }


    override fun getItemCount(): Int {
      return artists.size
    }

    inner class ArtistViewHolder(private val binding: ItemArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(artist: Artist) {
            binding.txtArtistName.text = artist.name

            // Lấy URL ảnh từ danh sách images
            val imageUrl = artist.images?.firstOrNull()?.url
            if (imageUrl != null) {
                Glide.with(binding.imgArtistAvatar.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_round) // Sử dụng ảnh placeholder
                    .circleCrop() // Hiển thị ảnh profile dưới dạng hình tròn
                    .into(binding.imgArtistAvatar)
            } else {
                binding.imgArtistAvatar.setImageResource(R.drawable.ic_launcher_round)
            }
            binding.rootItemArtist.setOnClickListener {
                onItemClick(artist)
            }
        }
    }

}