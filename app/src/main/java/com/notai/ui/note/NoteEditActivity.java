package com.notai.ui.note;

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
    
    private ActivityNoteEditBinding binding;
    private NoteViewModel noteViewModel;
    private TagViewModel tagViewModel;
    
    private Long noteId = null;
    private Set<Long> selectedTagIds = new HashSet<>();
    private TagChipAdapter tagChipAdapter;
    
    public static final String EXTRA_NOTE_ID = "extra_note_id";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        loadNote();
        loadTags();
    }
    
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
    
    private void loadNote() {
        if (noteId != null) {
            NoteWithTags noteWithTags = noteViewModel.getNoteWithTagsById(noteId);
            if (noteWithTags != null) {
                binding.titleEditText.setText(noteWithTags.getNote().getTitle());
                binding.contentEditText.setText(noteWithTags.getNote().getContent());
                selectedTagIds = new HashSet<>();
                if (noteWithTags.getTags() != null) {
                    for (Tag tag : noteWithTags.getTags()) {
                        selectedTagIds.add(tag.getId());
                    }
                }
                updateTagSelection();
            }
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
        if (noteId != null) {
            note = new Note(noteId, title, content, 0, System.currentTimeMillis());
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
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

