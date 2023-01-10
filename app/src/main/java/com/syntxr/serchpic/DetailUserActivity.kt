package com.syntxr.serchpic

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.faltenreich.skeletonlayout.applySkeleton
import com.faltenreich.skeletonlayout.createSkeleton
import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.`object`.SharedPref
import com.syntxr.serchpic.adapter.ItemExploreAdapter
import com.syntxr.serchpic.data.Bookmark
import com.syntxr.serchpic.data.LocalDatabase
import com.syntxr.serchpic.databinding.ActivityDetailUserBinding
import com.syntxr.serchpic.model.favorite.Favorite
import com.syntxr.serchpic.model.follow.Follow
import kotlinx.coroutines.launch

class DetailUserActivity : AppCompatActivity(R.layout.activity_detail_user) {

    private val binding: ActivityDetailUserBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (CheckInternet().checkInternet(this)) {

            getUserPost()
            fetchData()

        } else {
            Toast.makeText(this, "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT).show()
        }

        binding.swipeDetailUser.setOnRefreshListener {
            if (CheckInternet().checkInternet(this)) {

                getUserPost()
                fetchData()

            } else {
                Toast.makeText(this, "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBackUserDetail.setOnClickListener {
            onBackPressed()
        }


    }

    @SuppressLint("ResourceAsColor")
    fun getUserPost() {

        val pref = getSharedPreferences(SharedPref.pref, MODE_PRIVATE)
        val id = intent.getIntExtra("userId", 0)
        val api = Api.create()
        val userId = pref.getInt(SharedPref.userId, 0)

//        if (id == userId){
//            binding.btnFollow.visibility = View.GONE
//        }

        lifecycleScope.launch {
            try {


                val shimmerItem = binding.rvDetailUser.applySkeleton(R.layout.item_explore)

                shimmerItem.showSkeleton()

                val rensponsePost = api.getUserPost("eq.$id", "*")

                shimmerItem.showOriginal()

                binding.swipeDetailUser.isRefreshing = false

                val adapter = ItemExploreAdapter(rensponsePost, lifecycleScope)
                val layoutManager = GridLayoutManager(this@DetailUserActivity, 2)
                binding.rvDetailUser.adapter = adapter
                binding.rvDetailUser.layoutManager = layoutManager

                adapter.onClick = {
                    val intent = Intent(this@DetailUserActivity, DetailActivity::class.java)
                    intent.putExtra("id", it.id)
                    intent.putExtra("userId", it.userId)
                    startActivity(intent)
                }

                adapter.onSaveClick = {
                    val favorite = Favorite(
                        postId = it.id,
                        userId = userId
                    )
                    lifecycleScope.launch {
                        val isFavorite = api.isFav("eq.$userId", "eq.${it.id}")
                        if (isFavorite.isNotEmpty()) {
                            api.unfavorite("eq.${isFavorite.first().id}")
                            Toast.makeText(
                                this@DetailUserActivity,
                                "favorite telah dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }

                        val database = LocalDatabase.getInstance(this@DetailUserActivity)
                        val bookmark = Bookmark(
                            id = it.id,
                            authorId = it.userId,
                            userId = userId,
                            image = it.imageContent,
                            title = it.title,
                        )
                        database.bookmarkDao().insertBookmark(bookmark)

                        api.favorite(favorite)
                        Toast.makeText(
                            this@DetailUserActivity,
                            "favorite telah ditambahkan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@DetailUserActivity,
                    "kesalahan ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun fetchData() {
        val api = Api.create()
        val pref = getSharedPreferences(SharedPref.pref, MODE_PRIVATE)
        val id = intent.getIntExtra("userId", 0)
        val userId = pref.getInt(SharedPref.userId, 0)
        lifecycleScope.launch {

            val shimmerImg = binding.imgProfileDetail.createSkeleton()
            val shimmerName = binding.tvProfileNameDetail.createSkeleton()
            val shimmerEmail = binding.tvProfileEmailDetail.createSkeleton()
            val shimmerFollow = binding.tvCountFollowOther.createSkeleton()
            val shimmerPost = binding.tvCountPostOther.createSkeleton()

            shimmerFollow.showSkeleton()
            shimmerEmail.showSkeleton()
            shimmerImg.showSkeleton()
            shimmerName.showSkeleton()
            shimmerPost.showSkeleton()

            binding.swipeDetailUser.isRefreshing = false

            val rensponseUser = api.getUserData("eq.$id", "*")
            val isFollow = api.isFollow("eq.$id", "eq.$userId")
            val getFollower = api.getFollower("eq.$id")
            val rensponsePost = api.getUserPost("eq.$id", "*")

            shimmerEmail.showOriginal()
            shimmerImg.showOriginal()
            shimmerName.showOriginal()
            shimmerPost.showOriginal()
            shimmerFollow.showOriginal()

            binding.swipeDetailUser.isRefreshing = false

            val followed = getFollower.filter {
                it.userId == userId
            }

            binding.tvCountPostOther.text = rensponsePost.count().toString()
            binding.tvCountFollowOther.text = getFollower.count().toString()

            if (followed.size == 1) {
                binding.btnFollow.text = "Batal Mengikuti"
                binding.btnFollow.setBackgroundColor(R.color.btn_logout)
            } else {
                binding.btnFollow.text = "Ikuti"
            }


            binding.btnFollow.setOnClickListener {
                val follow = Follow(
                    userId = id,
                    followersId = userId
                )
                lifecycleScope.launch {
//                    val isFollows = api.isFollow("eq.$id","eq.$userId")
                    if (followed.size == 1) {
                        api.unfollow("eq.${isFollow.first().id}")
                        Toast.makeText(
                            this@DetailUserActivity,
                            "anda tidak lagi mengikuti pengguna ini",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    api.follow(follow)
                    Toast.makeText(
                        this@DetailUserActivity,
                        "anda mengikuti pengguna ini",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


            if (rensponsePost.isEmpty()) {
                binding.tvIsUserPost.visibility = View.VISIBLE
                binding.rvDetailUser.visibility = View.GONE
                return@launch
            }

            val user = rensponseUser.first()
            val post = rensponsePost.first()

            binding.tvProfileNameDetail.text = user.username
            binding.tvProfileEmailDetail.text = user.email



            if (user.image.isNullOrEmpty()) {
                binding.imgProfileDetail.setBackgroundColor(R.color.btn_search)
                return@launch
            }

            binding.imgProfileDetail.load(user.image)

            binding.tvCountFollowOther.text = getFollower.count().toString()

        }
    }

    override fun onStart() {
        super.onStart()
        val pref = getSharedPreferences(SharedPref.pref, MODE_PRIVATE)
        val id = intent.getIntExtra("userId", 0)
        val api = Api.create()
        val userId = pref.getInt(SharedPref.userId, 0)

        if (id == userId) {
            binding.btnFollow.visibility = View.GONE
        }
    }


}