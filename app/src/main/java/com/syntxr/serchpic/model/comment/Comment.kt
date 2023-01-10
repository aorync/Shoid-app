package com.syntxr.serchpic.model.comment

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("comments")
    val comments: String,
    @SerializedName("post_id")
    val postId: Int,
    @SerializedName("user_id")
    val userId: Int
)
