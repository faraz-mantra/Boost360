package com.boost.presignin.holder

import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.boost.presignin.R
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.ListItemCategorySuggestionBinding
import com.boost.presignin.databinding.RecyclerItemFpInfoBinding
import com.boost.presignin.model.CategorySuggestionUiModel
import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.boost.presignin.model.category.ApiCategoryResponseCategory
import com.boost.presignin.model.fpList.ResultItem
import com.boost.presignin.recyclerView.AppBaseRecyclerViewHolder
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad

class CategorySuggestionViewholder(binding: ListItemCategorySuggestionBinding) :
  AppBaseRecyclerViewHolder<ListItemCategorySuggestionBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? CategorySuggestionUiModel ?: return

    binding.tvSubCat.text = "in "+data.subCategory
    val spannableString = SpannableString(data.category)
    val boldStart = data.category.indexOf(data.category)
    if (boldStart>-1){
      spannableString.setSpan(ForegroundColorSpan(Color.BLACK),boldStart,boldStart+data.searchKeyword.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
      binding.tvCat.text = spannableString
    }else{
      binding.tvCat.text = data.category
    }
    setClickListeners(binding.root)
  }

  private fun onItemClick(position: Int, item: BaseRecyclerViewItem) {
    listener?.onItemClick(position, item, RecyclerViewActionType.CATEGORY_SUGGESTION_CLICKED.ordinal)
  }
}