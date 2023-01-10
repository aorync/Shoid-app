package com.syntxr.serchpic

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.`object`.SharedPref
import com.syntxr.serchpic.adapter.ItemCommentAdapter
import com.syntxr.serchpic.databinding.FragmentBottomSheetCommentsBinding
import com.syntxr.serchpic.model.comment.Comment
import kotlinx.coroutines.launch

class BottomSheetCommentsFragment : BottomSheetDialogFragment() {

    private val binding: FragmentBottomSheetCommentsBinding by viewBinding()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_bottom_sheet_comments, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = requireContext().getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)
        val userId = pref.getInt(SharedPref.userId, 0)
        val postId = arguments?.getInt("postId")
        val api = Api.create()

        if (CheckInternet().checkInternet(requireContext())) {
            getComment()

            binding.btnSend.setOnClickListener {
                val textComment = binding.inputComment.text.toString()
                val comment = Comment(
                    postId = postId!!,
                    userId = userId,
                    comments = textComment
                )

                if (textComment.isNotEmpty()) {
                    lifecycleScope.launch {
                        try {
                            api.comments(comment)
                            binding.inputComment.text = null
                            getComment()
                            binding.tvIsComment.visibility = View.GONE

                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                "kesalahan ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                    return@setOnClickListener
                }
            }
        } else {
            Toast.makeText(requireContext(), "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT).show()
        }




    }

    private fun getComment() {
        val postId = arguments?.getInt("postId")
        val api = Api.create()
        lifecycleScope.launch {
            try {
                val shimmer = binding.rvComment.applySkeleton(R.layout.item_comments)
                shimmer.showSkeleton()

                val getComments = api.getComments("eq.$postId", "*")

                shimmer.showOriginal()

                binding.swipeComment.isRefreshing = false
                if (getComments.isEmpty()) {
                    binding.tvIsComment.visibility = View.VISIBLE
                    return@launch
                }

                val adapter = ItemCommentAdapter(getComments, lifecycleScope)
                val layoutManager = LinearLayoutManager(requireContext())
                binding.rvComment.adapter = adapter
                binding.rvComment.layoutManager = layoutManager

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "kesalahan ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }


        }
    }

}