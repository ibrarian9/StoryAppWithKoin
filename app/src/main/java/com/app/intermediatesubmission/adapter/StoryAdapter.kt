package com.app.intermediatesubmission.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.intermediatesubmission.databinding.ListLayoutBinding
import com.app.intermediatesubmission.di.models.StoryItem
import com.app.intermediatesubmission.ui.detailStory.DetailStoryActivity
import com.bumptech.glide.Glide

class StoryAdapter: PagingDataAdapter<StoryItem, StoryAdapter.ViewHolder>(CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding(getItem(position)!!)
    }

    class ViewHolder(
        private val bind: ListLayoutBinding
    ): RecyclerView.ViewHolder(bind.root) {
        fun binding(item: StoryItem) {
            bind.apply {
                judulStory.text = item.name
                textStory.text = item.description
                Glide.with(itemView.context).load(item.photoUrl).into(storyPoto)
            }
            itemView.setOnClickListener {
                val i = Intent(it.context, DetailStoryActivity::class.java)
                i.putExtra(DetailStoryActivity.STORY_ID, item.id)
                it.context.startActivity(i)
            }
        }
    }

    companion object {
        val CALLBACK = object : DiffUtil.ItemCallback<StoryItem>() {
            override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}