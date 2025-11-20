package com.notai.ui.note;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.notai.R;
import com.notai.data.model.Note;
import com.notai.data.model.NoteWithTags;
import com.notai.data.model.Tag;
import com.notai.databinding.ActivityNoteEditBinding;
import com.notai.ui.adapter.TagChipAdapter;
import com.notai.ui.viewmodel.NoteViewModel;
import com.notai.ui.viewmodel.TagViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 笔记编辑界面
 */
public class NoteEditActivity extends AppCompatActivity {
    
    private static final String TAG = "NoteEditActivity";
    
    private ActivityNoteEditBinding binding;
    private NoteViewModel noteViewModel;
    private TagViewModel tagViewModel;
    
    private Long noteId = null;
    private Set<Long> selectedTagIds = new HashSet<>();
    private TagChipAdapter tagChipAdapter;
    private Note currentNote = null;
    
    public static final String EXTRA_NOTE_ID = "extra_note_id";
    
    // ==================== Activity生命周期方法 ====================
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        binding = ActivityNoteEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        long noteIdExtra = getIntent().getLongExtra(EXTRA_NOTE_ID, -1);
        if (noteIdExtra != -1) {
            noteId = noteIdExtra;
        }
        
        noteViewModel = new ViewModelProvider(this, new NoteViewModel.Factory()).get(NoteViewModel.class);
        tagViewModel = new ViewModelProvider(this, new TagViewModel.Factory()).get(TagViewModel.class);
        
        setupToolbar();
        setupTagRecyclerView();
        setupListeners();
        
        // 恢复保存的状态
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.d(TAG, "onPostCreate");
        // onCreate完成后调用
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        loadNote();
        loadTags();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        // Activity可见且可交互
        // LiveData会自动更新，无需手动刷新
    }
    
    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume");
        // onResume完成后调用
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        // Activity失去焦点但仍可见
        // 自动保存草稿
        saveDraft();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        // Activity不再可见
        // 可以在这里停止后台任务
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
        // Activity从停止状态重新启动
        // 刷新数据
        loadTags();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        // 清理资源
        if (binding != null) {
            binding = null;
        }
        if (tagChipAdapter != null) {
            tagChipAdapter = null;
        }
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged: " + hasFocus);
        // 窗口焦点变化时调用
        if (hasFocus) {
            // 窗口获得焦点时的操作
        } else {
            // 窗口失去焦点时的操作
        }
    }
    
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        // 保存编辑内容
        if (binding != null) {
            outState.putString("title", binding.titleEditText.getText().toString());
            outState.putString("content", binding.contentEditText.getText().toString());
            if (noteId != null) {
                outState.putLong("note_id", noteId);
            }
        }
    }
    
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
        restoreInstanceState(savedInstanceState);
    }
    
    // ==================== 初始化方法 ====================
    
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(noteId != null ? getString(R.string.edit_note) : getString(R.string.add_note));
        }
    }
    
    private void setupTagRecyclerView() {
        tagChipAdapter = new TagChipAdapter((tagId, selected) -> {
            if (selected) {
                selectedTagIds.add(tagId);
            } else {
                selectedTagIds.remove(tagId);
            }
        });
        
        binding.tagsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.tagsRecyclerView.setAdapter(tagChipAdapter);
    }
    
    private void setupListeners() {
        binding.addTagButton.setOnClickListener(v -> showAddTagDialog());
        binding.saveFab.setOnClickListener(v -> saveNote());
    }
    
    // ==================== 数据加载方法 ====================
    
    private void loadNote() {
        if (noteId != null) {
            noteViewModel.getNoteWithTagsById(noteId).observe(this, noteWithTags -> {
                if (noteWithTags != null) {
                    currentNote = noteWithTags.getNote();
                    binding.titleEditText.setText(currentNote.getTitle());
                    binding.contentEditText.setText(currentNote.getContent());
                    selectedTagIds = new HashSet<>();
                    if (noteWithTags.getTags() != null) {
                        for (Tag tag : noteWithTags.getTags()) {
                            selectedTagIds.add(tag.getId());
                        }
                    }
                    updateTagSelection();
                }
            });
        }
    }
    
    private void loadTags() {
        tagViewModel.getTags().observe(this, tags -> {
            if (tags != null) {
                tagChipAdapter.submitList(tags);
                updateTagSelection();
            }
        });
    }
    
    private void updateTagSelection() {
        if (tagChipAdapter != null) {
            tagChipAdapter.setSelectedTagIds(selectedTagIds);
        }
    }
    
    // ==================== 状态保存和恢复方法 ====================
    
    private void restoreInstanceState(Bundle savedInstanceState) {
        String title = savedInstanceState.getString("title", "");
        String content = savedInstanceState.getString("content", "");
        if (binding != null) {
            if (!title.isEmpty()) {
                binding.titleEditText.setText(title);
            }
            if (!content.isEmpty()) {
                binding.contentEditText.setText(content);
            }
        }
        long savedNoteId = savedInstanceState.getLong("note_id", -1);
        if (savedNoteId != -1 && noteId == null) {
            noteId = savedNoteId;
        }
    }
    
    private void saveDraft() {
        // 可选：在onPause时保存草稿
        // 这里可以根据需要实现自动保存功能
    }
    
    // ==================== 业务方法 ====================
    
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
                        tagViewModel.insertTag(tag);
                    } else {
                        Toast.makeText(this, R.string.empty_title, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
    
    private void saveNote() {
        String title = binding.titleEditText.getText().toString().trim();
        String content = binding.contentEditText.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(this, R.string.empty_title, Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (content.isEmpty()) {
            Toast.makeText(this, R.string.empty_content, Toast.LENGTH_SHORT).show();
            return;
        }
        
        Note note;
        if (noteId != null && currentNote != null) {
            // 更新笔记时保留原始创建时间
            note = new Note(noteId, title, content, currentNote.getCreatedAt(), System.currentTimeMillis());
        } else {
            note = new Note(title, content);
        }
        
        List<Long> tagIdsList = new ArrayList<>(selectedTagIds);
        if (noteId != null) {
            noteViewModel.updateNote(note, tagIdsList);
        } else {
            noteViewModel.insertNote(note, tagIdsList);
        }
        
        finish();
    }
    
    // ==================== 菜单方法 ====================
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

