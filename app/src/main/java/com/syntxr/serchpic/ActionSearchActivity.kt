package com.syntxr.serchpic

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.syntxr.serchpic.databinding.ActivityActionSearchBinding
import com.syntxr.serchpic.ui.ExploreFragment
import com.syntxr.serchpic.ui.UsersFragment

class ActionSearchActivity : AppCompatActivity(R.layout.activity_action_search) {

    private val binding : ActivityActionSearchBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnCloseSearch.setOnClickListener {
            onBackPressed()
        }

        val query = intent.getStringExtra("query")
        binding.inputSearchAction.setText(query)

        binding.inputSearchAction.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){

                val query = v.text

                if (query.isNotEmpty()){
                    val intent = Intent(this, SearchActivity::class.java)
                    ExploreFragment().arguments = bundleOf("query" to query)
                    UsersFragment().arguments = bundleOf("query" to query)
                    intent.putExtra("query", query)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(com.google.android.material.R.anim.abc_fade_in,
                        com.google.android.material.R.anim.abc_fade_out)
                }

            }
            false
        }

        binding.btnSearch.setOnClickListener {
            val query = binding.inputSearchAction.text.toString()

            if (query.isNotEmpty()){
                val intent = Intent(this, SearchActivity::class.java)
                ExploreFragment().arguments = bundleOf("query" to query)
                UsersFragment().arguments = bundleOf("query" to query)
                intent.putExtra("query", query)
                startActivity(intent)
                finish()
                overridePendingTransition(com.google.android.material.R.anim.abc_fade_in,
                    com.google.android.material.R.anim.abc_fade_out
                )
            }
        }


    }

}