package com.syntxr.serchpic.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.faltenreich.skeletonlayout.applySkeleton
import com.faltenreich.skeletonlayout.createSkeleton
import com.syntxr.serchpic.ActionSearchActivity
import com.syntxr.serchpic.CheckInternet
import com.syntxr.serchpic.DetailActivity
import com.syntxr.serchpic.R
import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.`object`.SharedPref
import com.syntxr.serchpic.adapter.ItemExploreAdapter
import com.syntxr.serchpic.data.Bookmark
import com.syntxr.serchpic.data.LocalDatabase
import com.syntxr.serchpic.databinding.FragmentDashboardBinding
import com.syntxr.serchpic.model.favorite.Favorite
import kotlinx.coroutines.launch

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val binding: FragmentDashboardBinding by viewBinding()

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (CheckInternet().checkInternet(requireContext())) {
            fetchData()
            getUserData()


        } else {
            Toast.makeText(requireContext(), "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT).show()
        }

        binding.swipeHome.setOnRefreshListener {
            if (CheckInternet().checkInternet(requireContext())) {
                fetchData()
                getUserData()


            } else {
                Toast.makeText(requireContext(), "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT).show()
            }
        }




        val pref = requireContext().getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)
//        val name = pref.getString(SharedPref.userName,"")
//        val img = pref.getString(SharedPref.userImg,"")
//
//        when(img?.isNotEmpty()){
//            true -> binding.imgUserHome.load(img)
//            else -> binding.imgUserHome.setBackgroundColor(R.color.purple_200)
//        }
//
//        binding.tvUserNameHome.text = name

        binding.toSearch.setOnClickListener {
            startActivity(Intent(requireContext(),ActionSearchActivity::class.java))
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun fetchData() {
        val api = Api.create()
        val pref = requireContext().getSharedPreferences(SharedPref.pref,Context.MODE_PRIVATE)
        val userId = pref.getInt(SharedPref.userId,0)

        lifecycleScope.launch {
            try {

                val shimmer = binding.rvHome.applySkeleton(R.layout.item_explore)
                shimmer.showSkeleton()

                val rensponseExplore = api.homeExplore("*",)

                shimmer.showOriginal()

                binding.swipeHome.isRefreshing = false

                if(rensponseExplore.isEmpty()){
                    Toast.makeText(requireContext(), "Gagal Memuat data", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val post = rensponseExplore.first()




                val adapter = ItemExploreAdapter(rensponseExplore.shuffled(),lifecycleScope)
                val layoutManager = GridLayoutManager(requireContext(),2)
                binding.rvHome.adapter = adapter
                binding.rvHome.layoutManager = layoutManager


                adapter.onClick = {
                    val intent = Intent(requireContext(), DetailActivity::class.java)
                    intent.putExtra("id",it.id)
                    intent.putExtra("title",it.title)
                    intent.putExtra("desc",it.description)
                    intent.putExtra("image",it.imageContent)
                    intent.putExtra("userId",it.userId)
                    intent.putExtra("date",it.date)
                    startActivity(intent)
                }

                adapter.onSaveClick = {
                    val favorite = Favorite(
                        postId = it.id,
                        userId = userId
                    )
                    lifecycleScope.launch {
                        try {
                            val isFavorite = api.isFav("eq.$userId","eq.${it.id}",)
                            if (isFavorite.isNotEmpty()){
                                api.unfavorite("eq.${isFavorite.first().id}")
                                Toast.makeText(requireContext(), "favorit telah dihapus", Toast.LENGTH_SHORT).show()
                                return@launch
                            }

                            val database = LocalDatabase.getInstance(requireContext())
                            val bookmark = Bookmark(
                                id = it.id,
                                authorId = it.userId,
                                userId = userId,
                                image = it.imageContent,
                                title = it.title,
                            )
                            database.bookmarkDao().insertBookmark(bookmark)
                            api.favorite(favorite)
                            Toast.makeText(requireContext(), "favorit telah ditambahkan", Toast.LENGTH_SHORT).show()
                        }catch (e : Exception){
                            Toast.makeText(requireContext(), "kesalahan ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

//                adapter.onSaveClick  = {
//                }






            }catch (e : Exception){
                Toast.makeText(requireContext(), "kesalahan ${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("WEE!", "${e.toString()} ")
            }

        }

    }

    @SuppressLint("ResourceAsColor")
    private fun getUserData(){
        val pref = requireContext().getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)
        val id = pref.getInt(SharedPref.userId, 0)
        val api = Api.create()


        lifecycleScope.launch {
            try {

                val shimmerImg = binding.imgUserHome.createSkeleton()
                val shimmerTvUsername = binding.tvUserNameHome.createSkeleton()

                shimmerImg.showSkeleton()
                shimmerImg.showSkeleton()

                val rensponseUser = api.getUserData( "eq.$id","*",)

                shimmerImg.showOriginal()
                shimmerTvUsername.showOriginal()

                binding.swipeHome.isRefreshing = false

                binding.tvUserNameHome.text = rensponseUser.first().username

                if (rensponseUser.first().image.isNullOrEmpty()) {
                    binding.imgUserHome.setBackgroundColor(R.color.btn_search)
                    return@launch
                }
                binding.imgUserHome.load(rensponseUser.first().image)
            }catch (e : Exception){
                Toast.makeText(requireContext(), "kesalahan ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


}