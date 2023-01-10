package com.syntxr.serchpic

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.syntxr.serchpic.databinding.FragmentSearchBinding
import com.syntxr.serchpic.ui.ExploreFragment
import com.syntxr.serchpic.ui.UsersFragment

class SearchActivity : AppCompatActivity(R.layout.fragment_search) {

    private val binding : FragmentSearchBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val query = intent.getStringExtra("query")
        binding.actionSearch.setText(query)

        val bundle = bundleOf("query" to query)

        ExploreFragment().arguments = bundle
        UsersFragment().arguments = bundle


        val fragmentList = listOf(ExploreFragment(), UsersFragment())
        val adapter = ViewPagerAdapter(this,fragmentList)
        binding.viewPagerHome.adapter = adapter
        val title = listOf("Jelajahi","Pengguna")
        TabLayoutMediator(binding.tabLayoutHome,binding.viewPagerHome){tab,pos ->
            tab.text = title[pos]
        }.attach()

        binding.btnBackSearch.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        binding.actionSearch.setOnClickListener {
            val intent = Intent(this,ActionSearchActivity::class.java)
            intent.putExtra("query", query)
            startActivity(intent)
            finish()
            overridePendingTransition(com.google.android.material.R.anim.abc_fade_in,
                com.google.android.material.R.anim.abc_fade_out)
        }
    }


}