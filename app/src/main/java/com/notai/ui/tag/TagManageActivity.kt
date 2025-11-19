package com.notai.ui.tag

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.notai.R
import com.notai.data.model.Tag
import com.notai.databinding.ActivityTagManageBinding
import com.notai.ui.adapter.TagManageAdapter
import com.notai.ui.viewmodel.TagViewModel
import kotlinx.coroutines.launch

/**
 * 标签管理界面
 */
class TagManageActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTagManageBinding
    private val viewModel: TagViewModel by viewModels { TagViewModel.provideFactory() }
    private lateinit var tagAdapter: TagManageAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTagManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupFab()
        observeViewModel()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    private fun setupRecyclerView() {
        tagAdapter = TagManageAdapter(
            onEditClick = { tag ->
                showEditTagDialog(tag)
            },
            onDeleteClick = { tag ->
                showDeleteTagDialog(tag.id)
            }
        )
        
        binding.tagsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TagManageActivity)
            adapter = tagAdapter
        }
    }
    
    private fun setupFab() {
        binding.fabAddTag.setOnClickListener {
            showAddTagDialog()
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.tags.collect { tags ->
                tagAdapter.submitList(tags)
                binding.emptyTextView.visibility = if (tags.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }
        }
    }
    
    private fun showAddTagDialog() {
        val input = EditText(this)
        input.hint = getString(R.string.tag_name)
        
        AlertDialog.Builder(this)
            .setTitle(R.string.add_tag)
            .setView(input)
            .setPositiveButton(R.string.save) { _, _ ->
                val tagName = input.text.toString().trim()
                if (tagName.isNotEmpty()) {
                    val tag = Tag(name = tagName)
                    viewModel.insertTag(tag)
                } else {
                    Toast.makeText(this, R.string.empty_title, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun showEditTagDialog(tag: Tag) {
        val input = EditText(this)
        input.setText(tag.name)
        input.hint = getString(R.string.tag_name)
        
        AlertDialog.Builder(this)
            .setTitle(R.string.edit_tag)
            .setView(input)
            .setPositiveButton(R.string.save) { _, _ ->
                val tagName = input.text.toString().trim()
                if (tagName.isNotEmpty()) {
                    val updatedTag = tag.copy(name = tagName)
                    viewModel.updateTag(updatedTag)
                } else {
                    Toast.makeText(this, R.string.empty_title, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun showDeleteTagDialog(tagId: Long) {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_delete)
            .setMessage(R.string.confirm_delete_tag)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteTag(tagId)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

