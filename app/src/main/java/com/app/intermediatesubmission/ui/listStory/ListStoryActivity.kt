package com.app.intermediatesubmission.ui.listStory

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.intermediatesubmission.R
import com.app.intermediatesubmission.adapter.LoadingStateAdapter
import com.app.intermediatesubmission.adapter.StoryAdapter
import com.app.intermediatesubmission.databinding.ActivityListStoryBinding
import com.app.intermediatesubmission.ui.WelcomeActivity
import com.app.intermediatesubmission.ui.addStory.AddStoryActivity
import com.app.intermediatesubmission.ui.mapStory.MapsActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListStoryActivity : AppCompatActivity() {

    private val pagingAdapter = StoryAdapter()
    private lateinit var bind: ActivityListStoryBinding
    private val listStoryViewModel: ListStoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(bind.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bind.apply {

            btnLogout.setOnClickListener {
                listStoryViewModel.logout()
                val i = Intent(this@ListStoryActivity, WelcomeActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            }

            btnLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            btnMaps.setOnClickListener {
                startActivity(Intent(this@ListStoryActivity, MapsActivity::class.java))
            }

            btnAddStory.setOnClickListener {
                startActivity(Intent(this@ListStoryActivity, AddStoryActivity::class.java))
            }

            listStoryViewModel.getSession().observe(this@ListStoryActivity) {
                val token = it.token
                if (token.isNotEmpty()) {
                    rv.apply {
                        layoutManager = LinearLayoutManager(this@ListStoryActivity)
                        adapter = pagingAdapter.withLoadStateFooter(
                            footer = LoadingStateAdapter {
                                pagingAdapter.retry()
                            }
                        )
                    }

                    lifecycleScope.launch {
                       listStoryViewModel.getAllStory().observe(this@ListStoryActivity) { pagingData ->
                           pagingAdapter.submitData(lifecycle, pagingData)
                       }
                    }
                } else {
                    startActivity(Intent(this@ListStoryActivity, WelcomeActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        listStoryViewModel.getAllStory()
    }
}