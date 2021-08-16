package com.inventoryorder.holders

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.utils.fromHtml
import com.google.android.material.textview.MaterialTextView
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemProductsAddedBinding
import com.inventoryorder.model.orderRequest.ItemsItem
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class ProductSelectedItemViewHolder(binding: ItemProductsAddedBinding) :
  AppBaseRecyclerViewHolder<ItemProductsAddedBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = (item as? ItemsItem) ?: return
    binding.view.visibility = if (position == 0) View.GONE else View.VISIBLE
    if (data.productDetails?.imageUri.isNullOrEmpty().not()) activity?.glideLoad(
      binding.itemImage,
      data.productDetails?.imageUri,
      R.drawable.placeholder_image_n
    )
    else binding.itemImage.setImageResource(R.drawable.placeholder_image_n)

    val currency = data.productDetails?.getCurrencyCodeValue() ?: "INR"
    binding.tvProductName.text = data.productDetails?.name ?: ""
    binding.tvProductPrice.text = "$currency ${data.productDetails?.getPayablePrice()}"
    binding.tvProductQuantity.text = " x ${data.quantity}"
    binding.tvTotalItemPrice.text = "$currency ${data.getPayablePriceAmount()}"
    if (data.getActualPriceAmount() > 0.0) {
      binding.tvActualPrice.visible()
      binding.tvActualPrice.text =
        fromHtml("<strike>$currency ${data.getActualPriceAmount()}</strike>")
    } else binding.tvActualPrice.gone()
    if (data.productDetails?.getDiscountPercentage() ?: 0.0 > 0.0) {
      binding.tvDiscount.visible()
      binding.tvDiscount.text = "${data.productDetails?.getDiscountPercentage()}% OFF"
    } else binding.tvDiscount.visibility = View.INVISIBLE
    binding.ivOptions.setOnClickListener { showItemOptionsPopUp(it, data) }
  }

  private fun showItemOptionsPopUp(view: View?, data: ItemsItem) {
    val inflater = LayoutInflater.from(activity)
    val popupView: View = inflater.inflate(R.layout.popup_item_option, null)
    val width = LinearLayout.LayoutParams.WRAP_CONTENT
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    val focusable = true
    val popupWindow = PopupWindow(popupView, width, height, focusable)
    val textRemoveItem =
      popupWindow.contentView.findViewById<MaterialTextView>(R.id.text_remove_item)
    textRemoveItem.setOnClickListener {
      popupWindow.dismiss()
      listener?.onItemClick(
        adapterPosition,
        data,
        RecyclerViewActionType.PRODUCT_SELECTED_ITEM_OPTIONS_REMOVE.ordinal
      )
    }
    popupWindow.showAsDropDown(view, 0, -60)
  }
}