package com.syntxr.serchpic.model.follow


import com.google.gson.annotations.SerializedName

data class FollowResponseItem(
    @SerializedName("followers_id")
    val followersId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int
)