package com.app.intermediatesubmission.di

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.app.intermediatesubmission.di.api.ApiService
import com.app.intermediatesubmission.di.database.StoryDatabase
import com.app.intermediatesubmission.di.models.RequestLogin
import com.app.intermediatesubmission.di.models.RequestRegister
import com.app.intermediatesubmission.di.models.StoryItem
import com.app.intermediatesubmission.di.models.UserModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository (
    private val userPref: UserPreferences,
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
){

    private val timeLoading: Long = 1000

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getSession(): Flow<UserModel> {
        return userPref.getSession()
    }

    private suspend fun saveSession(user: UserModel){
        return userPref.saveSession(user)
    }

    suspend fun logout(){
        return userPref.logout()
    }

    suspend fun postRegister(reqRegister: RequestRegister): Result<String> {
        _loading.postValue(true)
        return try {
            delay(timeLoading)
            val res = apiService.postRegister(reqRegister)
            if (res.isSuccessful){
                Result.success(res.body()!!.message)
            } else {
                Result.failure(Exception("Data Is Null"))
            }
        } catch (e: Exception){
            Result.failure(e)
        } finally {
            _loading.postValue(false)
        }
    }

    suspend fun postLogin(reqLogin: RequestLogin): Result<String> {
        _loading.postValue(true)
        return try {
            delay(timeLoading)
            val res = apiService.postLogin(reqLogin)
            if (res.isSuccessful) {
                val body = res.body()
                if (body != null) {
                    val token = body.loginResult.token
                    val name = body.loginResult.name
                    this.saveSession(UserModel(name, token))
                    Result.success(body.message)
                } else {
                    Result.failure(Exception("Response Body is Null"))
                }
            } else {
                Result.failure(Exception("Error Code: ${res.code()} - ${res.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _loading.postValue(false)
        }
    }

    suspend fun getLocationStory(): List<StoryItem> {
        return try {
            val result = apiService.getStory(location = 1)
            if (result.isSuccessful){
                result.body()!!.listStory
            } else {
                emptyList()
            }
        } catch (e: Exception){
            emptyList()
        }
    }

    fun getAllStory(): LiveData<PagingData<StoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase = storyDatabase, apiService = apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun postStory(desc: RequestBody, poto: MultipartBody.Part, lat: RequestBody?, long: RequestBody?): Result<String> {
        _loading.postValue(true)
        return try {
            delay(timeLoading)
            val response = apiService.postStory(desc = desc, poto = poto, lat = lat, lon = long)
            if (response.isSuccessful){
                Result.success(response.body()!!.message)
            } else {
                Result.failure(Exception("Data Is Null"))
            }
        } catch (e: Exception){
            Result.failure(e)
        } finally {
            _loading.postValue(false)
        }
    }

    suspend fun getDetailStory(id: String): StoryItem? {
        _loading.postValue(true)
        return try {
            delay(timeLoading)
            val response = apiService.getDetailStory(id)
            if (response.isSuccessful){
               response.body()?.story
            } else {
                _error.postValue("Data Is Null")
                null
            }
        } catch (e: Exception) {
            _error.postValue(e.toString())
           null
        } finally {
            _loading.postValue(false)
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}