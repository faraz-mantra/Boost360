package com.boost.marketplace.ui.My_Plan

import android.view.View
import android.widget.Toast
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.boost.marketplace.databinding.BottomSheetMyplanBinding
import com.bumptech.glide.Glide
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyPlanBottomSheet: BaseBottomSheetDialog<BottomSheetMyplanBinding, BaseViewModel>() {


    lateinit var singleAddon: FeaturesModel

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_myplan
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        singleAddon = Gson().fromJson<FeaturesModel>(requireArguments().getString("bundleData"), object : TypeToken<FeaturesModel>() {}.type)
        binding?.addonsTitle?.text = singleAddon.name
        binding?.addonsDesc?.text=singleAddon.description_title
        Glide.with(baseActivity).load(singleAddon.primary_image).into(binding!!.addonsIcon)
        dialog.behavior.isDraggable = true
        setOnClickListener(
            binding?.btnUseThisFeature,
            binding?.btnFeatureDetails,
            binding?.rivCloseBottomSheet
        )
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnUseThisFeature -> {
                Usefeature()
            }
            binding?.btnFeatureDetails-> {
                featuredetails()
            }
            binding?.rivCloseBottomSheet, binding?.rivCloseBottomSheet -> {
                dismiss()
            }
        }
    }

    private fun Usefeature() {
        Toast.makeText(context, "Clicked on Usefeature", Toast.LENGTH_SHORT).show()
    }

    private fun featuredetails() {

        Toast.makeText(context, "Clicked on featuredetails", Toast.LENGTH_SHORT).show()



    }
}