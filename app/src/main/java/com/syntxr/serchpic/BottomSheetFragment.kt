package com.syntxr.serchpic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.databinding.BottomSheetFragmentBinding
import com.syntxr.serchpic.model.favorite.Favorite
import kotlinx.coroutines.launch

open class BottomSheetFragment : BottomSheetDialogFragment() {

    private val binding: BottomSheetFragmentBinding by viewBinding()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = arguments?.getInt("postId")
        val userId = arguments?.getInt("userId")
        val image = arguments?.getString("image")
        val title = arguments?.getString("title")
        val desc = arguments?.getString("desc")

        val api = Api.create()

        binding.btnDeletePost.setOnClickListener {

            lifecycleScope.launch {
//                try {
                val rensponse = api.deletePost("eq.$postId")
                val rensponseComments = api.deleteComments("eq.$postId")
                val rensponseFav = api.deletePostFavorite("eq.$postId")
                if (!rensponse.isSuccessful) {
                    return@launch
                }
                dismiss()
//                } catch (e: Exception) {
//                    Toast.makeText(
//                        requireContext(),
//                        "kesalahan ${e.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
            }


        }

        binding.btnPatchPost.setOnClickListener {
            val intent = Intent(requireContext(), EditPostActivity::class.java)
            intent.putExtra("postId", postId)
            intent.putExtra("title", title)
            intent.putExtra("desc", desc)
            intent.putExtra("image", image)
            startActivity(intent)
            dismiss()
        }


    }

}