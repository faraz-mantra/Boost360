package dev.patrickgold.florisboard.customization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.model.response.Product
import dev.patrickgold.florisboard.customization.viewholder.ProductViewHolder


class ProductsAdapter : ListAdapter<Product, ProductViewHolder>(ProductItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ProductViewHolder {
        return ProductViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.adapter_item_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }
}

class ProductItemDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem == newItem
}