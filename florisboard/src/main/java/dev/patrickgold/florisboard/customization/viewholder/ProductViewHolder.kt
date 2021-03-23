package dev.patrickgold.florisboard.customization.viewholder

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.model.response.Product

class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val productImage: ImageView = itemView.findViewById(R.id.imageView)
    val productName: TextView = itemView.findViewById(R.id.tv_product_name)
    val productPrice: TextView = itemView.findViewById(R.id.tv_product_price)
    val productDiscount: TextView = itemView.findViewById(R.id.tv_discount)
    val productDescription: TextView = itemView.findViewById(R.id.tv_description)
    val productShare: Button = itemView.findViewById(R.id.buttonCopy)

    fun bindTo(product: Product) {
        // bind views with data
        Glide.with(productImage).load(product.imageUri)
                .placeholder(R.drawable.default_product_image).into(productImage)

        productName.text = product.name
        productPrice.text = product.price
        productDiscount.text = product.discountAmount
        productDescription.text = product.description
        productShare.setOnClickListener {
            Toast.makeText(itemView.context, "Share Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}