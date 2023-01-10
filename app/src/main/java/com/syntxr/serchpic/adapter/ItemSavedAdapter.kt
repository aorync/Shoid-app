package com.syntxr.serchpic.adapter

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
import com.syntxr.serchpic.databinding.ItemSavedBinding
import com.syntxr.serchpic.model.favorite.FavoriteRensponseItem
import kotlinx.coroutines.launch

class ItemSavedAdapter(val savedList : List<FavoriteRensponseItem>, private val lifecycleCoroutineScope: LifecycleCoroutineScope) :
    Adapter<ItemSavedAdapter.ItemSavedViewHolder>() {

    var onClick : ((FavoriteRensponseItem) -> Unit)? = null
    var onLongClick : ((FavoriteRensponseItem) -> Unit)? = null

    class ItemSavedViewHolder(itemView: View) : ViewHolder(itemView) {
        val binding : ItemSavedBinding by viewBinding()
        val api = Api.create()
        fun bindView(
            favorite: FavoriteRensponseItem,
            lifecycleCoroutineScope: LifecycleCoroutineScope,
            onClick: ((FavoriteRensponseItem) -> Unit)?,
            onLongClick: ((FavoriteRensponseItem) -> Unit)?
        ){
            lifecycleCoroutineScope.launch {
                val post = api.getPostDetail("eq.${favorite.postId}","*")
                val user = api.getUserData("eq.${post.first().userId}","*")

                binding.tvTitleSaved.text = post.first().title
                binding.tvAuthorSaved.text = "@${user.first().username}"
                binding.imgSavedPost.load(post.first().imageContent)

                binding.cardSaved.setOnClickListener {
                    onClick?.invoke(favorite)
                }

                binding.cardSaved.setOnLongClickListener {
                    onLongClick?.invoke(favorite)
                    return@setOnLongClickListener true
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSavedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_saved,parent,false)
        return ItemSavedViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemSavedViewHolder, position: Int) {
        val saved = savedList[position]
        holder.bindView(saved,lifecycleCoroutineScope,onClick,onLongClick)
    }

    override fun getItemCount(): Int {
        return savedList.size
    }
}