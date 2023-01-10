package com.syntxr.serchpic.model.follow

import com.google.gson.annotations.SerializedName

data class Follow(
    @SerializedName("followers_id")
    val followersId: Int,
    @SerializedName("user_id")
    val userId: Int
)
