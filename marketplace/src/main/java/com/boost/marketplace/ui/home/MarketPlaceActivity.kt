package com.boost.marketplace.ui.home

import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityMarketplaceBinding

class MarketPlaceActivity : AppBaseActivity<ActivityMarketplaceBinding, MarketPlaceHomeViewModel>() {

    override fun getLayout(): Int {
        return R.layout.activity_marketplace
    }

    override fun getViewModelClass(): Class<MarketPlaceHomeViewModel> {
        return MarketPlaceHomeViewModel::class.java
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.overflowMenu -> {
                Toast.makeText(applicationContext, "Clicked on More Button", Toast.LENGTH_LONG).show()
                true
            }
            R.id.plan_history ->{
                Toast.makeText(applicationContext, "Clicked on Plan History", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.offer_coupons ->{
                Toast.makeText(applicationContext, "Clicked on Offer Coupons", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.help_section ->{
                Toast.makeText(applicationContext, "Clicked on Help Section", Toast.LENGTH_LONG).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

