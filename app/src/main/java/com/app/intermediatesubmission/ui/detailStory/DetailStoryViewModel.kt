package com.app.intermediatesubmission.ui.detailStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.intermediatesubmission.di.StoryRepository
import com.app.intermediatesubmission.di.models.StoryItem
import kotlinx.coroutines.launch

class DetailStoryViewModel(private val repo: StoryRepository): ViewModel() {

    val loading = repo.loading

    val error = repo.error

    private val _detailStory = MutableLiveData<StoryItem?>()
    val detailStory: LiveData<StoryItem?> = _detailStory

    fun getDetailStory(id: String) {
        viewModelScope.launch {
            val result = repo.getDetailStory(id)
            _detailStory.postValue(result)
        }
    }
}