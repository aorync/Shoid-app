package com.syntxr.serchpic

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
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
import com.syntxr.serchpic.databinding.ActivityDetailBinding
import com.syntxr.serchpic.model.favorite.Favorite
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity(R.layout.activity_detail) {

    private val binding: ActivityDetailBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)
        val id = pref.getInt(SharedPref.userId, 0)

        if (CheckInternet().checkInternet(this)) {
            getPostDetail()

            getOtherPost()


        } else {
            Toast.makeText(this, "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT).show()
        }

        binding.swipeDetail.setOnRefreshListener {
            if (CheckInternet().checkInternet(this)) {
                getPostDetail()

                getOtherPost()


            } else {
                Toast.makeText(this, "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT).show()
            }
        }


//        val postId = intent.getIntExtra("id", 0)
//        val userId = intent.getIntExtra("userId", 0)
//        val postImg = intent.getStringExtra("image")
//        val postTitle = intent.getStringExtra("title")
//        val postDesc = intent.getStringExtra("desc")


        binding.btnBackDetail.setOnClickListener {
            onBackPressed()
        }

//        lifecycleScope.launch {
//            try {
//
//            } catch (e: Exception) {
//                Toast.makeText(this@DetailActivity, "kesalahan ${e.message}", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
    }

    @SuppressLint("ResourceAsColor")
    private fun getPostDetail() {
        val pref = getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)
        val id = pref.getInt(SharedPref.userId, 0)
        val postId = intent.getIntExtra("id", 0)
        val userId = intent.getIntExtra("userId", 0)
        val postImg = intent.getStringExtra("image")
        val postTitle = intent.getStringExtra("title")
        val postDesc = intent.getStringExtra("desc")
        val postDate = intent.getStringExtra("date")
        val api = Api.create()

        lifecycleScope.launch {

            val shimmerImgAuthor = binding.imgAuthorDetail.createSkeleton()
            val shimmerDate = binding.tvDateDetail.createSkeleton()
            val shimmerDesc = binding.tvDescDetail.createSkeleton()
            val shimmerTitle = binding.tvTitleDetail.createSkeleton()
            val shimmerFav = binding.btnFavDetail.createSkeleton()
//            val shimmerEdit = binding.btnPatchDetail.createSkeleton()

            shimmerDate.showSkeleton()
            shimmerDesc.showSkeleton()
            shimmerFav.showSkeleton()
            shimmerTitle.showSkeleton()
            shimmerImgAuthor.showSkeleton()


            try {
                val rensponsePostDetail = api.getPostDetail("eq.$postId", "*")
                val rensponseUser = api.getUserData("eq.$userId", "*")
                val isFavorite = api.isFav("eq.$userId", "eq.$postId")
                val getFavorite = api.getFav("eq.$postId")
                val getComments = api.getComments("eq.$postId", "*")

                val postDetail = rensponsePostDetail.first()

                binding.btnShowComments.text = "(${getComments.count()}) Lihat Komentar"
                binding.tvLikePost.text = "${getFavorite.count()} Suka"

                shimmerDate.showOriginal()
                shimmerDesc.showOriginal()
                shimmerFav.showOriginal()
                shimmerTitle.showOriginal()
                shimmerImgAuthor.showOriginal()

                binding.swipeDetail.isRefreshing = false


                if (rensponsePostDetail.isEmpty()) {
                    Toast.makeText(
                        this@DetailActivity,
                        "gagal mengambil detail unggahan",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                if (rensponseUser.isEmpty()) {
                    Toast.makeText(
                        this@DetailActivity,
                        "gagal mengambil data author",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

//                val postDetail = rensponsePostDetail.first()
                val userDetail = rensponseUser.first()



                binding.imgAuthorDetail.setOnClickListener {
                    val intent = Intent(this@DetailActivity, DetailUserActivity::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                }

                binding.tvAuthorNameDetail.setOnClickListener {
                    val intent = Intent(this@DetailActivity, DetailUserActivity::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                }

                binding.btnPatchDetail.setOnClickListener {
                    val intent = Intent(this@DetailActivity, EditPostActivity::class.java)
                    intent.putExtra("postId", postDetail.id)
                    intent.putExtra("title", postDetail.title)
                    intent.putExtra("desc", postDetail.description)
                    intent.putExtra("image", postDetail.imageContent)
                    startActivity(intent)
                }



                binding.btnShowComments.setOnClickListener {
                    val comment = BottomSheetCommentsFragment().apply {
                        arguments = bundleOf(
                            "postId" to postDetail.id
                        )
                    }
                    comment.show(supportFragmentManager, "")
                }


                binding.imgDetail.load(postDetail.imageContent)
                binding.tvTitleDetail.text = postDetail.title
                binding.tvDateDetail.text = postDetail.date
                binding.tvAuthorNameDetail.text = userDetail.username


                if (userDetail.image.isNullOrEmpty()) {
                    binding.imgAuthorDetail.setBackgroundColor(R.color.btn_search)
                } else {
                    binding.imgAuthorDetail.load(userDetail.image)
                }


                if (postDetail.description.isNullOrEmpty()) {
                    binding.tvDescDetail.text = ""
                } else {
                    binding.tvDescDetail.text = postDetail.description
                }


            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, "kesalahan ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun getOtherPost() {
        val postId = intent.getIntExtra("id", 0)
        val api = Api.create()
        val pref = getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)
        val userId = pref.getInt(SharedPref.userId, 0)
        lifecycleScope.launch {
            try {

                val shimmer = binding.rvDetail.applySkeleton(R.layout.item_explore)
                shimmer.showSkeleton()

                val rensponse = api.getOtherPost("*", "neq.$postId")
                val isFavorites = api.isFav("eq.$userId", "eq.$postId")

                shimmer.showOriginal()

                binding.swipeDetail.isRefreshing = false

                val adapter = ItemExploreAdapter(rensponse.shuffled(), lifecycleScope)
                val layoutManager = LinearLayoutManager(this@DetailActivity)
                binding.rvDetail.adapter = adapter
                binding.rvDetail.layoutManager = layoutManager

                val post = rensponse.first()

                val rensponsePostDetail = api.getPostDetail("eq.$postId", "*")
                val postDetail = rensponsePostDetail.first()

                if (isFavorites.isNotEmpty()) {
                    binding.btnFavDetail.setImageResource(R.drawable.ic_round_favorite)
                } else {
                    binding.btnFavDetail.setImageResource(R.drawable.ic_round_favorite_border)
                }

                binding.btnFavDetail.setOnClickListener {
                    val favorite = Favorite(
                        postId = postId,
                        userId = userId
                    )
                    lifecycleScope.launch {
                        val isFavorite = api.isFav("eq.$userId", "eq.$postId")
                        if (isFavorite.isNotEmpty()) {
                            binding.btnFavDetail.setImageResource(R.drawable.ic_round_favorite)
                            api.unfavorite("eq.${isFavorite.first().id}")
                            Toast.makeText(
                                this@DetailActivity,
                                "favorite telah dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }

                        val database = LocalDatabase.getInstance(this@DetailActivity)
                        val bookmark = Bookmark(
                            id = postDetail.id,
                            authorId = postDetail.userId,
                            userId = userId,
                            image = postDetail.imageContent,
                            title = postDetail.title
                        )
                        database.bookmarkDao().insertBookmark(bookmark)

                        api.favorite(favorite)
                        Toast.makeText(
                            this@DetailActivity,
                            "favorite telah ditambahkan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                adapter.onClick = {
                    val intent = Intent(this@DetailActivity, DetailActivity::class.java)
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
                                this@DetailActivity,
                                "favorit telah dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }

                        val database = LocalDatabase.getInstance(this@DetailActivity)
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
                            this@DetailActivity,
                            "favorite telah ditambahkan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, "kesalahan ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }


        }
    }

    override fun onStart() {
        super.onStart()

        val pref = getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)
        val id = pref.getInt(SharedPref.userId, 0)
        val userId = intent.getIntExtra("userId", 0)

        if (id != userId) {
            binding.btnPatchDetail.visibility = View.GONE
        }

    }


}