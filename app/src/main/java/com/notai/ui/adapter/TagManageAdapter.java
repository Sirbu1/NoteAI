package com.notai.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.notai.R;
import com.notai.data.model.Tag;

/**
 * 标签管理列表适配器
 */
public class TagManageAdapter extends ListAdapter<Tag, TagManageAdapter.TagViewHolder> {
    
    private final OnEditClickListener onEditClick;
    private final OnDeleteClickListener onDeleteClick;
    
    public interface OnEditClickListener {
        void onEditClick(Tag tag);
    }
    
    public interface OnDeleteClickListener {
        void onDeleteClick(Tag tag);
    }
    
    public TagManageAdapter(OnEditClickListener onEditClick, OnDeleteClickListener onDeleteClick) {
        super(new TagDiffCallback());
        this.onEditClick = onEditClick;
        this.onDeleteClick = onDeleteClick;
    }
    
    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);
        return new TagViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
    
    class TagViewHolder extends RecyclerView.ViewHolder {
        private final View tagColorView;
        private final TextView tagNameTextView;
        private final ImageButton editTagButton;
        private final ImageButton deleteTagButton;
        
        TagViewHolder(View itemView) {
            super(itemView);
            tagColorView = itemView.findViewById(R.id.tagColorView);
            tagNameTextView = itemView.findViewById(R.id.tagNameTextView);
            editTagButton = itemView.findViewById(R.id.editTagButton);
            deleteTagButton = itemView.findViewById(R.id.deleteTagButton);
        }
        
        void bind(Tag tag) {
            tagNameTextView.setText(tag.getName());
            
            try {
                int color = Color.parseColor(tag.getColor());
                tagColorView.setBackgroundColor(color);
            } catch (Exception e) {
                tagColorView.setBackgroundColor(Color.parseColor("#6200EE"));
            }
            
            editTagButton.setOnClickListener(v -> onEditClick.onEditClick(tag));
            deleteTagButton.setOnClickListener(v -> onDeleteClick.onDeleteClick(tag));
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

