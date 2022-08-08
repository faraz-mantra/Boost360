package com.festive.poster.ui.promoUpdates.holders

import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.databinding.ListItemTodaysPickTemplateBinding
import com.festive.poster.models.PosterPackModel
import com.festive.poster.models.TodaysPickCategory
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.framework.base.BaseActivity
import com.framework.utils.loadFromUrl
import com.framework.utils.toArrayList
import com.framework.views.itemdecoration.LineItemDecoration
import com.squareup.picasso.Picasso

class TodaysPickCategoryViewHolder(binding: ListItemTodaysPickTemplateBinding):
    AppBaseRecyclerViewHolder<ListItemTodaysPickTemplateBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as TodaysPickCategory
        binding.tvCatTitle.text = model.name
        binding.ivCategoryImg.loadFromUrl(model.iconUrl)
//        Picasso.get().load(model.tagsModel.icon).into(binding.ivCategoryIcon)
       // binding.tvCatDesc.text = model.description
        //binding.ivCategoryIcon.setImageResource(R.drawable.ic_dummy_poster_cat_icon)
        //binding.tvCatDesc.text = "Post about offers or friday sale upto 50% discount  on selected products"
        model.templates?.let {
            val adapter = AppBaseRecyclerViewAdapter(binding.root.context as BaseActivity<*, *>, it.toArrayList(),object :
                RecyclerItemClickListener {
                override fun onItemClick(c_position: Int, c_item: BaseRecyclerViewItem?, actionType: Int) {
                    listener?.onChildClick(c_position, position, c_item, item, actionType)
                }
            })
            binding.vpTemplate.adapter = adapter
            binding.vpTemplate.layoutManager=
                LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL,false)
            binding.vpTemplate.addItemDecoration(LineItemDecoration(40,10))
        }

        super.bind(position, item)
    }
}