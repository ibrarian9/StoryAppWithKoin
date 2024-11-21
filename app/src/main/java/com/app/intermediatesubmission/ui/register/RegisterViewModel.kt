package com.app.intermediatesubmission.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.intermediatesubmission.di.StoryRepository
import com.app.intermediatesubmission.di.models.RequestRegister
import kotlinx.coroutines.launch

class RegisterViewModel(private val repo: StoryRepository): ViewModel() {

    val loading = repo.loading

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    fun postRegister(register: RequestRegister){
        viewModelScope.launch {
            val res = repo.postRegister(register)
            _registerResult.postValue(res)
        }
    }
}