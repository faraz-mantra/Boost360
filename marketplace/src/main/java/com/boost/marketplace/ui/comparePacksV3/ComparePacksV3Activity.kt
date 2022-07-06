package com.boost.marketplace.ui.comparePacksV3

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.marketplace.Adapters.PacksFaqAdapter
import com.boost.marketplace.Adapters.PacksV3HowToUseAdapter
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityComparePacksv3Binding
import com.boost.marketplace.ui.Compare_Plans.ComparePacksViewModel


class ComparePacksV3Activity: AppBaseActivity<ActivityComparePacksv3Binding, ComparePacksViewModel>() {

    lateinit var howToUseAdapter: PacksV3HowToUseAdapter
    lateinit var faqAdapter: PacksFaqAdapter



    override fun getLayout(): Int {
        return R.layout.activity_compare_packsv3
    }

    override fun getViewModelClass(): Class<ComparePacksViewModel> {
        return ComparePacksViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()

        howToUseAdapter = PacksV3HowToUseAdapter(this, ArrayList())
        faqAdapter = PacksFaqAdapter(this, ArrayList())

        initializeHowToUseRecycler()
        initializeFAQRecycler()

    }

    private fun initializeHowToUseRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
       binding?.packsHowToUseRecycler?.apply {
            layoutManager = gridLayoutManager
           binding?.packsHowToUseRecycler?.adapter = howToUseAdapter
        }
    }

    private fun initializeFAQRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.packsFaqRecycler?.apply {
            layoutManager = gridLayoutManager
            binding?.packsFaqRecycler?.adapter = faqAdapter
        }
    }

}