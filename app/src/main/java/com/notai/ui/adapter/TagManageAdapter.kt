package com.notai.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.notai.R
import com.notai.data.model.Tag

/**
 * 标签管理列表适配器
 */
class TagManageAdapter(
    private val onEditClick: (Tag) -> Unit,
    private val onDeleteClick: (Tag) -> Unit
) : ListAdapter<Tag, TagManageAdapter.TagViewHolder>(TagDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tag, parent, false)
        return TagViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tagColorView: View = itemView.findViewById(R.id.tagColorView)
        private val tagNameTextView: TextView = itemView.findViewById(R.id.tagNameTextView)
        private val editTagButton: ImageButton = itemView.findViewById(R.id.editTagButton)
        private val deleteTagButton: ImageButton = itemView.findViewById(R.id.deleteTagButton)
        
        fun bind(tag: Tag) {
            tagNameTextView.text = tag.name
            
            try {
                val color = android.graphics.Color.parseColor(tag.color)
                tagColorView.setBackgroundColor(color)
            } catch (e: Exception) {
                tagColorView.setBackgroundColor(
                    android.graphics.Color.parseColor("#6200EE")
                )
            }
            
            editTagButton.setOnClickListener {
                onEditClick(tag)
            }
            
            deleteTagButton.setOnClickListener {
                onDeleteClick(tag)
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

