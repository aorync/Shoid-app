package com.syntxr.serchpic.`object`

import com.syntxr.serchpic.data.SupabaseApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {

    fun create():SupabaseApi{
        return Retrofit.Builder()
            .baseUrl(BASH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApi::class.java)
    }

    private const val BASH_URL = "https://ngrrvubbkhssalzqgfpd.supabase.co/rest/v1/"
    const val PREFER = "return=representation"
    const val APIKEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5ncnJ2dWJia2hzc2FsenFnZnBkIiwicm9sZSI6ImFub24iLCJpYXQiOjE2NjY3NTA0NjEsImV4cCI6MTk4MjMyNjQ2MX0.KHbQrMuD1BVxcZusdhIt7SwCHqV5fJnC4du1qslZg6U"
}