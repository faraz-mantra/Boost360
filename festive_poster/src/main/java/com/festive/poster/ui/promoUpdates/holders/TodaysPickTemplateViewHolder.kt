package com.festive.poster.ui.promoUpdates.holders

import com.festive.poster.databinding.ListItemTodaysPickTemplateBinding
import com.festive.poster.models.promoModele.TodaysPickModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.framework.base.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator

class TodaysPickTemplateViewHolder(binding: ListItemTodaysPickTemplateBinding):
    AppBaseRecyclerViewHolder<ListItemTodaysPickTemplateBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as TodaysPickModel
        binding.tvCatTitle.text = model.cat_title
        binding.tvCatDesc.text = model.cat_desc
        model.templateList?.let {
            val adapter = AppBaseRecyclerViewAdapter(binding.root.context as BaseActivity<*, *>, it)
            binding.vpTemplate.adapter = adapter
            binding.introIndicatorNew.setViewPager2(binding.vpTemplate)
        }

        super.bind(position, item)
    }
}