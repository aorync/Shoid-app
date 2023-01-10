package com.syntxr.serchpic.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.faltenreich.skeletonlayout.applySkeleton
import com.syntxr.serchpic.CheckInternet
import com.syntxr.serchpic.DetailUserActivity
import com.syntxr.serchpic.R
import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.`object`.SharedPref
import com.syntxr.serchpic.adapter.ItemUserAdapter
import com.syntxr.serchpic.databinding.FragmentUsersBinding
import kotlinx.coroutines.launch

class UsersFragment : Fragment(R.layout.fragment_users) {

    private val binding : FragmentUsersBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (CheckInternet().checkInternet(requireContext())) {
            getUserSearch()
        } else {
            Toast.makeText(requireContext(), "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT).show()
        }



    }

    private fun getUserSearch(){
        val api = Api.create()
        val pref = requireContext().getSharedPreferences(SharedPref.pref,Context.MODE_PRIVATE)
        val query = activity?.intent?.getStringExtra("query")

        lifecycleScope.launch {

            val shimmer = binding.rvUser.applySkeleton(R.layout.item_user)
            shimmer.showSkeleton()

            val rensponse = api.getUserSearch("ilike.%$query%")

            shimmer.showOriginal()
            if (rensponse.isEmpty()){
                binding.tvNotUser.visibility = View.VISIBLE
                binding.rvUser.visibility = View.GONE
                return@launch
            }

            val adapter = ItemUserAdapter(rensponse)
            val layoutManager = LinearLayoutManager(requireContext())
            binding.rvUser.adapter = adapter
            binding.rvUser.layoutManager = layoutManager

            adapter.onClick = {
                val intent = Intent(requireContext(),DetailUserActivity::class.java)
                intent.putExtra("userId",it.id)
                startActivity(intent)
            }
        }
    }
}