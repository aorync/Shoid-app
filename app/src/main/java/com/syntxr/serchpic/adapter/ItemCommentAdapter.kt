package com.syntxr.serchpic.adapter

import android.annotation.SuppressLint
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
import com.syntxr.serchpic.databinding.ItemCommentsBinding
import com.syntxr.serchpic.model.comment.CommentResponseItem
import kotlinx.coroutines.launch

class ItemCommentAdapter(val comments : List<CommentResponseItem>, private val lifecycleCoroutineScope: LifecycleCoroutineScope) : Adapter<ItemCommentAdapter.ItemCommentsViewHolder>() {

    class ItemCommentsViewHolder(itemView: View) : ViewHolder(itemView) {
        val binding : ItemCommentsBinding by viewBinding()
        val api = Api.create()
        @SuppressLint("ResourceAsColor")
        fun bindView (
            comment: CommentResponseItem,
            lifecycleCoroutineScope: LifecycleCoroutineScope
        ){
            binding.tvUserComments.text = comment.comments

            lifecycleCoroutineScope.launch {
                val getUserData = api.getUserData("eq.${comment.userId}","*")
                val user = getUserData.first()
                binding.tvUsername.text = user.username
                if (user.image.isNullOrEmpty()){
                    binding.imgComment.setBackgroundColor(R.color.btn_search)
                    return@launch
                }

                binding.imgComment.load(user.image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCommentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comments,parent,false)
        return ItemCommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemCommentsViewHolder, position: Int) {
        val comment  = comments[position]
        holder.bindView(comment,lifecycleCoroutineScope)
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}