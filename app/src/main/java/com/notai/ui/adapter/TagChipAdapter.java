package com.notai.ui.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.notai.R;
import com.notai.data.model.Tag;

import java.util.HashSet;
import java.util.Set;

/**
 * 标签 Chip 适配器（用于笔记编辑界面）
 */
public class TagChipAdapter extends ListAdapter<Tag, TagChipAdapter.TagChipViewHolder> {
    
    private final Set<Long> selectedTagIds = new HashSet<>();
    private final OnTagSelectedListener onTagSelected;
    
    public interface OnTagSelectedListener {
        void onTagSelected(long tagId, boolean selected);
    }
    
    public TagChipAdapter(OnTagSelectedListener onTagSelected) {
        super(new TagDiffCallback());
        this.onTagSelected = onTagSelected;
    }
    
    public void setSelectedTagIds(Set<Long> ids) {
        selectedTagIds.clear();
        if (ids != null) {
            selectedTagIds.addAll(ids);
        }
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public TagChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Chip chip = (Chip) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag_chip, parent, false);
        return new TagChipViewHolder(chip);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TagChipViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
    
    class TagChipViewHolder extends RecyclerView.ViewHolder {
        private final Chip chip;
        
        TagChipViewHolder(Chip chip) {
            super(chip);
            this.chip = chip;
        }
        
        void bind(Tag tag) {
            chip.setText(tag.getName());
            chip.setChecked(selectedTagIds.contains(tag.getId()));
            
            try {
                int color = Color.parseColor(tag.getColor());
                chip.setChipBackgroundColor(ColorStateList.valueOf(color));
            } catch (Exception e) {
                chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#6200EE")));
            }
            
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                onTagSelected.onTagSelected(tag.getId(), isChecked);
            });
        }
    }
    
    static class TagDiffCallback extends DiffUtil.ItemCallback<Tag> {
        @Override
        public boolean areItemsTheSame(@NonNull Tag oldItem, @NonNull Tag newItem) {
            return oldItem.getId() == newItem.getId();
        }
        
        @Override
        public boolean areContentsTheSame(@NonNull Tag oldItem, @NonNull Tag newItem) {
            return oldItem.equals(newItem);
        }
    }
}

