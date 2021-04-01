package com.boost.presignin.ui.intro

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.boost.presignin.R
import com.boost.presignin.adapter.IntroAdapter
import com.boost.presignin.databinding.ActivityIntroBinding
import com.boost.presignin.model.IntroItem
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.google.android.material.tabs.TabLayout
import java.lang.reflect.Type
import kotlin.math.abs

class IntroActivity : BaseActivity<ActivityIntroBinding,BaseViewModel>() {

    private lateinit var items: List<IntroItem>

    private fun initItems(){
        items  = listOf(
                IntroItem(getString(R.string.intro_title_0),"" ,R.drawable.psn_intro_asset_1),
                IntroItem(getString(R.string.intro_title_1),getString(R.string.intro_sub_title_1) ,R.drawable.psn_intro_asset_4),
                IntroItem(getString(R.string.intro_title_2),getString(R.string.intro_sub_title_2),R.drawable.psn_intro_asset_3),
                IntroItem(getString(R.string.intro_title_3),getString(R.string.intro_sub_title_3),R.drawable.psn_intro_asset_4),
                IntroItem(getString(R.string.intro_title_4),getString(R.string.intro_sub_title_4),R.drawable.psn_intro_asset_7),
                IntroItem(getString(R.string.intro_title_5),getString(R.string.intro_sub_title_5),R.drawable.psn_intro_asset_6),
                IntroItem(getString(R.string.intro_title_6),getString(R.string.intro_sub_title_6),R.drawable.psn_intro_asset_7),
                IntroItem(getString(R.string.intro_title_7),getString(R.string.intro_sub_title_7),R.drawable.psn_intro_asset_8),
        );
    }

    private fun initTncString(){

        val clickableSpan = object : ClickableSpan(){
            override fun onClick(p0: View) {

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true;
                ds.color = ContextCompat.getColor(this@IntroActivity, R.color.black)
                ds.linkColor = ContextCompat.getColor(this@IntroActivity, R.color.black)
            }
        }
        val tncString = getString(R.string.ps_accept_tnc)
        val termsQuery = getString(R.string.terms)
        val conditionsQuery = getString(R.string.conditions)

        val spannable = SpannableString(tncString)


        val indexStart = tncString.indexOf(termsQuery);
        val indexEnd = indexStart + termsQuery.length

        spannable.setSpan(clickableSpan, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        val indexStartCond = tncString.indexOf(conditionsQuery);
        val indexEndCond = indexStartCond + conditionsQuery.length
        spannable.setSpan(clickableSpan, indexStartCond, indexEndCond, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding?.acceptTnc?.setText(spannable,TextView.BufferType.SPANNABLE)

    }

    override fun getLayout(): Int {
        return R.layout.activity_intro
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        initItems();
        initTncString()

        binding?.introIndicator?.setupWithViewPager(binding?.introViewpager)
        binding?.introIndicator?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                val firstTab = binding?.introIndicator?.getTabAt(0);
                val iconView = firstTab?.view?.findViewById<ImageView>(R.id.play_icon_iv);
                val tintColor = if (tab!!.position == 0) R.color.intro_indi_active else R.color.intro_indi_inactive
                iconView?.setColorFilter(ContextCompat.getColor(this@IntroActivity, tintColor), android.graphics.PorterDuff.Mode.MULTIPLY);
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
        binding?.introViewpager?.adapter = IntroAdapter(supportFragmentManager,items)
        binding?.introViewpager?.setPageTransformer(true) { page, position ->
            page.translationX = page.width * -position;
            if(position <= -1.0F || position >= 1.0F) {
                page.alpha = 0.0F;
            } else if( position == 0.0F ) {
                page.alpha = 1.0F;
            } else {
                // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                page.alpha = 1.0F - abs(position);
            }
        }
        binding?.introIndicator?.getTabAt(0)?.customView = LayoutInflater.from(this).inflate(R.layout.item_play_indicator_view,null);

    }

}