package com.notai.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
    
    private ActivityMainBinding binding;
    private NoteViewModel viewModel;
    private NoteAdapter noteAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        viewModel = new ViewModelProvider(this, new NoteViewModel.Factory()).get(NoteViewModel.class);
        
        setupToolbar();
        setupRecyclerView();
        setupSearch();
        setupFab();
        observeViewModel();
    }
    
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
}

