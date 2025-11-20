package com.notai.ui.tag;

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
import com.notai.data.model.Tag;
import com.notai.databinding.ActivityTagManageBinding;
import com.notai.ui.adapter.TagManageAdapter;
import com.notai.ui.viewmodel.TagViewModel;

import java.util.List;

/**
 * 标签管理界面
 */
public class TagManageActivity extends AppCompatActivity {
    
    private static final String TAG = "TagManageActivity";
    
    private ActivityTagManageBinding binding;
    private TagViewModel viewModel;
    private TagManageAdapter tagAdapter;
    
    // ==================== Activity生命周期方法 ====================
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        binding = ActivityTagManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        viewModel = new ViewModelProvider(this, new TagViewModel.Factory()).get(TagViewModel.class);
        
        setupToolbar();
        setupRecyclerView();
        setupFab();
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
        observeViewModel();
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
        // LiveData会自动更新，无需手动刷新
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        // 清理资源
        if (binding != null) {
            binding = null;
        }
        if (tagAdapter != null) {
            tagAdapter = null;
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
        // 可以保存一些状态信息
    }
    
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
        // 恢复保存的状态
    }
    
    // ==================== 初始化方法 ====================
    
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
    
    // ==================== 数据观察方法 ====================
    
    private void observeViewModel() {
        viewModel.getTags().observe(this, tags -> {
            if (tags != null) {
                tagAdapter.submitList(tags);
                binding.emptyTextView.setVisibility(tags.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });
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

