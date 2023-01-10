package com.syntxr.serchpic.`object`

import android.content.Context

object SharedPref {
    const val pref = "preferences"
    const val userId = "user_id"
    const val userEmail = "user_email"
    const val userName = "user_name"
    const val userImg = "user_img"
    const val otherUserId = "other_user_id"

    fun Pref(context: Context){
        val pref = context.getSharedPreferences(SharedPref.pref,Context.MODE_PRIVATE)
    }
}