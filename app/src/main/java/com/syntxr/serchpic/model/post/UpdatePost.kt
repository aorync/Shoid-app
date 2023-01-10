package com.syntxr.serchpic.model.post

import com.google.gson.annotations.SerializedName

data class UpdatePost(
    @SerializedName("title")
    val title : String,
    @SerializedName("description")
    val desc : String,
    @SerializedName("image_content")
    val imageUrl : String,
)
