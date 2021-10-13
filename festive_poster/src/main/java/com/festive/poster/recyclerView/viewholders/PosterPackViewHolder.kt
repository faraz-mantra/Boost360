package com.festive.poster.recyclerView.viewholders


import android.view.View
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
import com.framework.adapters.RecyclerViewItemClickListener
import com.framework.base.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.abs

class PosterPackViewHolder(binding: ListItemPosterPackBinding):
    AppBaseRecyclerViewHolder<ListItemPosterPackBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterPackModel
        binding.tvPosterHeading.text = model.tagsModel.name
        binding.tvPrice.text ="Pack of ${model.posterList?.size} posters for ₹${model.price}"
        setupVp(binding.vpPoster)

        if (model.isPurchased){
            binding.btnGetPack.text=getResources()?.getString(R.string.view_pack)
        }else{
            binding.btnGetPack.text = "Get ${model.tagsModel.name} Posters Pack"

        }
        binding.btnGetPack.setOnClickListener {
            listener?.onItemClick(position,item,RecyclerViewActionType.GET_POSTER_PACK_CLICK.ordinal)
        }

        model.posterList?.let {
            val adapter = AppBaseRecyclerViewAdapter(binding.root.context as BaseActivity<*, *>,it,object :RecyclerItemClickListener{
                override fun onItemClick(
                    c_position: Int,
                    c_item: BaseRecyclerViewItem?,
                    actionType: Int
                ) {
                    listener?.onChildClick(c_position,position,c_item,item,actionType)
                }


            })
            binding.vpPoster.adapter = adapter
            TabLayoutMediator(binding.dots,binding.vpPoster){
                    tab,position->
            }.attach()
        }




        super.bind(position, item)
    }

    private fun setupVp(vpPdfs: ViewPager2) {
        vpPdfs.clipToPadding = false;
        vpPdfs.clipChildren = false;
        vpPdfs.offscreenPageLimit=3
        vpPdfs.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER;

        val compositePageTransformer = CompositePageTransformer();
        compositePageTransformer.addTransformer( MarginPageTransformer(30));

        vpPdfs.setPageTransformer(compositePageTransformer);

    }


}