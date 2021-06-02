package com.boost.presignin.ui.intro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.boost.presignin.R
import com.boost.presignin.adapter.IntroAdapter
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.ActivityIntroBinding
import com.boost.presignin.dialog.WebViewDialog
import com.boost.presignin.helper.ViewPager2Transformation
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.IntroItem
import com.boost.presignin.ui.AccountNotFoundActivity
import com.boost.presignin.ui.mobileVerification.MobileVerificationActivity
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.framework.utils.makeLinks
import com.framework.webengageconstant.*

class IntroActivity : BaseActivity<ActivityIntroBinding, BaseViewModel>() {

  private lateinit var items: List<IntroItem>
  private var isVideoPlaying = false
  private val handler = Handler()

  override fun getLayout(): Int {
    return R.layout.activity_intro
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  private val nextRunnable = Runnable {
    binding?.introViewpager?.post {
      if (!isVideoPlaying) {
        val item = binding?.introViewpager?.currentItem ?: 0
        val currentIndex = if (item == (items.size - 1)) 0 else item + 1
        binding?.introViewpager?.setCurrentItem(currentIndex, true)
        nextPageTimer()
      }
    }
  }

  private fun initTncString() {
    binding?.acceptTnc?.makeLinks(
      Pair("terms", View.OnClickListener {
        WebEngageController.trackEvent(BOOST_360_TERMS_CLICK, CLICKED, NO_EVENT_VALUE)
        openTNCDialog("https://www.getboost360.com/tnc?src=android&stage=presignup", resources.getString(R.string.boost360_terms_conditions))
      }),
      Pair("conditions", View.OnClickListener {
        WebEngageController.trackEvent(BOOST_360_CONDITIONS_CLICK, CLICKED, NO_EVENT_VALUE)
        openTNCDialog("https://www.getboost360.com/tnc?src=android&stage=presignup", resources.getString(R.string.boost360_terms_conditions))
      })
    )
  }

  private fun openTNCDialog(url: String, title: String) {
    WebViewDialog().apply {
      setData(false, url, title)
      onClickType = { }
      show(this@IntroActivity.supportFragmentManager, title)
    }
  }

  override fun onCreateView() {
    items = IntroItem().getData(this)
    initTncString()
    nextPageTimer()
    binding?.introViewpager?.adapter = IntroAdapter(supportFragmentManager, lifecycle, items, { setNextPage() }, { isVideoPlaying = it; })
    binding?.introIndicator?.setViewPager2(binding!!.introViewpager)
    binding?.introViewpager?.setPageTransformer(ViewPager2Transformation())
    binding?.btnCreate?.setOnClickListener {
//      navigator?.startActivity(AccountNotFoundActivity::class.java, args = Bundle().apply { putString(IntentConstant.EXTRA_PHONE_NUMBER.name, "8097789346") })
      WebEngageController.trackEvent(PS_INTRO_SCREEN_START, GET_START_CLICKED, NO_EVENT_VALUE)
      startActivity(Intent(this@IntroActivity, MobileVerificationActivity::class.java))
      finish()
    }
  }

  private fun setNextPage() {
    binding?.introViewpager?.currentItem = binding?.introViewpager?.currentItem ?: 0 + 1
  }

  fun slideNextPage() {
    binding?.introViewpager?.currentItem = binding?.introViewpager?.currentItem!! + 1

  }

  private fun nextPageTimer() {
    handler.postDelayed(nextRunnable, 3000)
  }
}