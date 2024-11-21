package com.app.intermediatesubmission.ui.addStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.intermediatesubmission.di.StoryRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repo: StoryRepository): ViewModel() {

    val loading = repo.loading

    private val _resultStory = MutableLiveData<Result<String>>()
    val resultStory: LiveData<Result<String>> = _resultStory

    fun postStory(desc: RequestBody, image: MultipartBody.Part, lat: RequestBody?, long: RequestBody?) {
        viewModelScope.launch {
            val result = repo.postStory(desc = desc, poto = image, lat = lat, long = long)
            _resultStory.postValue(result)
        }
    }
}
