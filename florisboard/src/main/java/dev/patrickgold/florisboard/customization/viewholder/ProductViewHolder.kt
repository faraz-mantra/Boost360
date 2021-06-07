package dev.patrickgold.florisboard.customization.viewholder

import android.graphics.Paint
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.views.customViews.CustomTextView
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.Product

class ProductViewHolder(itemView: View, val listener: OnItemClickListener?) : BaseRecyclerViewHolder(itemView) {

    private val productImage: ImageView = itemView.findViewById(R.id.imageView)
    private val productName: CustomTextView = itemView.findViewById(R.id.tv_name)
    private val productPrice: CustomTextView = itemView.findViewById(R.id.tv_price)
    private val productDiscount: CustomTextView = itemView.findViewById(R.id.tv_discount)
    private val productDescription: CustomTextView = itemView.findViewById(R.id.tv_description)
    private val btnShare: Button = itemView.findViewById(R.id.btn_share)
    private val ctvDuration: CustomTextView = itemView.findViewById(R.id.ctv_duration)
    private val ctvOffPercentage: CustomTextView = itemView.findViewById(R.id.tv_off_percent)

    override fun bindTo(position: Int, item: BaseRecyclerItem?) {
        val product = item as Product
        // bind views with data
        Glide.with(productImage).load(product.imageUri).placeholder(R.drawable.placeholder_image_n).into(productImage)
        if (product.price?.toDouble() ?: 0.0 <= 0) {
            productPrice.gone()
        } else {
            productPrice.visible()
        }
        if (product.discountAmount?.toDouble() ?: 0.0 <= 0) {
            productDiscount.gone()
        } else {
            productDiscount.visible()
        }
        if (product.shipmentDuration ?: 0 <= 0) {
            ctvDuration.gone()
        } else {
            ctvDuration.visible()
        }
        ctvDuration.text = "${product.shipmentDuration} min"
        ctvOffPercentage.text = product.getProductOffPrice()
        productName.text = product.name
        productPrice.text = product.getProductPrice()
        productDiscount.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        productDiscount.text = product.getProductDiscountPrice()
        productDescription.text = product.description
        btnShare.setOnClickListener {
            listener?.onItemClick(position, product)
        }
    }
}