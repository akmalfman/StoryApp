package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemStoryBinding
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailActivity

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.MyStoryHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStoryHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyStoryHolder(binding)
    }

    override fun onBindViewHolder(holder: MyStoryHolder, position: Int) {
        val review = getItem(position)
        if (review != null) {
            holder.bind(review)
        }

    }

    class MyStoryHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.tvItemNama.text = "${story.name}"
            binding.tvItemDesc.text = "${story.description}"
            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.imgItemStory)

            Log.d("name_story", "bind: ${story.name}")
            itemView.setOnClickListener {
                val intentDetail = Intent(itemView.context, DetailActivity::class.java)
                intentDetail.putExtra("story_id", story.id)
                itemView.context.startActivity(intentDetail)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}