package com.app.intermediatesubmission.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.intermediatesubmission.di.StoryRepository
import com.app.intermediatesubmission.di.models.RequestLogin
import com.app.intermediatesubmission.di.models.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: StoryRepository): ViewModel() {

    val loading = repo.loading

    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> = _loginResult

    fun getSession(): LiveData<UserModel> {
        return repo.getSession().asLiveData()
    }

    fun postLogin(reqLogin: RequestLogin) {
        viewModelScope.launch {
            val result = repo.postLogin(reqLogin)
            _loginResult.postValue(result)
        }
    }

}