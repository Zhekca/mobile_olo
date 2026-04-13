package com.example.glitchstore.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.glitchstore.databinding.WideCatsBinding
import org.json.JSONArray

class CollectionAdapter(
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    private val items = mutableListOf<CollectionEntity>()

    fun setData(list: List<CollectionEntity>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: WideCatsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return if (items.isEmpty()) 0 else Int.MAX_VALUE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = WideCatsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val realPosition = position % items.size
        val item = items[realPosition]

        val imageUrl = parseImage(item.img)

        Glide.with(holder.itemView)
            .load(imageUrl)
            .into(holder.binding.imgsubwidcats)

        holder.itemView.setOnClickListener {
            onClick(item.id)
        }
    }

    private fun parseImage(json: String?): String {
        return try {
            val array = JSONArray(json)
            array.getString(0)
        } catch (e: Exception) {
            ""
        }
    }
}