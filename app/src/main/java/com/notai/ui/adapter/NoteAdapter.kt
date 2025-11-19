package com.notai.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.notai.R
import com.notai.data.model.NoteWithTags
import java.text.SimpleDateFormat
import java.util.*

/**
 * 笔记列表适配器
 */
class NoteAdapter(
    private val onNoteClick: (NoteWithTags) -> Unit,
    private val onNoteLongClick: (NoteWithTags) -> Unit
) : ListAdapter<NoteWithTags, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.noteTitleTextView)
        private val contentTextView: TextView = itemView.findViewById(R.id.noteContentTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.noteDateTextView)
        private val tagsContainer: ChipGroup = itemView.findViewById(R.id.tagsContainer)
        
        fun bind(noteWithTags: NoteWithTags) {
            val note = noteWithTags.note
            titleTextView.text = note.title
            contentTextView.text = note.content
            
            // 格式化日期
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            dateTextView.text = dateFormat.format(Date(note.updatedAt))
            
            // 显示标签
            tagsContainer.removeAllViews()
            if (noteWithTags.tags.isNotEmpty()) {
                tagsContainer.visibility = View.VISIBLE
                noteWithTags.tags.forEach { tag ->
                    val chip = Chip(itemView.context)
                    chip.text = tag.name
                    chip.isClickable = false
                    chip.isCheckable = false
                    chip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor(tag.color)
                    )
                    chip.chipTextColor = android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.WHITE
                    )
                    tagsContainer.addView(chip)
                }
            } else {
                tagsContainer.visibility = View.GONE
            }
            
            itemView.setOnClickListener {
                onNoteClick(noteWithTags)
            }
            
            itemView.setOnLongClickListener {
                onNoteLongClick(noteWithTags)
                true
            }
        }
    }
    
    class NoteDiffCallback : DiffUtil.ItemCallback<NoteWithTags>() {
        override fun areItemsTheSame(oldItem: NoteWithTags, newItem: NoteWithTags): Boolean {
            return oldItem.note.id == newItem.note.id
        }
        
        override fun areContentsTheSame(oldItem: NoteWithTags, newItem: NoteWithTags): Boolean {
            return oldItem == newItem
        }
    }
}

