package com.app.intermediatesubmission.ui.mapStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.intermediatesubmission.di.StoryRepository
import com.app.intermediatesubmission.di.models.StoryItem
import kotlinx.coroutines.launch

class MapsViewModel(private val repo: StoryRepository): ViewModel() {

    private val _locationStory = MutableLiveData<List<StoryItem>>()
    val locationStory: LiveData<List<StoryItem>> = _locationStory

    init {
        getAllLocationStory()
    }

    private fun getAllLocationStory(){
        viewModelScope.launch {
            val result = repo.getLocationStory()
            _locationStory.postValue(result)
        }
    }

}