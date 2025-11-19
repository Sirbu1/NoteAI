package com.notai.ui.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.notai.R;
import com.notai.data.model.NoteWithTags;
import com.notai.data.model.Tag;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 笔记列表适配器
 */
public class NoteAdapter extends ListAdapter<NoteWithTags, NoteAdapter.NoteViewHolder> {
    
    private final OnNoteClickListener onNoteClick;
    private final OnNoteLongClickListener onNoteLongClick;
    
    public interface OnNoteClickListener {
        void onNoteClick(NoteWithTags noteWithTags);
    }
    
    public interface OnNoteLongClickListener {
        void onNoteLongClick(NoteWithTags noteWithTags);
    }
    
    public NoteAdapter(OnNoteClickListener onNoteClick, OnNoteLongClickListener onNoteLongClick) {
        super(new NoteDiffCallback());
        this.onNoteClick = onNoteClick;
        this.onNoteLongClick = onNoteLongClick;
    }
    
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
    
    class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView contentTextView;
        private final TextView dateTextView;
        private final ChipGroup tagsContainer;
        
        NoteViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.noteTitleTextView);
            contentTextView = itemView.findViewById(R.id.noteContentTextView);
            dateTextView = itemView.findViewById(R.id.noteDateTextView);
            tagsContainer = itemView.findViewById(R.id.tagsContainer);
        }
        
        void bind(NoteWithTags noteWithTags) {
            com.notai.data.model.Note note = noteWithTags.getNote();
            titleTextView.setText(note.getTitle());
            contentTextView.setText(note.getContent());
            
            // 格式化日期
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            dateTextView.setText(dateFormat.format(new Date(note.getUpdatedAt())));
            
            // 显示标签
            tagsContainer.removeAllViews();
            List<Tag> tags = noteWithTags.getTags();
            if (tags != null && !tags.isEmpty()) {
                tagsContainer.setVisibility(View.VISIBLE);
                for (Tag tag : tags) {
                    Chip chip = new Chip(itemView.getContext());
                    chip.setText(tag.getName());
                    chip.setClickable(false);
                    chip.setCheckable(false);
                    try {
                        int color = Color.parseColor(tag.getColor());
                        chip.setChipBackgroundColor(ColorStateList.valueOf(color));
                        chip.setChipTextColor(ColorStateList.valueOf(Color.WHITE));
                    } catch (Exception e) {
                        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#6200EE")));
                        chip.setChipTextColor(ColorStateList.valueOf(Color.WHITE));
                    }
                    tagsContainer.addView(chip);
                }
            } else {
                tagsContainer.setVisibility(View.GONE);
            }
            
            itemView.setOnClickListener(v -> onNoteClick.onNoteClick(noteWithTags));
            itemView.setOnLongClickListener(v -> {
                onNoteLongClick.onNoteLongClick(noteWithTags);
                return true;
            });
        }
    }
    
    static class NoteDiffCallback extends DiffUtil.ItemCallback<NoteWithTags> {
        @Override
        public boolean areItemsTheSame(@NonNull NoteWithTags oldItem, @NonNull NoteWithTags newItem) {
            return oldItem.getNote().getId() == newItem.getNote().getId();
        }
        
        @Override
        public boolean areContentsTheSame(@NonNull NoteWithTags oldItem, @NonNull NoteWithTags newItem) {
            return oldItem.equals(newItem);
        }
    }
}

