package com.example.glitchstore.data


import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.glitchstore.R
import com.example.glitchstore.databinding.ItemCartBinding


class CartAdapter : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private val items = mutableListOf<CartItemWithProduct>()
    private val selectedItems = mutableSetOf<Int>()

    fun setData(list: List<CartItemWithProduct>) {
        items.clear()
        items.addAll(list)

        // чистим выбор если товары исчезли
        selectedItems.retainAll(items.map { it.productId })

        notifyDataSetChanged()
    }

    fun getSelectedItems(): Set<Int> = selectedItems

    inner class ViewHolder(val binding: ItemCartBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items[position]

        with(holder.binding) {

            prodTitle.text = item.name
            prodSubTxt.text = item.description
            prodMoney.text = "${item.price.toInt()} ₽"
            prodCount.text = "x${item.count}"

            val imageUrl = parseImage(item.mainImage)

            imgCont.load(imageUrl) {
                placeholder(R.drawable.img_loading)
                error(R.drawable.img_no_wifi)
            }

            // ✅ состояние выбора
            val isSelected = selectedItems.contains(item.productId)
            updateSelectionUI(this, isSelected)

            // ✅ клик по кнопке
            btnSelect.setOnClickListener {

                val currentlySelected = selectedItems.contains(item.productId)

                if (currentlySelected) {
                    selectedItems.remove(item.productId)
                } else {
                    selectedItems.add(item.productId)
                }

                notifyItemChanged(holder.adapterPosition)
            }
        }
    }

    // 🎨 цвет кнопки
    private fun updateSelectionUI(binding: ItemCartBinding, isSelected: Boolean) {

        val color = if (isSelected)
            ContextCompat.getColor(binding.root.context, R.color.btn_minus)
        else
            ContextCompat.getColor(binding.root.context, R.color.sub_layout)

        binding.btnSelect.foregroundTintList = ColorStateList.valueOf(color)
    }

    // 🖼 парс JSON картинки
    private fun parseImage(json: String?): String? {
        if (json.isNullOrEmpty()) return null

        return json
            .replace("[", "")
            .replace("]", "")
            .replace("\"", "")
            .split(",")
            .firstOrNull()
    }
}