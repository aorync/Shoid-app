package com.syntxr.serchpic.model.login

import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("id")
    val id : Int,
    @SerializedName("username")
    val username : String,
    @SerializedName("email")
    val email : String,
    @SerializedName("password")
    val password : String,
)
