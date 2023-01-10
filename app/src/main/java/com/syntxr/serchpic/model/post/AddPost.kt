package com.syntxr.serchpic.model.post

import com.google.gson.annotations.SerializedName

data class AddPost(
    @SerializedName("user_id")
    val userId : Int,
    @SerializedName("title")
    val title : String,
    @SerializedName("description")
    val desc : String,
    @SerializedName("image_content")
    val imageUrl : String,
    @SerializedName("date")
    val date : String
)
