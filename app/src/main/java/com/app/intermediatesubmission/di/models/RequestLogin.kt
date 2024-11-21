package com.app.intermediatesubmission.di.models

import com.google.gson.annotations.SerializedName

data class RequestLogin (

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("password")
    val password: String? = null,
)
