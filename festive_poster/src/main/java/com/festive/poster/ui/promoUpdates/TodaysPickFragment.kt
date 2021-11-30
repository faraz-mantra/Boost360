package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.databinding.FragmentTodaysPickBinding
import com.festive.poster.models.promoModele.TemplateModel
import com.festive.poster.models.promoModele.TodaysPickModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

class TodaysPickFragment: AppBaseFragment<FragmentTodaysPickBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_todays_pick
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
    companion object {
        fun newInstance(bundle: Bundle = Bundle()): TodaysPickFragment {
            val fragment = TodaysPickFragment()
            fragment.arguments = bundle
            return fragment
        }


    }

    override fun onCreateView() {
        val dataList = arrayListOf(
            TodaysPickModel(cat_title = "Discounts and Offers",cat_desc="Discounts and Offers sajbd jadb basjfb ab cf",
                templateList = arrayListOf(TemplateModel(desc="50% off get the offfer asd wsdf qawf wqjwj qwsd "),TemplateModel(),TemplateModel(),TemplateModel())),
            TodaysPickModel(cat_title = "Discounts and Offers",cat_desc="Discounts and Offers sajbd jadb basjfb ab cf",
                templateList = arrayListOf(TemplateModel(desc="50% off get the offfer asd wsdf qawf wqjwj qwsd "),TemplateModel(),TemplateModel(),TemplateModel())),
            TodaysPickModel(cat_title = "Discounts and Offers",cat_desc="Discounts and Offers sajbd jadb basjfb ab cf",
                templateList = arrayListOf(TemplateModel(desc="50% off get the offfer asd wsdf qawf wqjwj qwsd "),TemplateModel(),TemplateModel(),TemplateModel())),
        )

        val adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,dataList)
        binding?.rvTemplates?.adapter = adapter
        binding?.rvTemplates?.layoutManager = LinearLayoutManager(requireActivity())



    }
}