package com.festive.poster.ui.promoUpdates.bottomSheet

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.appevents.suggestedevents.ViewOnClickListener
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.BsheetPromoteUsingBrandedUpdateTemplatesBinding
import com.festive.poster.models.FeaturePurchaseUiModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.utils.MarketPlaceUtils
import com.festive.poster.viewmodels.UpdateStudioPurchaseViewModel
import com.framework.base.BaseActivity
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.rest.NetworkResult
import com.framework.utils.showSnackBarNegative
import com.framework.utils.toArrayList

class PromoteBrandedUpdateTemplatesBottomSheet :
    BaseBottomSheetDialog<BsheetPromoteUsingBrandedUpdateTemplatesBinding,
            UpdateStudioPurchaseViewModel>(),RecyclerItemClickListener {

    private var adapter: AppBaseRecyclerViewAdapter<FeaturePurchaseUiModel>?=null
    private var purchaseList: ArrayList<FeaturePurchaseUiModel>?=null

    companion object {

        @JvmStatic
        fun newInstance(): PromoteBrandedUpdateTemplatesBottomSheet {
            val bundle = Bundle().apply {}
            val fragment = PromoteBrandedUpdateTemplatesBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_promote_using_branded_update_templates
    }

    override fun getViewModelClass(): Class<UpdateStudioPurchaseViewModel> {
        return UpdateStudioPurchaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.rivCloseBottomSheet?.setOnClickListener { dismiss() }

        observeApis()

        setOnClickListener(binding?.btnViewPackDetails)
    }

    private fun observeApis() {
        viewModel?.featurePurchaseData?.observe(this){
            when(it){
                is NetworkResult.Loading->{
                    binding?.progressBar?.visible()
                    binding!!.dataLayout!!.gone()
                }
                is NetworkResult.Success->{
                    binding?.progressBar?.gone()
                    binding!!.dataLayout!!.visible()
                    if (viewModel?.doesUserHavePurchasedAnything == true){
                        BusinessPromotionAddToCartBottomSheet.newInstance()
                            .show(parentFragmentManager,BusinessPromotionAddToCartBottomSheet::class.java.canonicalName)
                        dismiss()
                        return@observe
                    }
                    setPurchaseList(it.data)
                }
                is NetworkResult.Error->{
                    binding?.progressBar?.gone()
                    binding!!.dataLayout!!.gone()
                    showSnackBarNegative(requireActivity(),getString(R.string.something_went_wrong))
                }
            }
        }
    }

    private fun setPurchaseList(data: List<FeaturePurchaseUiModel>?) {
        data?:return
        purchaseList = data.toArrayList()
        purchaseItemClicked(0)
        adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,
            purchaseList!!,this)
        binding!!.rvPacks.adapter=adapter
        binding!!.rvPacks.layoutManager = LinearLayoutManager(requireActivity())
        binding!!.rvPacks.addItemDecoration(DividerItemDecoration(requireActivity(),
        DividerItemDecoration.VERTICAL))
    }



    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.btnViewPackDetails->{
                val selectedItem = purchaseList?.find { it.isSelected }
                if (selectedItem?.isPack == true){
                    MarketPlaceUtils.initiateAddonMarketplace(
                        sessionManager!!, context = requireActivity(), packId = selectedItem.code)
                }else{
                    MarketPlaceUtils.initiateAddonMarketplace(
                        sessionManager!!, context = requireActivity(), buyItemKey = selectedItem?.code)
                }
            }
           /* binding?.linearAdvancedWrapper -> {
                binding?.radioAdvanced?.isChecked = true
                binding?.radioClassic?.isChecked = false
                binding?.radioBuyThisOnly?.isChecked = false
                binding?.tvAdvanceTitle?.setTextColor(color4a4a4a)
                binding?.tvClassicTitle?.setTextColor(color888888)
                binding?.tvBuyOnlyTitle?.setTextColor(color888888)
                binding?.btnViewPackDetails?.text = getString(R.string.view_pack_details)
            }
            binding?.linearClassicWrapper -> {
                binding?.radioAdvanced?.isChecked = false
                binding?.radioClassic?.isChecked = true
                binding?.radioBuyThisOnly?.isChecked = false
                binding?.tvAdvanceTitle?.setTextColor(color888888)
                binding?.tvClassicTitle?.setTextColor(color4a4a4a)
                binding?.tvBuyOnlyTitle?.setTextColor(color888888)
                binding?.btnViewPackDetails?.text = getString(R.string.view_pack_details)
            }
            binding?.linearBuyThisWrapper -> {
                binding?.radioAdvanced?.isChecked = false
                binding?.radioClassic?.isChecked = false
                binding?.radioBuyThisOnly?.isChecked = true
                binding?.tvAdvanceTitle?.setTextColor(color888888)
                binding?.tvClassicTitle?.setTextColor(color888888)
                binding?.tvBuyOnlyTitle?.setTextColor(color4a4a4a)
                binding?.btnViewPackDetails?.text = getString(R.string.add_and_go_to_cart)
            }*/
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

        when(actionType){
            RecyclerViewActionType.PURCHASE_ITEM_CLICKED.ordinal->{
                purchaseItemClicked(position)
            }
        }
    }

    private fun purchaseItemClicked(position: Int) {
        purchaseList?:return
        purchaseList!!.forEach {
            it.isSelected=false
        }
        val selectedItem = purchaseList!![position]
        selectedItem.isSelected=true

        if (selectedItem.isPack){
            binding?.btnViewPackDetails?.text = getString(R.string.view_pack_details)
        }else{
            binding?.btnViewPackDetails?.text=getString(R.string.add_and_go_to_cart)
        }
        adapter?.notifyDataSetChanged()
    }
}