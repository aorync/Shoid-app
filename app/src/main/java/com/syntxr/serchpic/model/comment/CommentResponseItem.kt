package com.syntxr.serchpic.model.comment


import com.google.gson.annotations.SerializedName

data class CommentResponseItem(
    @SerializedName("comments")
    val comments: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("post_id")
    val postId: Int,
    @SerializedName("user_id")
    val userId: Int
)