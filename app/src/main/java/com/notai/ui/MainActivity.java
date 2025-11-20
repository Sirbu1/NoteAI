package com.notai.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.notai.R;
import com.notai.data.model.NoteWithTags;
import com.notai.databinding.ActivityMainBinding;
import com.notai.ui.adapter.NoteAdapter;
import com.notai.ui.note.NoteEditActivity;
import com.notai.ui.tag.TagManageActivity;
import com.notai.ui.viewmodel.NoteViewModel;

import java.util.List;

/**
 * 主界面 - 显示笔记列表
 */
public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    
    private ActivityMainBinding binding;
    private NoteViewModel viewModel;
    private NoteAdapter noteAdapter;
    
    // ==================== Activity生命周期方法 ====================
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        try {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            
            viewModel = new ViewModelProvider(this, new NoteViewModel.Factory()).get(NoteViewModel.class);
            
            setupToolbar();
            setupRecyclerView();
            setupSearch();
            setupFab();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "应用启动失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.d(TAG, "onPostCreate");
        // onCreate完成后调用，可以在这里进行一些初始化后的操作
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        observeViewModel();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        // Activity可见且可交互
        // 刷新数据（如果需要）
        if (viewModel != null && binding != null && binding.searchEditText != null) {
            String searchText = binding.searchEditText.getText().toString();
            if (!searchText.isEmpty()) {
                viewModel.searchNotes(searchText);
            } else {
                viewModel.searchNotes("");
            }
        }
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
        // 可以在这里暂停动画、保存临时数据等
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        // Activity不再可见
        // 可以在这里停止后台任务、释放资源等
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
        // Activity从停止状态重新启动
        // 可以在这里刷新数据
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        // 清理资源
        if (binding != null) {
            binding = null;
        }
        if (noteAdapter != null) {
            noteAdapter = null;
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
        // 保存搜索状态
        if (binding != null && binding.searchEditText != null) {
            String searchText = binding.searchEditText.getText().toString();
            outState.putString("search_text", searchText);
        }
    }
    
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
        // 恢复搜索状态
        if (binding != null && binding.searchEditText != null) {
            String searchText = savedInstanceState.getString("search_text", "");
            binding.searchEditText.setText(searchText);
            if (!searchText.isEmpty()) {
                viewModel.searchNotes(searchText);
            }
        }
    }
    
    // ==================== 初始化方法 ====================
    
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
    }
    
    private void setupRecyclerView() {
        noteAdapter = new NoteAdapter(
                noteWithTags -> openNoteEdit(noteWithTags.getNote().getId()),
                noteWithTags -> showDeleteDialog(noteWithTags.getNote().getId())
        );
        
        binding.notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.notesRecyclerView.setAdapter(noteAdapter);
    }
    
    private void setupSearch() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.searchNotes(s != null ? s.toString() : "");
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void setupFab() {
        binding.fabAddNote.setOnClickListener(v -> openNoteEdit(null));
    }
    
    private void observeViewModel() {
        viewModel.getNotes().observe(this, notes -> {
            if (notes != null) {
                noteAdapter.submitList(notes);
                binding.emptyTextView.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }
    
    // ==================== 菜单方法 ====================
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_manage_tags) {
            startActivity(new Intent(this, TagManageActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    // ==================== 业务方法 ====================
    
    private void openNoteEdit(Long noteId) {
        Intent intent = new Intent(this, NoteEditActivity.class);
        if (noteId != null) {
            intent.putExtra(NoteEditActivity.EXTRA_NOTE_ID, noteId);
        }
        startActivity(intent);
    }
    
    private void showDeleteDialog(long noteId) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete)
                .setMessage(R.string.confirm_delete_note)
                .setPositiveButton(R.string.delete, (dialog, which) -> viewModel.deleteNote(noteId))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}

