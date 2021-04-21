package com.boost.presignin.ui.intro

import android.content.Intent
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.boost.presignin.R
import com.boost.presignin.adapter.IntroAdapter
import com.boost.presignin.databinding.ActivityIntroBinding
import com.boost.presignin.model.IntroItem
import com.boost.presignin.ui.mobileVerification.MobileVerificationActivity
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.framework.utils.makeLinks
import kotlin.math.abs

class IntroActivity : BaseActivity<ActivityIntroBinding, BaseViewModel>() {

  private lateinit var items: List<IntroItem>
  private var isVideoPlaying = false


  private val nextRunnable = Runnable {
    if (!isVideoPlaying) {
      val item = binding?.introViewpager?.currentItem ?: 0
      val currentIndex = if (item < items.size - 1) item + 1 else 0
      binding?.introViewpager?.currentItem = currentIndex
      nextPageTimer()
    }
  }

  private val handler = Handler()

  private fun initItems() {
    items = listOf(
        IntroItem(getString(R.string.intro_title_0), getString(R.string.intro_sub_title_0), R.drawable.psn_intro_asset_1),
        IntroItem(getString(R.string.intro_title_1), getString(R.string.intro_sub_title_1), R.drawable.psn_intro_asset_2),
        IntroItem(getString(R.string.intro_title_2), getString(R.string.intro_sub_title_2), R.drawable.psn_intro_asset_3),
        IntroItem(getString(R.string.intro_title_3), getString(R.string.intro_sub_title_3), R.drawable.psn_intro_asset_4),
        IntroItem(getString(R.string.intro_title_4), getString(R.string.intro_sub_title_4), R.drawable.psn_intro_asset_5),
        IntroItem(getString(R.string.intro_title_5), getString(R.string.intro_sub_title_5), R.drawable.psn_intro_asset_6),
        IntroItem(getString(R.string.intro_title_6), getString(R.string.intro_sub_title_6), R.drawable.psn_intro_asset_7),
        IntroItem(getString(R.string.intro_title_7), getString(R.string.intro_sub_title_7), R.drawable.psn_intro_asset_8),
    )
  }

  private fun initTncString() {
    binding?.acceptTnc?.makeLinks(
        Pair("terms", View.OnClickListener {
          showShortToast("TERMS")
        }),
        Pair("conditions", View.OnClickListener {
          showShortToast("conditions")
        }))
  }

  override fun getLayout(): Int {
    return R.layout.activity_intro
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    initItems()
    initTncString()
    nextPageTimer()

    binding?.introViewpager?.adapter = IntroAdapter(supportFragmentManager, lifecycle, items, { setNextPage() }, { isVideoPlaying = it; })

    binding?.introIndicator?.setViewPager2(binding!!.introViewpager)
    binding?.introViewpager?.setPageTransformer { page, position ->
      page.translationX = page.width * -position
      if (position <= -1.0F || position >= 1.0F) {
        page.alpha = 0.0F
      } else if (position == 0.0F) {
        page.alpha = 1.0F
      } else {
        // position is between -1.0F & 0.0F OR 0.0F & 1.0F
        page.alpha = 1.0F - abs(position)
      }
    }
    binding?.btnLogin?.setOnClickListener {
      startActivity(Intent(this@IntroActivity, MobileVerificationActivity::class.java))
      finish()
    }
  }

  private fun setNextPage() {
    binding?.introViewpager?.currentItem = binding?.introViewpager?.currentItem ?: 0 + 1
  }

  private fun nextPageTimer() {
//    handler.postDelayed(nextRunnable, 3000)
  }
}