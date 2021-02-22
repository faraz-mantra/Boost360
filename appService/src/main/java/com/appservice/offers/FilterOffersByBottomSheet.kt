package com.appservice.offers

import android.view.View
import android.widget.RadioButton
import com.appservice.AppServiceApplication
import com.appservice.R
import com.appservice.databinding.BottomSheetSortOffersListingBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class FilterOffersByBottomSheet : BaseBottomSheetDialog<BottomSheetSortOffersListingBinding, BaseViewModel>() {

    private var filterType: Map<String, Int>? = mapOf()
    var onClicked: (value: Map<String, Int>) -> Unit = { }

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_sort_offers_listing
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    companion object {
        private const val CREATED_ON = 0
        private const val ACTIVE = 1
        private const val ASCENDING = 0
        private const val DESCENDING = 1
        val filters: Map<String, Int> = mapOf(
                Pair(AppServiceApplication.instance.getString(R.string.latest_on_top), CREATED_ON),
                Pair(AppServiceApplication.instance.getString(R.string.latest_on_top), ACTIVE),
                Pair(AppServiceApplication.instance.getString(R.string.latest_on_top), ASCENDING),
                Pair(AppServiceApplication.instance.getString(R.string.latest_on_top), DESCENDING)
        )
    }

    override fun onCreateView() {
        setOnClickListener(binding?.btnCancel, binding?.btnApply, binding?.radioGroupSorting)
        binding?.radioGroupSorting?.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: RadioButton = group.findViewById(checkedId)


        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnApply -> {
//                onClicked()
                dismiss()
            }
            binding?.btnCancel -> {
                dismiss()
            }
        }
    }
}