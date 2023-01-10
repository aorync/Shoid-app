package com.syntxr.serchpic.model.post


import com.google.gson.annotations.SerializedName

data class PostRensponseItem(
    @SerializedName("date")
    val date: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image_content")
    val imageContent: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("user_id")
    val userId: Int?
)