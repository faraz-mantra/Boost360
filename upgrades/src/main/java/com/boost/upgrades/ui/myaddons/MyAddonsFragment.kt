package com.boost.upgrades.ui.myaddons

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.FreeAddonsAdapter
import com.boost.upgrades.adapter.PaidAddonsAdapter
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.interfaces.MyAddonsListener
import com.boost.upgrades.ui.features.ViewAllFeaturesFragment
import com.boost.upgrades.ui.history.HistoryFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.Constants.Companion.HISTORY_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.VIEW_ALL_FEATURE
import com.boost.upgrades.utils.WebEngageController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.framework.webengageconstant.*
import es.dmoral.toasty.Toasty
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.my_addons_fragment.*
import kotlinx.android.synthetic.main.view_all_features_fragment.*

class MyAddonsFragment : BaseFragment(), MyAddonsListener {

  lateinit var root: View
  lateinit var freeAddonsAdapter: FreeAddonsAdapter
  lateinit var paidAddonsAdapter: PaidAddonsAdapter

  //    lateinit var localStorage: LocalStorage
  lateinit var myAddonsViewModelFactory: MyAddonsViewModelFactory

  var freeaddonsSeeMoreStatus = false
  var paidaddonsSeeMoreStatus = false

  var totalActiveWidgetCount = 0
  var totalActiveFreeWidgetCount = 0
  var totalActivePremiumWidgetCount = 0

  var totalFreeItemList: List<FeaturesModel>? = null
  var totalPaidItemList: List<FeaturesModel>? = null

  lateinit var progressDialog: ProgressDialog

  var purchasedPackages = ArrayList<String>()

  companion object {
    fun newInstance() = MyAddonsFragment()
  }

  private lateinit var viewModel: MyAddonsViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    root = inflater.inflate(R.layout.my_addons_fragment, container, false)

    myAddonsViewModelFactory =
      MyAddonsViewModelFactory(requireNotNull(requireActivity().application))

    viewModel = ViewModelProviders.of(requireActivity(), myAddonsViewModelFactory)
      .get(MyAddonsViewModel::class.java)

    progressDialog = ProgressDialog(requireContext())
    var purchasedPack = requireArguments().getStringArrayList("userPurchsedWidgets")
    if (purchasedPack != null) {
      purchasedPackages = purchasedPack
    }

    freeAddonsAdapter = FreeAddonsAdapter((activity as UpgradeActivity), ArrayList(), this)
    paidAddonsAdapter = PaidAddonsAdapter((activity as UpgradeActivity), ArrayList(), this)

    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    loadData()
    initMVVM()
    initializeFreeAddonsRecyclerView()
    initializePaidAddonsRecyclerView()
    shimmer_view_paidaddon.startShimmer()
    shimmer_view_freeaddon.startShimmer()
    val profileURL = (activity as UpgradeActivity).profileUrl

    if (profileURL.isNullOrEmpty() || profileURL.length < 2) {
      Glide.with(this)
        .load(R.drawable.group)
        .into(merchant_logo)
    } else {
      Glide.with(this)
        .load(profileURL)
        .into(merchant_logo)
    }

    top_line_view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)


//        Glide.with(this).load(R.drawable.back_beau)
//                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
//                .into(back_image)


    verifybtn1.setOnClickListener {
      if (add_remove_layout.visibility == View.VISIBLE) {
        add_remove_layout.visibility = View.GONE
      } else {
        val args = Bundle()
        args.putStringArrayList(
          "userPurchsedWidgets",
          arguments?.getStringArrayList("userPurchsedWidgets")
        )
        (activity as UpgradeActivity).addFragmentHome(
          ViewAllFeaturesFragment.newInstance(),
          Constants.VIEW_ALL_FEATURE, args
        )
      }
    }

    view_orders_history.setOnClickListener {
      //            Toasty.info(requireContext(), R.string.feature_coming_soon).show()
      (activity as UpgradeActivity).addFragment(HistoryFragment.newInstance(), HISTORY_FRAGMENT)
    }

    addons_back.setOnClickListener {
      (activity as UpgradeActivity).popFragmentFromBackStack()
    }

    paid_menu_layout.setOnClickListener {
      if (add_remove_layout.visibility == View.VISIBLE) {
        add_remove_layout.visibility = View.GONE
      } else {
        add_remove_layout.visibility = View.VISIBLE
      }
    }

    all_addons_view_layout.setOnClickListener {
      if (add_remove_layout.visibility == View.VISIBLE) {
        add_remove_layout.visibility = View.GONE
      }
    }

    add_paid_addons.setOnClickListener {
      add_remove_layout.visibility = View.GONE
      val args = Bundle()
      args.putStringArrayList(
        "userPurchsedWidgets",
        arguments?.getStringArrayList("userPurchsedWidgets")
      )
      (activity as UpgradeActivity).addFragmentHome(
        ViewAllFeaturesFragment.newInstance(),
        VIEW_ALL_FEATURE,
        args
      )
    }

    remove_paid_addons.setOnClickListener {
      add_remove_layout.visibility = View.GONE
//            (activity as UpgradeActivity).addFragment(RemoveAddonsFragment.newInstance(), REMOVE_ADDONS_FRAGMENT)
      Toasty.warning(requireContext(), R.string.feature_coming_soon).show()
    }

    read_more_less_free_addons.setOnClickListener {
      if (add_remove_layout.visibility == View.VISIBLE) {
        add_remove_layout.visibility = View.GONE
      } else {
        if (totalFreeItemList != null) {
          if (freeaddonsSeeMoreStatus && totalFreeItemList!!.size > 6) {
            val lessList = totalFreeItemList!!.subList(0, 6)
            updateFreeAddonsRecycler(lessList)
            freeaddonsSeeMoreStatus = false
            read_more_less_text_free_addons.setText("See more")
            read_more_less_text_free_addons.setCompoundDrawablesWithIntrinsicBounds(
              null,
              null,
              ContextCompat.getDrawable(requireContext(), R.drawable.addons_arrow_down),
              null
            )
          } else {
            updateFreeAddonsRecycler(totalFreeItemList!!)
            freeaddonsSeeMoreStatus = true
            read_more_less_text_free_addons.setText("See less")
            read_more_less_text_free_addons.setCompoundDrawablesWithIntrinsicBounds(
              null,
              null,
              ContextCompat.getDrawable(requireContext(), R.drawable.addons_arrow_up),
              null
            )


            WebEngageController.trackEvent(
              ADDONS_MARKETPLACE_FREE_ADDONS_SEE_MORE,
              FREE_ADDONS,
              NO_EVENT_VALUE
            )
          }
        }
      }
    }


    read_more_less_paid_addons.setOnClickListener {
      if (add_remove_layout.visibility == View.VISIBLE) {
        add_remove_layout.visibility = View.GONE
      } else {
        if (totalPaidItemList != null) {
          if (paidaddonsSeeMoreStatus && totalPaidItemList!!.size > 4) {
            val lessList = totalPaidItemList!!.subList(0, 4)
            updatePaidAddonsRecycler(lessList)
            paidaddonsSeeMoreStatus = false
            read_more_less_text_paid_addons.setText("See more")
            read_more_less_text_paid_addons.setCompoundDrawablesWithIntrinsicBounds(
              null,
              null,
              ContextCompat.getDrawable(requireContext(), R.drawable.addons_arrow_down),
              null
            )
          } else {
            updatePaidAddonsRecycler(totalPaidItemList!!)
            paidaddonsSeeMoreStatus = true
            read_more_less_text_paid_addons.setText("See less")
            read_more_less_text_paid_addons.setCompoundDrawablesWithIntrinsicBounds(
              null,
              null,
              ContextCompat.getDrawable(requireContext(), R.drawable.addons_arrow_up),
              null
            )
          }
        }
      }
    }

    WebEngageController.trackEvent(ADDONS_MARKETPLACE_MY_ADDONS_LOADED, MY_ADDONS, NO_EVENT_VALUE)
  }

  private fun loadData() {
    viewModel.loadUpdates(
      (activity as? UpgradeActivity)?.getAccessToken()?:"",
      (activity as UpgradeActivity).fpid!!,
      (activity as UpgradeActivity).clientid
    )
  }

  @SuppressLint("FragmentLiveDataObserve")
  private fun initMVVM() {
    viewModel.getActiveFreeWidgets().observe(this, Observer {
      totalFreeItemList = it

      totalActiveFreeWidgetCount = totalFreeItemList!!.size
      totalActiveWidgetCount = totalActiveFreeWidgetCount + totalActivePremiumWidgetCount

      setHeadlineTexts()

//            initializeFreeAddonsRecyclerView()
//            initializePaidAddonsRecyclerView()

      if (totalFreeItemList != null) {
        if (totalFreeItemList!!.size > 6) {
          if (shimmer_view_freeaddon.isShimmerStarted) {
            shimmer_view_freeaddon.stopShimmer()
            shimmer_view_freeaddon.visibility = View.GONE
          }
          val lessList = totalFreeItemList!!.subList(0, 6)
          updateFreeAddonsRecycler(lessList)
          myaddons_view1.visibility = View.VISIBLE
          read_more_less_free_addons.visibility = View.VISIBLE
        } else {
          if (shimmer_view_freeaddon.isShimmerStarted) {
            shimmer_view_freeaddon.stopShimmer()
            shimmer_view_freeaddon.visibility = View.GONE
          }
          myaddons_view1.visibility = View.INVISIBLE
          read_more_less_free_addons.visibility = View.GONE
          updateFreeAddonsRecycler(totalFreeItemList!!)
        }
      }
    })
    viewModel.getActivePremiumWidgets().observe(this, Observer {
      Log.i("getActiveWidgets", it.toString())
      totalPaidItemList = it

      totalActivePremiumWidgetCount = totalPaidItemList!!.size
      totalActiveWidgetCount = totalActiveFreeWidgetCount + totalActivePremiumWidgetCount

      setHeadlineTexts()

      val paidItemsCount = totalPaidItemList!!.size

      if (paidItemsCount != null && paidItemsCount > 0) {
        if (shimmer_view_paidaddon.isShimmerStarted) {
          shimmer_view_paidaddon.stopShimmer()
          shimmer_view_paidaddon.visibility = View.GONE
        }
        paid_title.setText(totalPaidItemList!!.size.toString() + " Premium add-ons")
        paid_subtitle.setText(totalPaidItemList!!.size.toString() + " Activated, 0 Syncing and 0 needs Attention")
        read_more_less_paid_addons.visibility = View.VISIBLE
        premium_account_flag.visibility = View.VISIBLE
      } else {
        if (shimmer_view_paidaddon.isShimmerStarted) {
          shimmer_view_paidaddon.stopShimmer()
          shimmer_view_paidaddon.visibility = View.GONE
        }
        paid_title.setText("No Premium add-ons active.")
        paid_subtitle.setText("check out the recommended add-ons for your business")
        read_more_less_paid_addons.visibility = View.GONE
      }

      if (totalPaidItemList != null) {
        if (totalPaidItemList!!.size > 4) {
          val lessList = totalPaidItemList!!.subList(0, 4)
          updatePaidAddonsRecycler(lessList)
          myaddons_view2.visibility = View.VISIBLE
          read_more_less_paid_addons.visibility = View.VISIBLE
        } else {
          myaddons_view2.visibility = View.INVISIBLE
          read_more_less_paid_addons.visibility = View.GONE
          updatePaidAddonsRecycler(totalPaidItemList!!)
        }
      }
    })
    viewModel.updatesLoader().observe(this, Observer {
      if (it) {
        val status = "Loading. Please wait..."
        progressDialog.setMessage(status)
        progressDialog.setCancelable(false) // disable dismiss by tapping outside of the dialog
        progressDialog.show()
      } else {
        progressDialog.dismiss()
      }
    })
  }

  private fun setHeadlineTexts() {
    free_addons_name.setText("Currently using\n" + totalActiveWidgetCount + " add-ons")
    bottom_free_addons.setText(totalActiveFreeWidgetCount.toString() + " free, " + totalActivePremiumWidgetCount.toString() + " premium")
    free_addons_title.setText(totalActiveFreeWidgetCount.toString() + " Free Add-ons")
  }

  private fun updateFreeAddonsRecycler(list: List<FeaturesModel>) {
    freeAddonsAdapter.addupdates(list)
    recycler_freeaddons.adapter = freeAddonsAdapter
    freeAddonsAdapter.notifyDataSetChanged()
  }

  private fun updatePaidAddonsRecycler(list: List<FeaturesModel>) {
    paidaddons_layout.visibility = View.VISIBLE
    paidAddonsAdapter.addupdates(list)
    recycler_paidaddons.adapter = paidAddonsAdapter
    paidAddonsAdapter.notifyDataSetChanged()
  }

  fun initializeFreeAddonsRecyclerView() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 2)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    recycler_freeaddons.apply {
      layoutManager = gridLayoutManager

    }
    recycler_freeaddons.adapter = freeAddonsAdapter
  }

  fun initializePaidAddonsRecyclerView() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    recycler_paidaddons.apply {
      layoutManager = gridLayoutManager

    }
    recycler_paidaddons.adapter = paidAddonsAdapter
  }

  override fun onFreeAddonsClicked(v: View?) {
    if (add_remove_layout.visibility == View.VISIBLE) {
      add_remove_layout.visibility = View.GONE
    } else {
      val itemPosition = recycler_freeaddons.getChildAdapterPosition(v!!)

    }
  }

  override fun onPaidAddonsClicked(v: View?) {
    if (add_remove_layout.visibility == View.VISIBLE) {
      add_remove_layout.visibility = View.GONE
    } else {
      val itemPosition = recycler_paidaddons.getChildAdapterPosition(v!!)

    }
  }
}
