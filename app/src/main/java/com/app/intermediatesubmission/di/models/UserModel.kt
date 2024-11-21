package com.app.intermediatesubmission.di.models

data class UserModel (
    val name: String,
    val token: String,
    val isLogin: Boolean = false
)