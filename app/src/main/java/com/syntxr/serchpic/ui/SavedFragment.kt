package com.syntxr.serchpic.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.syntxr.serchpic.CheckInternet
import com.syntxr.serchpic.DetailActivity
import com.syntxr.serchpic.R
import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.`object`.SharedPref
import com.syntxr.serchpic.adapter.ItemSavedAdapter
import com.syntxr.serchpic.databinding.FragmentSavedBinding
import kotlinx.coroutines.launch

class SavedFragment : Fragment(R.layout.fragment_saved) {

    private val binding: FragmentSavedBinding by viewBinding()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val api = Api.create()
        val pref = requireContext().getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)
        val userId = pref.getInt(SharedPref.userId, 0)

        if (CheckInternet().checkInternet(requireContext())) {
            getSaved()
        } else {
            Toast.makeText(requireContext(), "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT)
                .show()
        }



    }

    fun getSaved() {
        val api = Api.create()
        val pref = requireContext().getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)
        val userId = pref.getInt(SharedPref.userId, 0)

        lifecycleScope.launch {
            try {

                val shimmer = binding.rvSave.applySkeleton(R.layout.item_saved)
                shimmer.showSkeleton()

                val rensponse = api.getFavorite("eq.$userId", "*")

                shimmer.showOriginal()
                if (rensponse.isNullOrEmpty()) {
                    binding.tvIsSaved.visibility = View.VISIBLE
                    binding.rvSave.visibility = View.GONE
                    return@launch
                }
                val post = api.getPostDetail("eq.${rensponse.first().postId}", "*")
                val adapter = ItemSavedAdapter(rensponse, lifecycleScope)
                val layoutManager = GridLayoutManager(requireContext(), 2)
                binding.rvSave.adapter = adapter
                binding.rvSave.layoutManager = layoutManager

                adapter.onClick = {
                    val intent = Intent(requireContext(), DetailActivity::class.java)
                    intent.putExtra("id", rensponse.first().postId)
                    intent.putExtra("userId", post.first().userId)
                    startActivity(intent)
                }

                adapter.onLongClick = {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Hapus")
                        .setMessage("Anda ingin menghapus favorite ini ?")
                        .setPositiveButton("Ya") { dialog, which ->
                            lifecycleScope.launch {
                                val isFavorite = api.isFav("eq.$userId", "eq.${it.postId}")
                                if (isFavorite.isNotEmpty()) {
                                    api.unfavorite("eq.${it.id}")
                                    Toast.makeText(
                                        requireContext(),
                                        "favorit telah dihapus",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    getSaved()
                                    return@launch
                                }
                            }
                        }
                        .setNegativeButton("Batal") { dialog, which ->
                            dialog.dismiss()
                        }.show()
                }


            } catch (e: Exception) {
                Toast.makeText(requireContext(), "kesalahan ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}