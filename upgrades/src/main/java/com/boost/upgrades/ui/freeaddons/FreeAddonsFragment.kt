package com.boost.upgrades.ui.freeaddons

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
import com.boost.upgrades.adapter.CompareFreeAddonsAdapter
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
import es.dmoral.toasty.Toasty
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.free_addons_fragment.*
import kotlinx.android.synthetic.main.free_addons_fragment.addons_back
import kotlinx.android.synthetic.main.free_addons_fragment.paid_title
import kotlinx.android.synthetic.main.free_addons_fragment.recycler_paidaddons
import kotlinx.android.synthetic.main.my_addons_fragment.*


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

        myAddonsViewModelFactory = FreeAddonsViewModelFactory(requireNotNull(requireActivity().application))

        viewModel = ViewModelProviders.of(requireActivity(), myAddonsViewModelFactory).get(FreeAddonsViewModel::class.java)

        progressDialog = ProgressDialog(requireContext())
        var purchasedPack = arguments!!.getStringArrayList("userPurchsedWidgets")
        if (purchasedPack != null) {
            purchasedPackages = purchasedPack
        }

        freeAddonsAdapter = CompareFreeAddonsAdapter((activity as UpgradeActivity), ArrayList(), this)


        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadData()
        initMVVM()






//        Glide.with(this).load(R.drawable.back_beau)
//                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
//                .into(back_image)




        addons_back.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }







        WebEngageController.trackEvent("ADDONS_MARKETPLACE My_Addons Loaded", "My_Addons", "")
    }

    private fun loadData() {
        viewModel.loadUpdates((activity as UpgradeActivity).fpid!!, (activity as UpgradeActivity).clientid)
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun initMVVM() {
        viewModel.getActiveFreeWidgets().observe(this, Observer {
            totalFreeItemList = it

            totalActiveFreeWidgetCount = totalFreeItemList!!.size
            totalActiveWidgetCount = totalActiveFreeWidgetCount + totalActivePremiumWidgetCount



            initializeFreeAddonsRecyclerView()


            if (totalFreeItemList != null) {
                paid_title.setText(totalFreeItemList!!.size.toString() + " Free add-ons")
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
