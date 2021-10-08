package com.appservice.holder

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.appservice.AppServiceApplication
import com.appservice.R
import com.appservice.databinding.RecyclerItemProductCategoryBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.ui.catalog.catalogProduct.listing.CategoryProduct
import com.framework.views.customViews.CustomTextView

class ProductCategoryViewHolder(binding: RecyclerItemProductCategoryBinding) : AppBaseRecyclerViewHolder<RecyclerItemProductCategoryBinding>(binding) {
    private var popupWindow: PopupWindow? = null
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = item as? CategoryProduct
        binding.ctvCategory.text = data?.name
        binding.ctvProductCount.text = "${data?.countItems} products under this category"
        binding.civCategoryMenu.setOnClickListener {
            if (this.popupWindow?.isShowing == true) this.popupWindow?.dismiss() else showPopupWindow(it)
        }
    }

    private fun showPopupWindow(anchor: View) {
        val view = LayoutInflater.from(AppServiceApplication.instance).inflate(R.layout.popup_window_category_menu, null)
        this.popupWindow = PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true)
        val share = this.popupWindow?.contentView?.findViewById<CustomTextView>(R.id.ctv_share)
        val delete = this.popupWindow?.contentView?.findViewById<CustomTextView>(R.id.ctv_delete)
        val rename = this.popupWindow?.contentView?.findViewById<CustomTextView>(R.id.ctv_rename)
        share?.setOnClickListener {

            this.popupWindow?.dismiss()
        }
        delete?.setOnClickListener {
            this.popupWindow?.dismiss()
        }
        rename?.setOnClickListener {
            this.popupWindow?.dismiss()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) this.popupWindow?.elevation = 5.0F
        this.popupWindow?.showAsDropDown(anchor, -20, 0)
    }

}