package com.example.template.views.recyclerView.holders

import com.example.template.recyclerView.AppBaseRecyclerViewHolder
import com.example.template.recyclerView.BaseRecyclerViewItem
import com.example.template.databinding.ListItemTodaysPickTemplateBinding
import com.example.template.models.TodaysPickModel
import com.example.template.recyclerView.AppBaseRecyclerViewAdapter
import com.framework.base.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator

class TodaysPickTemplateViewHolder(binding: ListItemTodaysPickTemplateBinding):
    AppBaseRecyclerViewHolder<ListItemTodaysPickTemplateBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as TodaysPickModel
        binding.tvCatTitle.text = model.cat_title
        binding.tvCatDesc.text = model.cat_desc
        model.templateList?.let {
            val adapter = AppBaseRecyclerViewAdapter(binding.root.context as BaseActivity<*, *>,it)
            binding.vpTemplate.adapter = adapter
            TabLayoutMediator(binding.tabLayout,binding.vpTemplate){
                    tab,position->
            }.attach()
        }

        super.bind(position, item)
    }
}