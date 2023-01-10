package com.syntxr.serchpic.ui

import android.content.ClipData.Item
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.syntxr.serchpic.DetailActivity
import com.syntxr.serchpic.R
import com.syntxr.serchpic.`object`.SharedPref
import com.syntxr.serchpic.adapter.ItemSavedOfflineAdapter
import com.syntxr.serchpic.data.LocalDatabase
import com.syntxr.serchpic.databinding.FragmentOfflineSavedBinding
import kotlinx.coroutines.launch

class OfflineSavedFragment : Fragment(R.layout.fragment_offline_saved) {

    private val binding: FragmentOfflineSavedBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = requireContext().getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)
        val userid = pref.getInt(SharedPref.userId, 0)
        val database = LocalDatabase.getInstance(requireContext())

        val shimmer = binding.rvSavedOffline.applySkeleton(R.layout.item_save_offline)
        shimmer.showSkeleton()

        val bookmark = database.bookmarkDao().getBookmarkList(userid)

        shimmer.showOriginal()

        lifecycleScope.launch {
            bookmark.collect {
                val adapter = ItemSavedOfflineAdapter(it)
                binding.rvSavedOffline.adapter = adapter
                binding.rvSavedOffline.layoutManager = GridLayoutManager(requireContext(), 2)

                if (adapter.itemCount == 0) {
                    binding.tvIsSavedOffline.visibility = View.VISIBLE
                }

                adapter.onClick = {
                    val intent = Intent(requireContext(), DetailActivity::class.java)
                    intent.putExtra("id", it.id)
                    intent.putExtra("userId", it.userId)
                    startActivity(intent)
                }

                adapter.onLongClick = {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Hapus")
                        .setMessage("apakah anda ingin menghapus favorite ini ?")
                        .setPositiveButton("Ya") { dialog, which ->
                            lifecycleScope.launch {
                                database.bookmarkDao().deleteBookmark(it)
                                Toast.makeText(
                                    requireContext(),
                                    "favorite telah dihapus",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .setNegativeButton("Batal") { dialog, which ->
                            dialog.dismiss()
                        }.show()
                }

            }

        }

    }
}