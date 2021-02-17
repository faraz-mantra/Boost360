package com.inventoryorder.holders

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.google.android.material.textview.MaterialTextView
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemProductsAddedBinding
import com.inventoryorder.model.orderRequest.ItemsItem
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.ui.order.sheetOrder.EditCustomerInfoBottomSheetDialog
import com.squareup.picasso.Picasso

class ProductSelectedItemViewHolder(binding: ItemProductsAddedBinding) : AppBaseRecyclerViewHolder<ItemProductsAddedBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = (item as? ItemsItem) ?: return

        if (!data?.productDetails?.imageUri.isNullOrEmpty())
            Picasso.get().load(data?.productDetails?.imageUri).placeholder(R.drawable.placeholder_image).into(binding?.itemImage)

        binding?.tvProductName.text = data?.productDetails?.name ?: ""
        binding?.tvProductPrice.text = "${data?.productDetails?.currencyCode} ${data?.productDetails?.price.toString()}"
        binding?.tvProductQuantity?.text = " X ${data?.quantity}"

        binding?.tvTotalItemPrice?.text = "${data?.productDetails?.currencyCode ?: "INR"} ${data?.productDetails?.price?.times(data?.quantity)}"

        binding?.ivOptions?.setOnClickListener {
            //listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.PRODUCT_SELECTED_ITEM_OPTIONS.ordinal)
            showItemOptionsPopUp(it, data)
        }


    }

    private fun showItemOptionsPopUp(view: View?, data : ItemsItem) {

        // inflate the layout of the popup window
        val inflater = LayoutInflater.from(activity)
        val popupView: View = inflater.inflate(R.layout.popup_item_option, null)

        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        val textRemoveItem = popupWindow.contentView.findViewById<MaterialTextView>(R.id.text_remove_item)
        textRemoveItem.setOnClickListener {
            popupWindow?.dismiss()
            listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.PRODUCT_SELECTED_ITEM_OPTIONS_REMOVE.ordinal)
        }

        popupWindow.showAsDropDown(view, 0, -50)
    }
}