package com.example.onlineshop.Adapter

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineshop.Activity.ListItemsActivity
import com.example.onlineshop.Model.CategoryModel
import com.example.onlineshop.R
import com.example.onlineshop.databinding.ViewholderCategoryBinding


class CategoryAdapter(val items: List<CategoryModel>):
    RecyclerView.Adapter<CategoryAdapter.Viewholder>() {
    private var selectedPosition = -1
    private var lastSeletedPosition= -1


    inner class Viewholder(val binding: ViewholderCategoryBinding) :RecyclerView.ViewHolder(binding.root) {
        init {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.Viewholder {
        val binding=ViewholderCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.Viewholder, position: Int) {
        val item=items[position]
        holder.binding.titleTxt.text=item.title

        Glide.with(holder.itemView.context)
            .load(item.picUrl)
            .into(holder.binding.pic)

        if(selectedPosition==position){
            holder.binding.pic.setBackgroundResource(0)
            holder.binding.mainLayout.setBackgroundResource(R.drawable.green_button_bg)
            ImageViewCompat.setImageTintList(
                holder.binding.pic,
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )
            )
            holder.binding.titleTxt.visibility= View.VISIBLE
            holder.binding.titleTxt.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )
        } else {
            holder.binding.pic.setBackgroundResource(R.drawable.grey_bg)
            holder.binding.mainLayout.setBackgroundResource(0)
            ImageViewCompat.setImageTintList(
                holder.binding.pic,
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.black
                    )
                )
            )
            holder.binding.titleTxt.visibility= View.GONE
            holder.binding.titleTxt.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.black
                )
            )
            holder.binding.root.setOnClickListener{
                val position=position
                if(position!=RecyclerView.NO_POSITION){
                    lastSeletedPosition=selectedPosition
                    selectedPosition=position
                    notifyItemChanged(lastSeletedPosition)
                    notifyItemChanged(selectedPosition)
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent=Intent(holder.itemView.context,ListItemsActivity::class.java).apply {
                        putExtra("id",item.id.toString())
                        putExtra("title",item.title)
                    }
                    ContextCompat.startActivity(holder.itemView.context,intent,null)
                },1000)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}