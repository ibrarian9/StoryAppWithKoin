package com.app.intermediatesubmission.ui.detailStory

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.intermediatesubmission.R
import com.app.intermediatesubmission.databinding.ActivityDetailStoryBinding
import com.app.intermediatesubmission.di.Injection.messageToast
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var bind: ActivityDetailStoryBinding
    private val detailStoryViewModel: DetailStoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(bind.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bind.apply {

            val storyId = intent.getStringExtra(STORY_ID)

            if (!storyId.isNullOrEmpty()){
                detailStoryViewModel.getDetailStory(storyId)
            }

            detailStoryViewModel.error.observe(this@DetailStoryActivity) { err ->
                err?.let {
                    messageToast(this@DetailStoryActivity, it)
                }
            }

            detailStoryViewModel.loading.observe(this@DetailStoryActivity) { isLoading ->
                loading.visibility = if (isLoading) View.VISIBLE else View.GONE
            }

            detailStoryViewModel.detailStory.observe(this@DetailStoryActivity) { result ->
                result?.let {
                    storyJudul.text = it.name
                    storyDesc.text = it.description
                    Glide.with(this@DetailStoryActivity).load(it.photoUrl).into(storyPoto)
                }
            }

        }
    }

    companion object {
        const val STORY_ID = "id"
    }
}