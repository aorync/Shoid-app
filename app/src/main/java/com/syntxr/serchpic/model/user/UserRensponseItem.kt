package com.syntxr.serchpic.model.register


import com.google.gson.annotations.SerializedName

data class UserRensponseItem(
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("username")
    val username: String
)