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
import com.syntxr.serchpic.databinding.ItemUserBinding
import com.syntxr.serchpic.model.register.UserRensponseItem

class ItemUserAdapter(val userList: List<UserRensponseItem>) : Adapter<ItemUserAdapter.ItemUserViewHolder>() {

    var onClick : ((UserRensponseItem) -> Unit)? = null

    class ItemUserViewHolder(itemView: View) : ViewHolder(itemView) {
        val binding : ItemUserBinding by viewBinding()

        @SuppressLint("ResourceAsColor")
        fun bindView(user: UserRensponseItem, onClick: ((UserRensponseItem) -> Unit)?){

            if (user.image.isNullOrEmpty()){
                binding.imgUser.setBackgroundColor(R.color.btn_search)
            }else{
                binding.imgUser.load(user.image)
            }
            binding.tvUserName.text = user.username
            binding.tvUserEmail.text = user.email

            binding.cardUser.setOnClickListener {
                onClick?.invoke(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemUserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user,parent,false)
        return ItemUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemUserViewHolder, position: Int) {
        val user = userList[position]
        holder.bindView(user,onClick)
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}