package com.app.intermediatesubmission.di

import androidx.room.Room
import com.app.intermediatesubmission.di.api.ApiService
import com.app.intermediatesubmission.di.database.StoryDatabase
import com.app.intermediatesubmission.ui.addStory.AddStoryViewModel
import com.app.intermediatesubmission.ui.detailStory.DetailStoryViewModel
import com.app.intermediatesubmission.ui.listStory.ListStoryViewModel
import com.app.intermediatesubmission.ui.login.LoginViewModel
import com.app.intermediatesubmission.ui.mapStory.MapsViewModel
import com.app.intermediatesubmission.ui.register.RegisterViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    // Provide UserPreference Instance
    single { UserPreferences(get()) }

    single {
        val pref: UserPreferences = get()
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(Interceptor { chain ->
                val request = chain.request()
                val user = runBlocking {
                    pref.getSession().first()
                }
                val requestHeaders = request.newBuilder()
                    .addHeader("Authorization", "Bearer ${user.token}")
                    .build()
                chain.proceed(requestHeaders)
            })
            .build()
    }

    // Provide Retrofit instance
    single {
        val baseUrl = "https://story-api.dicoding.dev/v1/"

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get<OkHttpClient>())
            .build()
    }

    // Provide Api Service Instance
    single { get<Retrofit>().create(ApiService::class.java) }

    // Provide the QuoteDatabase instance
    single {
        Room.databaseBuilder(
            androidContext(),
            StoryDatabase::class.java,
            "story_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // Provide DAOs
    single { get<StoryDatabase>().storyDao() }
    single { get<StoryDatabase>().remoteKeysDao() }

    // Provide DataStore Instance
    single { androidContext().dataStore }

    // Provide Repository
    single { StoryRepository(apiService = get(), userPref = get(), storyDatabase = get()) }

    // View Model Definitions
    viewModel { LoginViewModel(get()) }
    viewModel { ListStoryViewModel(get()) }
    viewModel { DetailStoryViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { AddStoryViewModel(get()) }
    viewModel { MapsViewModel(get()) }
}