package com.syntxr.serchpic.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.faltenreich.skeletonlayout.applySkeleton
import com.syntxr.serchpic.BottomSheetFragment
import com.syntxr.serchpic.CheckInternet
import com.syntxr.serchpic.DetailActivity
import com.syntxr.serchpic.R
import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.`object`.SharedPref
import com.syntxr.serchpic.adapter.ItemPostAdapter
import com.syntxr.serchpic.databinding.FragmentPostBinding
import kotlinx.coroutines.launch

class PostFragment : Fragment(R.layout.fragment_post) {

    private val binding: FragmentPostBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (CheckInternet().checkInternet(requireContext())) {
            getPost()
        } else {
            Toast.makeText(requireContext(), "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT)
                .show()
        }


    }

    fun getPost() {
        val pref = requireContext().getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)
        val id = pref.getInt(SharedPref.userId, 0)
        val api = Api.create()
        val userId = pref.getInt(SharedPref.userId, 0)

        lifecycleScope.launch {

            val shimmer = binding.rvPost.applySkeleton(R.layout.item_post)
            shimmer.showSkeleton()

            val rensponse = api.getPost("eq.$id", "*")

            shimmer.showOriginal()
            if (rensponse.isNotEmpty()) {
                binding.tvIsPost.visibility = View.GONE
                binding.rvPost.visibility = View.VISIBLE

                val adapter = ItemPostAdapter(rensponse)
                val layoutManager = GridLayoutManager(requireContext(), 2)
                binding.rvPost.adapter = adapter
                binding.rvPost.layoutManager = layoutManager

                adapter.onClick = {
                    val intent = Intent(requireContext(), DetailActivity::class.java)
                    intent.putExtra("id", it.id)
                    intent.putExtra("userId", it.userId)
                    startActivity(intent)
                }

                adapter.onMoreClick = {
                    val bottomSheet = BottomSheetFragment().apply {
                        arguments = bundleOf(
                            "postId" to it.id,
                            "userId" to userId,
                            "title" to it.title,
                            "desc" to it.description,
                            "image" to it.imageContent,
                        )
                    }
                    bottomSheet.show(requireFragmentManager(), "")
                }
                return@launch
            }
        }
    }


}