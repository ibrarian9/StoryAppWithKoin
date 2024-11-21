package com.app.intermediatesubmission.di.api

import com.app.intermediatesubmission.di.models.RequestLogin
import com.app.intermediatesubmission.di.models.RequestRegister
import com.app.intermediatesubmission.di.models.ResponseData
import com.app.intermediatesubmission.di.models.ResponseDetailStory
import com.app.intermediatesubmission.di.models.ResponseStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    suspend fun postRegister(@Body reqRegister: RequestRegister): Response<ResponseData>

    @POST("login")
    suspend fun postLogin(@Body reqLogin: RequestLogin): Response<ResponseData>

    @GET("stories")
    suspend fun getStory(
        @Query("location") location: Int = 0,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): Response<ResponseStory>

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id: String
    ): Response<ResponseDetailStory>

    @POST("stories")
    @Multipart
    suspend fun postStory(
        @Part("description") desc: RequestBody,
        @Part poto: MultipartBody.Part,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Response<ResponseData>
}