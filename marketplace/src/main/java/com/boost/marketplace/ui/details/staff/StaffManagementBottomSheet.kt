package com.boost.marketplace.ui.details.staff

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.boost.marketplace.databinding.BottomSheetEmailConfirmationBinding
import com.boost.marketplace.interfaces.DetailsFragmentListener
import com.boost.marketplace.ui.details.FeatureDetailsViewModel
import com.bumptech.glide.Glide
import com.framework.analytics.SentryController
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_feature_details.*
import kotlinx.android.synthetic.main.bottom_sheet_staff_management.*

class StaffManagementBottomSheet(var listener: DetailsFragmentListener): BaseBottomSheetDialog<BottomSheetEmailConfirmationBinding, FeatureDetailsViewModel>() {

    var unit_count = 1
    var addonDetails: FeaturesModel? = null

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_staff_management
    }

    override fun getViewModelClass(): Class<FeatureDetailsViewModel> {
        return FeatureDetailsViewModel::class.java
    }

    override fun onCreateView() {
        addonDetails = Gson().fromJson<FeaturesModel>(
            requireArguments().getString("addonDetails"),
            object : TypeToken<FeaturesModel>() {}.type
        )
        Glide.with(requireActivity())
            .load(addonDetails!!.primary_image)
            .into(addons_icon)
        viewModel!!.setApplicationLifecycle(requireActivity().application, requireActivity())
        initView()
        loadData()
        initMvvm()
    }

    private fun initMvvm() {

    }

    private fun loadData() {

    }

    private fun initView() {
        addons_title.setText(addonDetails!!.name)
        if(arguments!!.getBoolean("addedToCart")){
            btn_Use_this_feature.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.added_to_cart_grey
            )
            btn_Use_this_feature.setTextColor(getResources().getColor(R.color.tv_color_BB))
            btn_Use_this_feature.setText("Added $unit_count staff profiles to cart")
            btn_Use_this_feature.isClickable = false
        }
        riv_close_bottomSheet.setOnClickListener {
            dismiss()
        }
        no_of_units_edit_inc.setOnClickListener {
            if(unit_count<99){
                unit_count+=1
                btn_Use_this_feature.setText("Add $unit_count staff profiles to cart")
                no_of_units.setText(if(unit_count<10)"0"+unit_count.toString() else unit_count.toString())
                error_layout.visibility = View.GONE
            } else {
                error_layout.visibility = View.VISIBLE
            }
        }

        no_of_units_edit_dsc.setOnClickListener {
            if(unit_count>=2){
                unit_count-=1
                btn_Use_this_feature.setText("Add $unit_count staff profiles to cart")
                no_of_units.setText(if(unit_count<10)"0"+unit_count.toString() else unit_count.toString())
            }
            error_layout.visibility = View.GONE
        }

        no_of_units.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val count = if(!s.toString().isNullOrEmpty()) s.toString() else "1"
                var n = count.toInt()
                if(unit_count == n || s.toString().isNullOrEmpty())
                    return
                try {
                    if(n>99){
                        unit_count = 99
//                        no_of_units.setText(unit_count.toString())
                        error_layout.visibility = View.VISIBLE
                    }else{
                        unit_count = n
//                        no_of_units.setText(unit_count.toString())
                        error_layout.visibility = View.GONE
                    }
                    btn_Use_this_feature.setText("Add $unit_count staff profiles to cart")
                } catch (nfe: NumberFormatException) {
                    SentryController.captureException(nfe)
                }
            }

        })

        btn_Use_this_feature.setOnClickListener {
            viewModel!!.addItemToCart1(addonDetails!!, requireActivity())
            btn_Use_this_feature.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.added_to_cart_grey
            )
            btn_Use_this_feature.setTextColor(getResources().getColor(R.color.tv_color_BB))
            btn_Use_this_feature.setText("Added $unit_count staff profiles to cart")
            btn_Use_this_feature.isClickable = false
            listener.itemAddedToCart(true)
        }
    }

}