package com.boost.upgrades.ui.myaddons

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.FreeAddonsAdapter
import com.boost.upgrades.adapter.PaidAddonsAdapter
import com.boost.upgrades.data.model.WidgetModel
import com.boost.upgrades.interfaces.MyAddonsListener
import com.boost.upgrades.ui.features.ViewAllFeaturesFragment
import com.boost.upgrades.ui.removeaddons.RemoveAddonsFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.Constants.Companion.REMOVE_ADDONS_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.VIEW_ALL_FEATURE
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.home_fragment.back_image
import kotlinx.android.synthetic.main.home_fragment.verifybtn1
import kotlinx.android.synthetic.main.my_addons_fragment.*

class MyAddonsFragment : BaseFragment(), MyAddonsListener {

    lateinit var root: View
    lateinit var freeAddonsAdapter: FreeAddonsAdapter
    lateinit var paidAddonsAdapter: PaidAddonsAdapter
//    lateinit var localStorage: LocalStorage
    lateinit var myAddonsViewModelFactory: MyAddonsViewModelFactory

    var freeaddonsSeeMoreStatus = false
    var paidaddonsSeeMoreStatus = false

    var totalItemList: List<WidgetModel>? = null

    companion object {
        fun newInstance() = MyAddonsFragment()
    }

    private lateinit var viewModel: MyAddonsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.my_addons_fragment, container, false)

        myAddonsViewModelFactory = MyAddonsViewModelFactory(requireNotNull(requireActivity().application))

        viewModel = ViewModelProviders.of(requireActivity(), myAddonsViewModelFactory).get(MyAddonsViewModel::class.java)

        freeAddonsAdapter = FreeAddonsAdapter((activity as UpgradeActivity), ArrayList(), this)
        paidAddonsAdapter = PaidAddonsAdapter((activity as UpgradeActivity), ArrayList(), this)

//        localStorage = LocalStorage.getInstance(context!!)!!

//        totalItemList = localStorage.getInitialLoad()

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadData()

        viewModel.upgradeResult().observe(this, Observer {
            totalItemList = it
            initializeFreeAddonsRecyclerView()
            initializePaidAddonsRecyclerView()

            if(totalItemList!=null){
                if(totalItemList!!.size > 6){
                    val lessList = totalItemList!!.subList(0,6)
                    updateFreeAddonsRecycler(lessList)
                }else {
                    updateFreeAddonsRecycler(totalItemList!!)
                }
            }

            if(totalItemList!=null){
                if(totalItemList!!.size > 6){
                    val lessList = totalItemList!!.subList(0,6)
                    updatePaidAddonsRecycler(lessList)
                }else {
                    updatePaidAddonsRecycler(totalItemList!!)
                }
            }
        })



        Glide.with(this).load(R.drawable.back_beau)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
            .into(back_image)


        verifybtn1.setOnClickListener{
            if(add_remove_layout.visibility == View.VISIBLE){
                add_remove_layout.visibility = View.GONE
            }else{
                (activity as UpgradeActivity).addFragment(
                    ViewAllFeaturesFragment.newInstance(),
                    Constants.VIEW_ALL_FEATURE
                )
            }
        }

        addons_back.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }

        paid_menu_layout.setOnClickListener {
            if(add_remove_layout.visibility == View.VISIBLE){
                add_remove_layout.visibility = View.GONE
            }else{
                add_remove_layout.visibility = View.VISIBLE
            }
        }

        all_addons_view_layout.setOnClickListener {
            if(add_remove_layout.visibility == View.VISIBLE){
                add_remove_layout.visibility = View.GONE
            }
        }

        add_paid_addons.setOnClickListener {
            add_remove_layout.visibility = View.GONE
            (activity as UpgradeActivity).addFragment(ViewAllFeaturesFragment.newInstance(), VIEW_ALL_FEATURE )
        }

        remove_paid_addons.setOnClickListener {
            add_remove_layout.visibility = View.GONE
            (activity as UpgradeActivity).addFragment(RemoveAddonsFragment.newInstance(), REMOVE_ADDONS_FRAGMENT )
        }

        read_more_less_free_addons.setOnClickListener {
            if(add_remove_layout.visibility == View.VISIBLE){
                add_remove_layout.visibility = View.GONE
                return@setOnClickListener
            }
            if(totalItemList!=null) {
                if (freeaddonsSeeMoreStatus) {
                    if(totalItemList!!.size > 6) {
                        val lessList = totalItemList!!.subList(0, 6)
                        updateFreeAddonsRecycler(lessList)
                        freeaddonsSeeMoreStatus = false
                        read_more_less_text_free_addons.setText("See more")
                        read_more_less_text_free_addons.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down),
                            null
                        )
                    }
                } else {
                    updateFreeAddonsRecycler(totalItemList!!)
                    freeaddonsSeeMoreStatus = true
                    read_more_less_text_free_addons.setText("See less")
                    read_more_less_text_free_addons.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(requireContext(),R.drawable.ic_arrow_up),null)
                }
            }
        }


        read_more_less_paid_addons.setOnClickListener {
            if(add_remove_layout.visibility == View.VISIBLE){
                add_remove_layout.visibility = View.GONE
                return@setOnClickListener
            }
            if(totalItemList!=null) {
                if (paidaddonsSeeMoreStatus) {
                    if(totalItemList!!.size > 6) {
                        val lessList = totalItemList!!.subList(0, 6)
                        updatePaidAddonsRecycler(lessList)
                        paidaddonsSeeMoreStatus = false
                        read_more_less_text_paid_addons.setText("See more")
                        read_more_less_text_paid_addons.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down),
                            null
                        )
                    }
                } else {
                    updatePaidAddonsRecycler(totalItemList!!)
                    paidaddonsSeeMoreStatus = true
                    read_more_less_text_paid_addons.setText("See less")
                    read_more_less_text_paid_addons.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(requireContext(),R.drawable.ic_arrow_up),null)
                }
            }
        }




    }

    private fun loadData() {
        viewModel.loadUpdates()
    }

    private fun updateFreeAddonsRecycler(list: List<WidgetModel>) {
        freeAddonsAdapter.addupdates(list)
        recycler_freeaddons.adapter = freeAddonsAdapter
        freeAddonsAdapter.notifyDataSetChanged()
    }

    private fun updatePaidAddonsRecycler(list: List<WidgetModel>) {
        paidAddonsAdapter.addupdates(list)
        recycler_paidaddons.adapter = paidAddonsAdapter
        paidAddonsAdapter.notifyDataSetChanged()
    }

    fun initializeFreeAddonsRecyclerView(){
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recycler_freeaddons.apply {
            layoutManager = gridLayoutManager

        }
        recycler_freeaddons.adapter = freeAddonsAdapter
    }

    fun initializePaidAddonsRecyclerView(){
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recycler_paidaddons.apply {
            layoutManager = gridLayoutManager

        }
        recycler_paidaddons.adapter = paidAddonsAdapter
    }

    override fun onFreeAddonsClicked(v: View?) {
        if(add_remove_layout.visibility == View.VISIBLE){
            add_remove_layout.visibility = View.GONE
        }else{
            val itemPosition = recycler_freeaddons.getChildAdapterPosition(v!!)

        }
    }

    override fun onPaidAddonsClicked(v: View?) {
        if(add_remove_layout.visibility == View.VISIBLE){
            add_remove_layout.visibility = View.GONE
        }else{
            val itemPosition = recycler_paidaddons.getChildAdapterPosition(v!!)

        }
    }
}
