package com.inventoryorder.ui.order

import android.os.Bundle
import android.view.View
import com.inventoryorder.databinding.FragmentInventoryOrderDetailBinding
import com.inventoryorder.model.InventoryOrderDetailsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter

class InventoryOrderDetailFragment : BaseOrderFragment<FragmentInventoryOrderDetailBinding>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): InventoryOrderDetailFragment {
            val fragment = InventoryOrderDetailFragment()
            fragment.arguments = bundle
            return fragment
        }
    }


    override fun onCreateView() {
        super.onCreateView()

        setAdapter()

        setOnClickListener(binding?.ivClose)
    }

    override fun onClick(v: View) {
        super.onClick(v)

        when (v) {

            binding?.ivClose -> {
                baseActivity.finish()
            }
        }


    }

    private fun setAdapter() {
        val list = InventoryOrderDetailsModel().getOrderDetails()
        val adapter = AppBaseRecyclerViewAdapter(baseActivity, list)
        binding?.recyclerViewOrderDetails?.adapter = adapter
    }


}