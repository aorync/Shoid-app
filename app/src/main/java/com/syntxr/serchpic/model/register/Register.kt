package com.syntxr.serchpic.model.register

import com.google.gson.annotations.SerializedName

data class Register(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email:String,
    @SerializedName("password")
    val password: String,
    @SerializedName("image")
    val image: String?
)
