package com.boost.marketplace.ui.browse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.Adapters.CompareItemAdapter
import com.boost.marketplace.Adapters.MarketOfferDetailAdapter
import com.boost.marketplace.Adapters.ParentCompareItemAdapter
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityMyCurrentPlanBinding
import com.boost.marketplace.databinding.ActivitySearchBinding
import com.boost.marketplace.ui.marketplace_Offers.MarketPlaceOffersActivity
import java.util.ArrayList

class SearchActivity : AppBaseActivity<ActivitySearchBinding,SearchViewModel>() {

    lateinit var packageAdaptor: CompareItemAdapter

    override fun getLayout(): Int {
        return R.layout.activity_search
    }

    override fun getViewModelClass(): Class<SearchViewModel> {
        return SearchViewModel::class.java
    }


    override fun onCreateView() {
        super.onCreateView()

         initializeDetailsRecycler()
        packageAdaptor = CompareItemAdapter(ArrayList<FeaturesModel>())


    }

    private fun initializeDetailsRecycler() {

            val gridLayoutManager = LinearLayoutManager(this)
            gridLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            binding?.childRecyclerview?.apply {
                layoutManager = gridLayoutManager

            }


    }
}