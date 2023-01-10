package com.syntxr.serchpic.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.syntxr.serchpic.R
import com.syntxr.serchpic.databinding.ItemPostBinding
import com.syntxr.serchpic.model.post.PostRensponseItem

class ItemPostAdapter(val postList : List<PostRensponseItem>) : Adapter<ItemPostAdapter.ItemPostViewHolder>() {

    var onClick : ((PostRensponseItem) -> Unit)? = null
    var onMoreClick : ((PostRensponseItem) -> Unit)? = null

    class ItemPostViewHolder(itemView: View) : ViewHolder(itemView) {
        val binding : ItemPostBinding by viewBinding()

        @SuppressLint("ResourceAsColor")
        fun bindView(
            post: PostRensponseItem,
            onClick: ((PostRensponseItem) -> Unit)?,
            onMoreClick: ((PostRensponseItem) -> Unit)?,){

            binding.cardPost.setOnClickListener {
                onClick?.invoke(post)
            }

            binding.btnMore.setOnClickListener {
                onMoreClick?.invoke(post)
            }

            if (post.imageContent.isNullOrEmpty()){
                binding.imgPost.setBackgroundColor(R.color.btn_search)
            }else{
                binding.imgPost.load(post.imageContent)
            }

            binding.tvTitlePost.text = post.title

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false)
        return ItemPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemPostViewHolder, position: Int) {
        val post = postList[position]
        holder.bindView(post,onClick,onMoreClick)
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}