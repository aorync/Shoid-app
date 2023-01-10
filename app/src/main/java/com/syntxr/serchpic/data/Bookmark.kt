package com.syntxr.serchpic.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark")
data class Bookmark(
    @PrimaryKey
    val id : Int? = 0,
    val authorId : Int? = 0,
    val postId : Int? = 0,
    val userId : Int? = 0,
    val image : String? = "",
    val title : String? = ""
)
