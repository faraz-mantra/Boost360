package com.boost.marketplace.ui.popup.myplan

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.boost.marketplace.databinding.BottomSheetMyplanBinding
import com.boost.marketplace.infra.utils.DeepLink.Companion.getScreenType
import com.boost.marketplace.ui.details.FeatureDetailsActivity
import com.bumptech.glide.Glide
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.utils.DateUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_feature_details.*


class MyPlanBottomSheet : BaseBottomSheetDialog<BottomSheetMyplanBinding, BaseViewModel>() {

    lateinit var singleAddon: FeaturesModel

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_myplan
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        singleAddon = Gson().fromJson<FeaturesModel>(
            requireArguments().getString("bundleData"),
            object : TypeToken<FeaturesModel>() {}.type
        )
        binding?.addonsTitle?.text = singleAddon.name
        if (singleAddon.feature_code=="STAFFPROFILE" || singleAddon.feature_code=="STAFFPROFILE15"
            || singleAddon.feature_code=="FACULTY" || singleAddon.feature_code=="OUR-TOPPERS"
            || singleAddon.feature_code=="MEMBERSHIP" || singleAddon.feature_code=="DOCTORBIO"
            || singleAddon.feature_code=="BOOSTKEYBOARD" || singleAddon.feature_code=="BOOKING-ENGINE"
            || singleAddon.feature_code=="APPOINTMENTENGINE")
        {
            binding!!.btnUseThisFeature.visibility=View.VISIBLE
        }
        else{
            binding!!.btnUseThisFeature.visibility=View.GONE
        }
        binding?.addonsDesc?.text = singleAddon.description_title

        if (singleAddon.is_premium.equals(false)){
            binding?.cslayout?.visibility=View.GONE
        }

        val date1: String? =
            DateUtils.parseDate(singleAddon.activatedDate, DateUtils.FORMAT_SERVER_DATE1, DateUtils.FORMAT1_DD_MM_YYYY)
        binding?.title3?.text= date1

        val date: String? =
            DateUtils.parseDate(singleAddon.expiryDate, DateUtils.FORMAT_SERVER_DATE1, DateUtils.FORMAT1_DD_MM_YYYY)
        binding?.title4?.text= date

        Glide.with(baseActivity).load(singleAddon.primary_image).into(binding!!.addonsIcon)
//        if (singleAddon.featureState == 1) {
//            binding!!.imageView3.setImageResource(R.drawable.ic_active)
//            binding!!.actionRequired.visibility=View.GONE
//            binding!!.actionText.visibility=View.GONE
//        }
//        else {
////            binding!!.imageView3.setImageResource(R.drawable.ic_action_req)
//            binding!!.actionRequired.visibility=View.VISIBLE
//            binding!!.actionText.visibility=View.VISIBLE
//            binding!!.actionText.text="Please activate this feature by going to ${singleAddon.name} and provide the missing details."
////            binding!!.txtMessage.visibility=View.GONE
////            binding!!.paidSingleDummyView.visibility=View.GONE
//            binding!!.imageView3.visibility=View.GONE
//        }

        dialog.behavior.isDraggable = true
        setOnClickListener(
            binding?.btnUseThisFeature,
            binding?.btnFeatureDetails,
            binding?.rivCloseBottomSheet
        )
        featureEdgeCase()
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnUseThisFeature -> {
                Usefeature()
            }
            binding?.btnFeatureDetails -> {
                featuredetails()
            }
            binding?.rivCloseBottomSheet, binding?.rivCloseBottomSheet -> {
                dismiss()
            }
        }
    }

    private fun Usefeature() {
        val screenType= singleAddon.feature_code?.let { getScreenType(it) }
        if (screenType.isNullOrEmpty().not()){
        try {
           val intent = Intent(requireActivity(), Class.forName("com.dashboard.controller.DeepLinkTransActivity"))
           intent.putExtra("SCREEN_TYPE",screenType)
           startActivity(intent)
       } catch(e:Exception){
           e.printStackTrace()
       }
        }else{
            Toasty.error(requireContext(), "Coming Soon...", Toast.LENGTH_SHORT, true).show();
        }
    }

    private fun featuredetails() {
        val intent = Intent(requireActivity(), FeatureDetailsActivity::class.java)
        intent.putExtra("fpid", UserSessionManager(requireActivity()).fPID)
        intent.putExtra("expCode", UserSessionManager(requireActivity()).fP_AppExperienceCode)
        intent.putExtra(
            "accountType",
            UserSessionManager(requireActivity()).getFPDetails("GET_FP_DETAILS_CATEGORY")
        )
        intent.putStringArrayListExtra(
            "userPurchsedWidgets",
            ArrayList(UserSessionManager(requireActivity()).getStoreWidgets())
        )
        intent.putExtra("email", "ria@nowfloats.com")
        intent.putExtra("mobileNo", "9160004303")
        intent.putExtra("itemId", singleAddon.boost_widget_key)
        startActivity(intent)
    }

    fun featureEdgeCase() {
        val edgeState = singleAddon.featureState
        if (edgeState==7) {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_red_white_bg)
                edge_case_title.setText("Action Required")
                edge_case_title.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_error_red,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText("You need to take action to activate this feature.")
                edge_case_desc.setText("There is an internal error inside Boost 360. We are working to resolve this issue.")
            }
        else if (edgeState==7) {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_red_white_bg)
                edge_case_title.setText("Something went wrong!")
                edge_case_title.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_error_red,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText("There is an internal error inside Boost 360. We are working to resolve this issue.")

            }
        else if (edgeState==1) {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_green_white_bg)
                edge_case_title.setText("Feature is currently active")
                edge_case_title.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_checked,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText(
                    "Feature validity expiring on Aug 23, 2021. You\n" +
                            "can extend validity by renewing it for a\n" +
                            "longer duration."
                )

            }
        else if (edgeState==1) {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_red_white_bg)
                edge_case_title.setText("Feature expired on Aug 23, 2021")
                edge_case_title.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_error_red,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText(
                    "You need to renew this feature to continue\n" +
                            "using a custom domain. Your domain may be\n" +
                            "lost if you don’t renew it."
                )
            }
        else if (edgeState==1) {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_skyblue_white_bg)
                edge_case_title.setText("Syncing information")
                edge_case_title.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_blue2))
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_sync_blue,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText("We are working on syncing your information for this feature. It may take some time to get updated. Contact support for help.")
            }
        else if (edgeState==1) {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_green_white_bg)
                edge_case_title.setText("Auto renewal is turned on")
                edge_case_title.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_checked,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText("We are working on syncing your information for this feature. It may take some time to get updated. Contact support for help.")
            }
        else if (edgeState==1) {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_orange_white_bg)
                edge_case_title.setText("Feature is currently in cart. ")
                edge_case_title.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.common_text_color
                    )
                )
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_cart_black,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText("Feature is currently in cart. ")
            }
        else if (edgeState==1) {
                edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_green_white_bg)
                edge_case_title.setText("Feature is part of “Online Classic”")
                edge_case_title.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_checked,
                    0,
                    0,
                    0
                )
                edge_case_desc.setText("")
            }
        }
}