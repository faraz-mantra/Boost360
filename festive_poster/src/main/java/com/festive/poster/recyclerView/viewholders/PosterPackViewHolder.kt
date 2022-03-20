package com.festive.poster.recyclerView.viewholders

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemPosterPackBinding
import com.festive.poster.models.PosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.framework.base.BaseActivity
import com.framework.views.itemdecoration.LineItemDecoration

class PosterPackViewHolder(binding: ListItemPosterPackBinding) : AppBaseRecyclerViewHolder<ListItemPosterPackBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    val model = item as PosterPackModel
    binding.tvPosterHeading.text = model.tagsModel.name
    binding.layoutPurchased.isVisible = model.isPurchased
    binding.tvPrice.text = "Pack of ${model.posterList?.size} posters for ₹${model.price.toInt()}"
  //  setupVp(binding.vpPoster)

    if (model.isPurchased) {
      binding.btnGetPack.setBackgroundColor(getColor(R.color.white)!!)
      binding.btnGetPack.setTextColor(getColor(R.color.colorPrimary)!!)
      binding.btnGetPack.text = getResources()?.getString(R.string.view_pack)
    } else {
      binding.btnGetPack.setBackgroundColor(getColor(R.color.colorPrimary)!!)
      binding.btnGetPack.setTextColor(getColor(R.color.white)!!)
      binding.btnGetPack.text = "Get ${model.tagsModel.name} Posters Pack"
    }
    binding.btnGetPack.setOnClickListener {
      listener?.onItemClick(position, item, RecyclerViewActionType.GET_POSTER_PACK_CLICK.ordinal)
    }

    model.posterList?.let {
      val adapter = AppBaseRecyclerViewAdapter(binding.root.context as BaseActivity<*, *>, it, object : RecyclerItemClickListener {
        override fun onItemClick(c_position: Int, c_item: BaseRecyclerViewItem?, actionType: Int) {
          listener?.onChildClick(c_position, position, c_item, item, actionType)
        }
      })
//      binding.vpPoster.offscreenPageLimit = 1
      binding.vpPoster.adapter = adapter
      binding.vpPoster.layoutManager=LinearLayoutManager(binding.root.context,LinearLayoutManager.HORIZONTAL,false)
      binding.vpPoster.addItemDecoration(LineItemDecoration())
      //TabLayoutMediator(binding.dots, binding.vpPoster) { _, _ -> }.attach()
    }
    super.bind(position, item)
  }

  private fun setupVp(vpPdfs: ViewPager2) {
    vpPdfs.clipToPadding = false;
    vpPdfs.clipChildren = false;
    vpPdfs.offscreenPageLimit = 1
    vpPdfs.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER;
    val compositePageTransformer = CompositePageTransformer();
    compositePageTransformer.addTransformer(MarginPageTransformer(30));
    vpPdfs.setPageTransformer(compositePageTransformer);
  }
}