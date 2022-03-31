package com.festive.poster.ui.promoUpdates.holders

import com.festive.poster.R
import com.festive.poster.databinding.ListItemTodaysPickTemplateBinding
import com.festive.poster.models.PosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.framework.base.BaseActivity
import com.squareup.picasso.Picasso

class TodaysPickTemplateListViewHolder(binding: ListItemTodaysPickTemplateBinding):
    AppBaseRecyclerViewHolder<ListItemTodaysPickTemplateBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterPackModel
        binding.tvCatTitle.text = model.tagsModel.name
//        Picasso.get().load(model.tagsModel.icon).into(binding.ivCategoryIcon)
        binding.tvCatDesc.text = model.tagsModel.description
        //binding.ivCategoryIcon.setImageResource(R.drawable.ic_dummy_poster_cat_icon)
        //binding.tvCatDesc.text = "Post about offers or friday sale upto 50% discount  on selected products"
        model.posterList?.let {
            val adapter = AppBaseRecyclerViewAdapter(binding.root.context as BaseActivity<*, *>, it,object :
                RecyclerItemClickListener {
                override fun onItemClick(c_position: Int, c_item: BaseRecyclerViewItem?, actionType: Int) {
                    listener?.onChildClick(c_position, position, c_item, item, actionType)
                }
            })
            binding.vpTemplate.adapter = adapter
            binding.introIndicatorNew.setViewPager2(binding.vpTemplate)
        }

        super.bind(position, item)
    }
}