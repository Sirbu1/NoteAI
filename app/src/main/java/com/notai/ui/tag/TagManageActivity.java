package com.notai.ui.tag;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.notai.R;
import com.notai.data.model.Tag;
import com.notai.databinding.ActivityTagManageBinding;
import com.notai.ui.adapter.TagManageAdapter;
import com.notai.ui.viewmodel.TagViewModel;

import java.util.List;

/**
 * 标签管理界面
 */
public class TagManageActivity extends AppCompatActivity {
    
    private ActivityTagManageBinding binding;
    private TagViewModel viewModel;
    private TagManageAdapter tagAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTagManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        viewModel = new ViewModelProvider(this, new TagViewModel.Factory()).get(TagViewModel.class);
        
        setupToolbar();
        setupRecyclerView();
        setupFab();
        observeViewModel();
    }
    
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void setupRecyclerView() {
        tagAdapter = new TagManageAdapter(
                tag -> showEditTagDialog(tag),
                tag -> showDeleteTagDialog(tag.getId())
        );
        
        binding.tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.tagsRecyclerView.setAdapter(tagAdapter);
    }
    
    private void setupFab() {
        binding.fabAddTag.setOnClickListener(v -> showAddTagDialog());
    }
    
    private void observeViewModel() {
        viewModel.getTags().observe(this, tags -> {
            if (tags != null) {
                tagAdapter.submitList(tags);
                binding.emptyTextView.setVisibility(tags.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });
    }
    
    private void showAddTagDialog() {
        EditText input = new EditText(this);
        input.setHint(getString(R.string.tag_name));
        
        new AlertDialog.Builder(this)
                .setTitle(R.string.add_tag)
                .setView(input)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String tagName = input.getText().toString().trim();
                    if (!tagName.isEmpty()) {
                        Tag tag = new Tag(tagName);
                        viewModel.insertTag(tag);
                    } else {
                        Toast.makeText(this, R.string.empty_title, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
    
    private void showEditTagDialog(Tag tag) {
        EditText input = new EditText(this);
        input.setText(tag.getName());
        input.setHint(getString(R.string.tag_name));
        
        new AlertDialog.Builder(this)
                .setTitle(R.string.edit_tag)
                .setView(input)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String tagName = input.getText().toString().trim();
                    if (!tagName.isEmpty()) {
                        Tag updatedTag = new Tag(tag.getId(), tagName, tag.getColor());
                        viewModel.updateTag(updatedTag);
                    } else {
                        Toast.makeText(this, R.string.empty_title, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
    
    private void showDeleteTagDialog(long tagId) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete)
                .setMessage(R.string.confirm_delete_tag)
                .setPositiveButton(R.string.delete, (dialog, which) -> viewModel.deleteTag(tagId))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

