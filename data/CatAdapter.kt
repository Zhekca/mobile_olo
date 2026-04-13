package com.example.glitchstore.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.glitchstore.databinding.CatsItemBinding

class CatAdapter(
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<CatAdapter.ViewHolder>() {

    private val items = mutableListOf<CatEntity>()

    fun setData(list: List<CatEntity>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: CatsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CatsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.txtcatlist.text = item.name

        holder.itemView.setOnClickListener {
            onClick(item.id)
        }
    }
}