package com.boost.presignup.ui

import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.boost.presignup.R
import com.boost.presignup.adapter.IntroPageAdapter
import com.boost.presignup.datamodel.SharedViewModel
import com.boost.presignup.datamodel.SingleScreenModel
import com.boost.presignup.utils.WebEngageController
import com.framework.webengageconstant.*
import kotlinx.android.synthetic.main.fragment_intro.view.*
import java.util.*
import kotlin.collections.ArrayList


class IntroFragment : Fragment() {

  lateinit var root: View
  private lateinit var viewModel: SharedViewModel
  var previouspage = 0
  var currentPage = 0
  var list = ArrayList<SingleScreenModel>()
  val swipeTimer = Timer()
  //    lateinit var introViewPagerAdapter: IntroPageAdapter

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    list.add(
      SingleScreenModel(
        getString(R.string.Get_multichannel_presence),
        getString(R.string.for_your_offline_business_effortlessly),
        R.drawable.intro_asset_1
      )
    )
    list.add(
      SingleScreenModel(
        getString(R.string.Post_offers_and_updates),
        getString(R.string.targeting_your_potential_customers),
        R.drawable.intro_asset_2
      )
    )
    list.add(
      SingleScreenModel(
        getString(R.string.Engage_and_interact),
        getString(R.string.with_your_multichannel_visitors_and_customers),
        R.drawable.intro_asset_3
      )
    )
    list.add(
      SingleScreenModel(
        getString(R.string.AI_assistant_Ria),
        getString(R.string.to_help_you_go_towards_digital_success),
        R.drawable.intro_asset_4
      )
    )
    list.add(
      SingleScreenModel(
        getString(R.string.Join_35k_plus_biz_owners),
        getString(R.string.in_growing_their_business_digitally_everyday),
        R.drawable.intro_asset_5
      )
    )

    root = inflater.inflate(R.layout.fragment_intro, container, false)
    root.replay_intro_text.setOnClickListener {
      WebEngageController.trackEvent(
        PS_CLICKED_REPLAY_INTRO_VIDEO,
        REPLAY_INTRO_VIDEO,
        NO_EVENT_VALUE
      )
      requireActivity().onBackPressed()
    }
    root.replay_intro_text.paintFlags =
      root.replay_intro_text.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG
    root.view_pager.adapter = IntroPageAdapter(list)
    root.view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
      override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
      ) {
        currentPage = position
        WebEngageController.trackEvent(
          PS_INTRO_FEATURE_SLIDER,
          Slide_NO + currentPage,
          NO_EVENT_VALUE
        )
        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
      }

      override fun onPageScrollStateChanged(state: Int) {
        super.onPageScrollStateChanged(state)
//                Log.e("onPageScrollStateChanged :", "" + state)
        if (state == 0 && previouspage == currentPage) {
          currentPage = 0
          root.view_pager!!.setCurrentItem(currentPage, true)
        }
        if (state == 1 && currentPage == 4) {
          previouspage = currentPage
        } else {
          previouspage = -1
        }
      }

      override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
//                Log.e("onPageSelected :", "" + position)
      }
    });
    // Auto start of viewpager
    val handler = Handler()
    val Update = Runnable {
      if (currentPage == list.size) {
        currentPage = 0
      }
      root.view_pager!!.setCurrentItem(currentPage++, true)
    }
    swipeTimer.schedule(object : TimerTask() {
      override fun run() {
        handler.post(Update)
      }
    }, 3000, 3000)

    return root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    activity?.let {
      viewModel = ViewModelProviders.of(it).get(SharedViewModel::class.java)
    }

    viewModel.SingleLanguageButtonView()
  }

  override fun onStop() {
    super.onStop()
    swipeTimer.cancel()
  }

  companion object {
    fun newInstance() = IntroFragment()
  }

}