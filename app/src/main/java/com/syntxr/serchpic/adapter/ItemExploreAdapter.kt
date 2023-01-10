package com.syntxr.serchpic.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.syntxr.serchpic.R
import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.`object`.SharedPref
import com.syntxr.serchpic.databinding.ItemExploreBinding
import com.syntxr.serchpic.model.explore.ExploreRensponseItem
import kotlinx.coroutines.launch

class ItemExploreAdapter(val exploreList: List<ExploreRensponseItem>, private val lifecycleCoroutineScope: LifecycleCoroutineScope) :
    Adapter<ItemExploreAdapter.ItemExploreViewHolder>() {

    var onClick: ((ExploreRensponseItem) -> Unit)? = null
    var onSaveClick: ((ExploreRensponseItem) -> Unit)? = null

    class ItemExploreViewHolder(itemView: View) : ViewHolder(itemView) {

        val binding: ItemExploreBinding by viewBinding()
        val api = Api.create()
        val pref = itemView.context.getSharedPreferences(SharedPref.pref,Context.MODE_PRIVATE)
        val userId = pref.getInt(SharedPref.userId,0)

        @SuppressLint("ResourceAsColor")
        fun bindView(
            post: ExploreRensponseItem,
            lifecycleCoroutineScope: LifecycleCoroutineScope,
        ) {

            lifecycleCoroutineScope.launch {
                val isFavorite = api.isFav("eq.$userId","eq.${post.id}",)
                if (isFavorite.isNotEmpty()){
                    binding.btnSave.setImageResource(R.drawable.ic_round_favorite)
                    return@launch
                }
            }

            if (post.imageContent.isNullOrEmpty()) {
                binding.imgExplore.setBackgroundColor(R.color.btn_search)
            } else {
                binding.imgExplore.load(post.imageContent)
            }



            binding.tvTitleExplore.text = post.title

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemExploreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_explore, parent, false)
        return ItemExploreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemExploreViewHolder, position: Int) {
        val explore = exploreList[position]
        holder.bindView(explore,lifecycleCoroutineScope)

        holder.binding.cardExplore.setOnClickListener {
            onClick?.invoke(explore)
        }

        holder.binding.btnSave.setOnClickListener {
            onSaveClick?.invoke(explore)
        }
    }

    override fun getItemCount(): Int {
        return exploreList.size
    }
}