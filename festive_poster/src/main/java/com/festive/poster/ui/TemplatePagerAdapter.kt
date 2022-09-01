package com.festive.poster.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.festive.poster.R
import com.festive.poster.databinding.ListItemTemplateForRvBinding
import com.festive.poster.models.PosterModel

class TemplatePagerAdapter(val context:Context,val list:List<PosterModel>):PagerAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = DataBindingUtil.inflate<ListItemTemplateForRvBinding>(LayoutInflater.from(
            context
        ),R.layout.list_item_template_for_rv,container,false)

        container.addView(binding.root)
        binding.root.setTag(position);

        return binding.root
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return `object`==view
    }
}