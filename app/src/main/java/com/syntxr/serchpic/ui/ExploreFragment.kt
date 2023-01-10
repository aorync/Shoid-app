package com.syntxr.serchpic.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.faltenreich.skeletonlayout.applySkeleton
import com.syntxr.serchpic.CheckInternet
import com.syntxr.serchpic.DetailActivity
import com.syntxr.serchpic.R
import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.`object`.SharedPref
import com.syntxr.serchpic.adapter.ItemExploreAdapter
import com.syntxr.serchpic.data.Bookmark
import com.syntxr.serchpic.data.LocalDatabase
import com.syntxr.serchpic.databinding.FragmentExploreBinding
import com.syntxr.serchpic.model.favorite.Favorite
import kotlinx.coroutines.launch

class ExploreFragment : Fragment(R.layout.fragment_explore) {

    private val binding: FragmentExploreBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (CheckInternet().checkInternet(requireContext())) {
            getPostSearch()
        } else {
            Toast.makeText(requireContext(), "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getPostSearch() {
        val api = Api.create()

        val query = activity?.intent?.getStringExtra("query")
        val pref = requireContext().getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)
        val userId = pref.getInt(SharedPref.userId,0)

        lifecycleScope.launch {
            try {

                val shimmer = binding.rvExplorer.applySkeleton(R.layout.item_explore)
                shimmer.showSkeleton()

                val rensponse = api.getPostSearch("ilike.%$query%", "*")

                shimmer.showOriginal()

                if (rensponse.isEmpty()) {
                    binding.tvNotPost.visibility = View.VISIBLE
                    binding.rvExplorer.visibility = View.GONE
                    return@launch
                }


                val adapter = ItemExploreAdapter(rensponse,lifecycleScope)
                val layoutManager = GridLayoutManager(requireContext(), 2)
                binding.rvExplorer.adapter = adapter
                binding.rvExplorer.layoutManager = layoutManager


                adapter.onClick = {
                    val intent = Intent(requireContext(), DetailActivity::class.java)
                    intent.putExtra("id", it.id)
                    intent.putExtra("title", it.title)
                    intent.putExtra("desc", it.description)
                    intent.putExtra("image", it.imageContent)
                    intent.putExtra("userId", it.userId)
                    intent.putExtra("date", it.date)
                    startActivity(intent)
                }

                adapter.onSaveClick = {
                    val favorite = Favorite(
                        postId = it.id,
                        userId = userId
                    )
                    lifecycleScope.launch {
                        val isFavorite = api.isFav("eq.$userId","eq.${it.id}")
                        if (isFavorite.isNotEmpty()){
                            api.unfavorite("eq.${isFavorite.first().id}")
                            Toast.makeText(requireContext(), "favorite telah ditambahkan", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(requireContext(), "favorite telah  ditambahkan", Toast.LENGTH_SHORT).show()
                    }
                }



            } catch (e: Exception) {
                Toast.makeText(requireContext(), "kesalahan ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}