package com.notai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.notai.R
import com.notai.data.model.Tag

/**
 * 标签 Chip 适配器（用于笔记编辑界面）
 */
class TagChipAdapter(
    private val onTagSelected: (Long, Boolean) -> Unit
) : ListAdapter<Tag, TagChipAdapter.TagChipViewHolder>(TagDiffCallback()) {
    
    private val selectedTagIds = mutableSetOf<Long>()
    
    fun setSelectedTagIds(ids: Set<Long>) {
        selectedTagIds.clear()
        selectedTagIds.addAll(ids)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagChipViewHolder {
        val chip = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tag_chip, parent, false) as Chip
        return TagChipViewHolder(chip)
    }
    
    override fun onBindViewHolder(holder: TagChipViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class TagChipViewHolder(private val chip: Chip) : RecyclerView.ViewHolder(chip) {
        fun bind(tag: Tag) {
            chip.text = tag.name
            chip.isChecked = selectedTagIds.contains(tag.id)
            
            try {
                val color = android.graphics.Color.parseColor(tag.color)
                chip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(color)
            } catch (e: Exception) {
                chip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#6200EE")
                )
            }
            
            chip.setOnCheckedChangeListener { _, isChecked ->
                onTagSelected(tag.id, isChecked)
            }
        }
    }
    
    class TagDiffCallback : DiffUtil.ItemCallback<Tag>() {
        override fun areItemsTheSame(oldItem: Tag, newItem: Tag): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Tag, newItem: Tag): Boolean {
            return oldItem == newItem
        }
    }
}

