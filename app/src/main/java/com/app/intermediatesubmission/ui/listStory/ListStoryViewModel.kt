package com.app.intermediatesubmission.ui.listStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.app.intermediatesubmission.di.StoryRepository
import com.app.intermediatesubmission.di.models.StoryItem
import com.app.intermediatesubmission.di.models.UserModel
import kotlinx.coroutines.launch

class ListStoryViewModel(private val repo: StoryRepository): ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repo.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
        }
    }

    fun getAllStory(): LiveData<PagingData<StoryItem>> {
        return repo.getAllStory().cachedIn(viewModelScope)
    }
}
