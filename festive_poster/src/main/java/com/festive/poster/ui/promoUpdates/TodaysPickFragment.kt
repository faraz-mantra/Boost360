package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.databinding.FragmentTodaysPickBinding
import com.festive.poster.models.PosterDetailsModel
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.models.PosterPackTagModel
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
            PosterPackModel(PosterPackTagModel("","","","",false,-1),
                arrayListOf(PosterModel(false,"", PosterDetailsModel("",false,0.0,"",false),"",ArrayList(),ArrayList(),"",ArrayList(),null,RecyclerViewItemType.TEMPLATE_VIEW_FOR_VP.getLayout())),0.0,false,RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()),
            PosterPackModel(PosterPackTagModel("","","","",false,-1),
                arrayListOf(PosterModel(false,"", PosterDetailsModel("",false,0.0,"",false),"",ArrayList(),ArrayList(),"",ArrayList(),null,RecyclerViewItemType.TEMPLATE_VIEW_FOR_VP.getLayout())),0.0,false,RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()),PosterPackModel(PosterPackTagModel("","","","",false,-1),ArrayList(),0.0,false,RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()),
            PosterPackModel(PosterPackTagModel("","","","",false,-1),
                arrayListOf(PosterModel(false,"", PosterDetailsModel("",false,0.0,"",false),"",ArrayList(),ArrayList(),"",ArrayList(),null,RecyclerViewItemType.TEMPLATE_VIEW_FOR_VP.getLayout())),0.0,false,RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()),PosterPackModel(PosterPackTagModel("","","","",false,-1),ArrayList(),0.0,false,RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW.getLayout()),

            )


        val adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,dataList)
        binding?.rvTemplates?.adapter = adapter
        binding?.rvTemplates?.layoutManager = LinearLayoutManager(requireActivity())



    }
}