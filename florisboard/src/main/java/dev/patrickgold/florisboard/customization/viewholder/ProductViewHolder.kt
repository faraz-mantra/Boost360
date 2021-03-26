package dev.patrickgold.florisboard.customization.viewholder

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.Product

class ProductViewHolder(itemView: View, private val listener: OnItemClickListener)
    : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    init {
        val productShare: Button = itemView.findViewById(R.id.buttonCopy)
        productShare.setOnClickListener(this)
    }

    private val productImage: ImageView = itemView.findViewById(R.id.imageView)
    private val productName: TextView = itemView.findViewById(R.id.tv_product_name)
    private val productPrice: TextView = itemView.findViewById(R.id.tv_product_price)
    private val productDiscount: TextView = itemView.findViewById(R.id.tv_discount)
    private val productDescription: TextView = itemView.findViewById(R.id.tv_description)

    fun bindTo(product: Product) {
        // bind views with data
        Glide.with(productImage).load(product.imageUri)
                .placeholder(R.drawable.default_product_image).into(productImage)

        productName.text = product.name
        productPrice.text = product.price
        productDiscount.text = product.discountAmount
        productDescription.text = product.description
    }

    override fun onClick(v: View?) {
        val pos = adapterPosition
        if (pos != RecyclerView.NO_POSITION) {
            listener.onItemClick(pos)
        }
    }
}