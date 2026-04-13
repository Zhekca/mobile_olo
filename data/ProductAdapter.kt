package com.example.glitchstore.data

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.glitchstore.R
import com.example.glitchstore.databinding.FragmentHomeBinding
import com.example.glitchstore.databinding.ItemProductHomeBinding
import com.example.glitchstore.homeFragment


enum class SortType {
    PRICE, RATING, NEW
}
enum class SortOrder {
    ASC, DESC
}
class ProductAdapter(
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val items = mutableListOf<ProductWithImage>()

    fun setData(newItems: List<ProductWithImage>) {

        val diff = androidx.recyclerview.widget.DiffUtil.calculateDiff(
            object : androidx.recyclerview.widget.DiffUtil.Callback() {

                override fun getOldListSize() = items.size
                override fun getNewListSize() = newItems.size

                override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
                    return items[oldPos].id == newItems[newPos].id
                }

                override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
                    return items[oldPos] == newItems[newPos]
                }
            }
        )

        items.clear()
        items.addAll(newItems)

        diff.dispatchUpdatesTo(this)
    }

    inner class ProductViewHolder(
        private val binding: ItemProductHomeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductWithImage) {

            binding.prodTitle.text = item.name
            binding.prodMoney.text = "${item.price.toInt()} ₽"

            val imageUrl = parseImage(item.mainImage)

            binding.imgCont.load(imageUrl) {
                crossfade(true)
                placeholder(R.drawable.img_loading)
                error(R.drawable.img_no_wifi)
            }

            binding.root.setOnClickListener {
                onItemClick(item.id)
            }
        }

        private fun parseImage(json: String?): String? {
            if (json.isNullOrEmpty()) return null

            return try {
                val cleaned = json
                    .replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")

                cleaned.split(",").firstOrNull()
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductHomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}