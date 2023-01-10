package com.syntxr.serchpic.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.syntxr.serchpic.R
import com.syntxr.serchpic.data.Bookmark
import com.syntxr.serchpic.databinding.ItemSaveOfflineBinding

class ItemSavedOfflineAdapter(val savedOfflineList : List<Bookmark>) : Adapter<ItemSavedOfflineAdapter.ItemSavedOfflineViewHolder>() {
    
    var onClick : ((Bookmark) -> Unit)? = null
    var onLongClick : ((Bookmark) -> Unit)? = null
    
    class ItemSavedOfflineViewHolder(itemView: View) : ViewHolder(itemView) {
        val binding : ItemSaveOfflineBinding by viewBinding()
        
        fun bindView(
            bookmark: Bookmark,
            onClick: ((Bookmark) -> Unit)?,
            onLongClick: ((Bookmark) -> Unit)?
        ){

            binding.tvTitleSavedOffline.text = bookmark.title
            binding.imgSavedPostOffline.load(bookmark.image)

            binding.cardSavedOffline.setOnClickListener {
                onClick?.invoke(bookmark)
            }

            binding.cardSavedOffline.setOnLongClickListener {
                onLongClick?.invoke(bookmark)
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSavedOfflineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_save_offline,parent,false)
        return ItemSavedOfflineViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemSavedOfflineViewHolder, position: Int) {
        val savedOffline = savedOfflineList[position]
        holder.bindView(savedOffline,onClick,onLongClick)
    }

    override fun getItemCount(): Int {
        return savedOfflineList.size
    }
}