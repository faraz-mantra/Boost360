package com.boost.upgrades.ui.removeaddons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.RemoveAddonsAdapter
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.database.LocalStorage
import com.boost.upgrades.interfaces.RemoveItemsListener
import com.boost.upgrades.utils.Constants.Companion.REMOVE_ADDONS_CONFIRMATION_FRAGMENT
import com.boost.upgrades.utils.Utils.longToast
import kotlinx.android.synthetic.main.remove_addons_fragment.*

class RemoveAddonsFragment : BaseFragment("MarketPlaceRemoveAddonsFragment"), RemoveItemsListener {

  lateinit var root: View
  private lateinit var viewModel: RemoveAddonsViewModel
  lateinit var removeAddonsAdapter: RemoveAddonsAdapter

  lateinit var localStorage: LocalStorage

  var cart_list: MutableList<CartModel>? = null

  val selectedItem: MutableList<CartModel> = ArrayList()

  companion object {
    fun newInstance() = RemoveAddonsFragment()
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    root = inflater.inflate(R.layout.remove_addons_fragment, container, false)

    viewModel = ViewModelProvider(requireActivity())[RemoveAddonsViewModel::class.java]

    removeAddonsAdapter = RemoveAddonsAdapter((activity as UpgradeActivity), ArrayList(), this)

    localStorage = LocalStorage.getInstance(requireContext())!!

//        cart_list = localStorage.getCartItems() as MutableList<UpdatesModel>?

    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    loadData()

    viewModel.cartResult().observe(viewLifecycleOwner, Observer {
      cart_list = it as MutableList<CartModel>?
      initializeRecycler()
    })

    viewModel.updatesError().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
      longToast(requireContext(), "onFailure: " + it)
    })


    remove_back_button2.setOnClickListener {
      (activity as UpgradeActivity).popFragmentFromBackStack()
    }

    remove_addons_submit.setOnClickListener {
      if (selectedItem.size > 0) {
        for (item in selectedItem) {
          cart_list!!.remove(item)
        }
//                localStorage.setCartItem(cart_list!!)
        viewModel.updateCartItems(cart_list!!)
        updateRecycler(cart_list!!)
        (activity as UpgradeActivity).replaceFragment(
          RemoveAddonConfirmationFragment.newInstance(),
          REMOVE_ADDONS_CONFIRMATION_FRAGMENT
        )
      }
    }

  }

  private fun loadData() {
    viewModel.getCartItems()
  }

  private fun initializeRecycler() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    remove_recyclerDetails.apply {
      layoutManager = gridLayoutManager
    }
    updateRecycler(cart_list!!)
  }

  fun updateRecycler(list: List<CartModel>) {
    removeAddonsAdapter.addupdates(list)
    remove_recyclerDetails.adapter = removeAddonsAdapter
    removeAddonsAdapter.notifyDataSetChanged()
  }

  override fun addItemToCart(item: CartModel) {
    selectedItem.remove(item)
  }

  override fun removeItemFromCart(item: CartModel) {
    selectedItem.add(item)
  }

}
