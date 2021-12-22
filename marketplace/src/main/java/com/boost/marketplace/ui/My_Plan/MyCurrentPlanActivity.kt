package com.boost.marketplace.ui.My_Plan

import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityMyCurrentPlanBinding
import com.boost.marketplace.ui.History_Orders.HistoryOrdersActivity
import com.boost.marketplace.ui.home.MarketPlaceActivity

class MyCurrentPlanActivity : AppBaseActivity<ActivityMyCurrentPlanBinding,MyCurrentPlanViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_my_current_plan
    }

    override fun getViewModelClass(): Class<MyCurrentPlanViewModel> {
        return MyCurrentPlanViewModel::class.java
    }


    override fun onCreateView() {
        super.onCreateView()


        binding?.arrowBtn?.setOnClickListener(View.OnClickListener {
            cardViewVisibilty()
        })
        binding?.cardView?.setOnClickListener(View.OnClickListener {
            cardViewVisibilty()
        })

        binding?.arrowBtn1?.setOnClickListener(View.OnClickListener {
            cardView1Visibilty()
        })

        binding?.cardView1?.setOnClickListener(View.OnClickListener {
            cardView1Visibilty()
        })

        binding?.help?.setOnClickListener {
            val intent = Intent(this, HistoryOrdersActivity::class.java)
            startActivity(intent)

        }

        binding?.addonsBack?.setOnClickListener {
            val intent = Intent(this, MarketPlaceActivity::class.java)
            startActivity(intent)

        }

    }

    private fun cardView1Visibilty() {
        if (binding?.expandableView1?.getVisibility() == View.GONE) {
            TransitionManager.beginDelayedTransition(binding?.cardView1, AutoTransition())
           binding?.expandableView1?.setVisibility(View.VISIBLE)
           binding?.arrowBtn1?.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
        } else {
            TransitionManager.beginDelayedTransition(binding?.cardView1, AutoTransition())
            binding?.expandableView1?.setVisibility(View.GONE)
           binding?.arrowBtn1?.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
        }
    }

    private fun cardViewVisibilty() {
        if (binding?.expandableView?.getVisibility() == View.GONE) {
            TransitionManager.beginDelayedTransition(binding?.cardView, AutoTransition())
            binding?.expandableView?.setVisibility(View.VISIBLE)
            binding?.arrowBtn?.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
        } else {
            TransitionManager.beginDelayedTransition(binding?.cardView, AutoTransition())
            binding?.expandableView?.setVisibility(View.GONE)
            binding?.arrowBtn?.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
        }
    }


}