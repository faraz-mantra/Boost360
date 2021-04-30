package com.boost.presignin.holders

import android.view.View
import androidx.core.content.ContextCompat
import com.boost.presignin.R
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.ItemCategoryLayoutBinding
import com.boost.presignin.model.category.CategoryDataModel
import com.boost.presignin.recyclerView.AppBaseRecyclerViewHolder
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.invisible
import com.framework.extensions.setTextColorCompat
import com.framework.extensions.visible

class CategoryRecyclerViewHolder constructor(binding: ItemCategoryLayoutBinding) :
    AppBaseRecyclerViewHolder<ItemCategoryLayoutBinding>(binding){

    private var model: CategoryDataModel? = null

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        model = item as? CategoryDataModel
        setViews(model)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding.card -> onCardClicked()
        }
    }

    private fun onCardClicked() {
        model?.isSelected = model?.isSelected?.not() ?: true
        listener?.onItemClick(position = adapterPosition, item = model, actionType = RecyclerViewActionType.CATEGORY_ITEM_CLICKED.ordinal)
    }

    private fun setCardSelection(isSelected: Boolean) {
        if (isSelected) {
            getColor(R.color.colorAccent)?.let {binding.cardBg.setBackgroundColor(it)  }
            getColor(R.color.white)?.let { binding.name.setTextColorCompat(it) }
            getColor(R.color.white)?.let { binding.image.setTintColor(it) }
            binding.check.visible()
        } else {
            binding.cardBg.background = null
            getColor(R.color.white)?.let { binding.cardBg.setBackgroundColor(it)  }
            getColor(R.color.black_4a4a4a)?.let { binding.name.setTextColorCompat(it) }
            getColor(R.color.black_4a4a4a)?.let { binding.image.setTintColor(it) }
            binding.check.invisible()
        }
    }


    private fun setViews(model: CategoryDataModel?) {
        val resources = getResources() ?: return
        val activity = this.activity ?: return
        binding.name.text = model?.category_Name
        val drawable = model?.getImage(activity) ?: return
        binding.image.setImageDrawable(drawable)
        setClickListeners(binding.card)
        setCardSelection(model.isSelected)
    }

}