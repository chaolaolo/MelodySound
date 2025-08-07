package com.example.melodysound.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.melodysound.data.model.Chart
import com.example.melodysound.databinding.ItemChartCardBinding


class ChartAdapter(private val charts: List<Chart>,
                   private val onItemClick: (Chart) -> Unit) :
    RecyclerView.Adapter<ChartAdapter.ChartViewHolder>() {

    inner class ChartViewHolder(private val binding: ItemChartCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chart: Chart) {
            binding.txtDescriptionChart.text = chart.description
            binding.imgChartThumbnail.setImageResource(chart.thumbnailUrl)

            binding.root.setOnClickListener {
                onItemClick(chart)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewHolder {
        val binding = ItemChartCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChartViewHolder, position: Int) {
        holder.bind(charts[position])
    }

    override fun getItemCount(): Int = charts.size
}