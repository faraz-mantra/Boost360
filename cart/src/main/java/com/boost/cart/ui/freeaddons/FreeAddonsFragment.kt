package com.boost.cart.ui.freeaddons

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.boost.cart.R
import com.boost.cart.CartActivity
import com.boost.cart.adapter.CompareFreeAddonsAdapter
import com.boost.cart.base_class.BaseFragment
import com.boost.dbcenterapi.upgradeDB.model.*
import com.boost.cart.interfaces.MyAddonsListener
import com.boost.cart.utils.WebEngageController
import com.framework.webengageconstant.ADDONS_MARKETPLACE_MY_ADDONS_LOADED
import com.framework.webengageconstant.MY_ADDONS
import com.framework.webengageconstant.NO_EVENT_VALUE
import kotlinx.android.synthetic.main.free_addons_fragment.addons_back
import kotlinx.android.synthetic.main.free_addons_fragment.paid_title
import kotlinx.android.synthetic.main.free_addons_fragment.recycler_paidaddons


class FreeAddonsFragment : BaseFragment(), MyAddonsListener {

  lateinit var root: View

  //    lateinit var freeAddonsAdapter: FreeAddonsAdapter
  lateinit var freeAddonsAdapter: CompareFreeAddonsAdapter
  lateinit var compareFreeAddonsAdapter: CompareFreeAddonsAdapter

  //    lateinit var localStorage: LocalStorage
  lateinit var myAddonsViewModelFactory: FreeAddonsViewModelFactory

  var freeaddonsSeeMoreStatus = false
  var paidaddonsSeeMoreStatus = false

  var totalActiveWidgetCount = 0
  var totalActiveFreeWidgetCount = 0
  var totalActivePremiumWidgetCount = 0

  var totalFreeItemList: List<FeaturesModel>? = null

  lateinit var progressDialog: ProgressDialog

  var purchasedPackages = ArrayList<String>()

  companion object {
    fun newInstance() = FreeAddonsFragment()
  }

  private lateinit var viewModel: FreeAddonsViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    root = inflater.inflate(R.layout.free_addons_fragment, container, false)

    myAddonsViewModelFactory =
      FreeAddonsViewModelFactory(requireNotNull(requireActivity().application))

    viewModel = ViewModelProviders.of(requireActivity(), myAddonsViewModelFactory)
      .get(FreeAddonsViewModel::class.java)

    progressDialog = ProgressDialog(requireContext())
    var purchasedPack = requireArguments().getStringArrayList("userPurchsedWidgets")
    if (purchasedPack != null) {
      purchasedPackages = purchasedPack
    }

    freeAddonsAdapter = CompareFreeAddonsAdapter((activity as CartActivity), ArrayList(), this)


    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    loadData()
    initMVVM()


//        Glide.with(getApplicationContext()).load(R.drawable.back_beau)
//                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
//                .into(back_image)


    addons_back.setOnClickListener {
      (activity as CartActivity).popFragmentFromBackStack()
    }







    WebEngageController.trackEvent(ADDONS_MARKETPLACE_MY_ADDONS_LOADED, MY_ADDONS, NO_EVENT_VALUE)
  }

  private fun loadData() {
    viewModel.loadUpdates(
      (activity as? CartActivity)?.getAccessToken()?:"",
      (activity as CartActivity).fpid!!,
      (activity as CartActivity).clientid
    )
  }

  @SuppressLint("FragmentLiveDataObserve")
  private fun initMVVM() {
    viewModel.getActiveFreeWidgets().observe(this, Observer {
      totalFreeItemList = it

      totalActiveFreeWidgetCount = totalFreeItemList!!.size
      totalActiveWidgetCount = totalActiveFreeWidgetCount + totalActivePremiumWidgetCount



      initializeFreeAddonsRecyclerView()


      if (totalFreeItemList != null) {
        paid_title.setText(totalFreeItemList!!.size.toString() + " Free Add-ons")
//                if (totalFreeItemList!!.size > 6) {
        val lessList = totalFreeItemList
        updateFreeAddonsRecycler(lessList)

//                }
        /*else {
            myaddons_view1.visibility = View.INVISIBLE
            read_more_less_free_addons.visibility = View.GONE
            updateFreeAddonsRecycler(totalFreeItemList!!)
        }*/
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


  private fun updateFreeAddonsRecycler(list: List<FeaturesModel>?) {
    freeAddonsAdapter.addupdates(list)
    recycler_paidaddons.adapter = freeAddonsAdapter
    freeAddonsAdapter.notifyDataSetChanged()
  }


  fun initializeFreeAddonsRecyclerView() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    recycler_paidaddons.apply {
      layoutManager = gridLayoutManager

    }
    recycler_paidaddons.adapter = freeAddonsAdapter
  }


  override fun onFreeAddonsClicked(v: View?) {
    /* if (add_remove_layout.visibility == View.VISIBLE) {
         add_remove_layout.visibility = View.GONE
     } else {
         val itemPosition = recycler_freeaddons.getChildAdapterPosition(v!!)

     }*/
  }

  override fun onPaidAddonsClicked(v: View?) {
    /* if (add_remove_layout.visibility == View.VISIBLE) {
         add_remove_layout.visibility = View.GONE
     } else {
         val itemPosition = recycler_paidaddons.getChildAdapterPosition(v!!)

     }*/
  }
}
