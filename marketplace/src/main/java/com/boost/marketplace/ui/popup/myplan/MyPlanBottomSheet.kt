package com.boost.marketplace.ui.popup.myplan

import android.app.Application
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.boost.marketplace.databinding.BottomSheetMyplanBinding
import com.boost.marketplace.infra.utils.DeepLink.Companion.getScreenType
import com.boost.marketplace.ui.My_Plan.MyCurrentPlanViewModel
import com.boost.marketplace.ui.details.FeatureDetailsActivity
import com.boost.marketplace.ui.details.call_track.CallTrackingActivity
import com.boost.marketplace.ui.details.domain.CustomDomainActivity
import com.boost.marketplace.ui.popup.call_track.FindingNumberLoaderBottomSheet
import com.bumptech.glide.Glide
import com.framework.analytics.SentryController
import com.framework.base.BaseBottomSheetDialog
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import com.framework.utils.DateUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_feature_details.*


class MyPlanBottomSheet :
    BaseBottomSheetDialog<BottomSheetMyplanBinding, MyCurrentPlanViewModel>() {

    val dialogCard = FindingNumberLoaderBottomSheet()
    lateinit var singleAddon: FeaturesModel
    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
    var fpid: String? = null

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_myplan
    }

    override fun getViewModelClass(): Class<MyCurrentPlanViewModel> {
        return MyCurrentPlanViewModel::class.java
    }

    override fun onCreateView() {

        viewModel?.setApplicationLifecycle(Application(), this)
        viewModel = ViewModelProviders.of(this)
            .get(MyCurrentPlanViewModel::class.java)
        fpid = requireArguments().getString("fpid")
        singleAddon = Gson().fromJson<FeaturesModel>(
            requireArguments().getString("bundleData"),
            object : TypeToken<FeaturesModel>() {}.type
        )
        binding?.addonsTitle?.text = singleAddon.name
        if (singleAddon.feature_code == "STAFFPROFILE" || singleAddon.feature_code == "STAFFPROFILE15"
            || singleAddon.feature_code == "FACULTY" || singleAddon.feature_code == "OUR-TOPPERS"
            || singleAddon.feature_code == "MEMBERSHIP" || singleAddon.feature_code == "DOCTORBIO"
            || singleAddon.feature_code == "BOOSTKEYBOARD" || singleAddon.feature_code == "BOOKING-ENGINE"
            || singleAddon.feature_code == "APPOINTMENTENGINE"
        ) {
            binding!!.btnUseThisFeature.visibility = View.VISIBLE
        } else {
            binding!!.btnUseThisFeature.visibility = View.GONE
        }
        binding?.addonsDesc?.text = singleAddon.description_title

        if (singleAddon.is_premium.equals(false)) {
            binding?.cslayout?.visibility = View.GONE
        }

        val date1: String? =
            DateUtils.parseDate(
                singleAddon.activatedDate,
                DateUtils.FORMAT_SERVER_DATE1,
                DateUtils.FORMAT1_DD_MM_YYYY
            )
        binding?.title3?.text = date1

        val date: String? =
            DateUtils.parseDate(
                singleAddon.expiryDate,
                DateUtils.FORMAT_SERVER_DATE1,
                DateUtils.FORMAT1_DD_MM_YYYY
            )
        binding?.title4?.text = date

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
        // featureEdgeCase(featureState, actionRequired)
        loadData()
        initMVVM()
    }

    private fun loadData() {
        try {
            fpid?.let {
                singleAddon.feature_code?.let { it1 ->
                    viewModel?.edgecases(
                        it,
                        "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21", it1
                    )
                }
            }
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
    }

    private fun initMVVM() {
        viewModel?.edgecaseResult()?.observe(this, androidx.lifecycle.Observer {

            if (it.Result.FeatureDetails !=null && it.Result.ActionNeeded.ActionNeeded!=null){
                val actionRequired = it.Result.ActionNeeded.ActionNeeded
                val featureState = it.Result.FeatureDetails.FeatureState
                featureEdgeCase(actionRequired, featureState)
            }

        })

        viewModel?.updatesLoader()?.observe(this, androidx.lifecycle.Observer {
            if (it) {
                dialogCard.show(
                    childFragmentManager,
                    FindingNumberLoaderBottomSheet::class.java.name
                )
                binding?.main?.visibility=View.GONE
            } else {
                dialogCard.dismiss()
                binding?.main?.visibility=View.VISIBLE
            }
        })
    }

    fun getAccessToken(): String {
        return UserSessionManager(requireActivity()).getAccessTokenAuth()?.barrierToken() ?: ""
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
        val screenType = singleAddon.feature_code?.let { getScreenType(it) }
        if (screenType.isNullOrEmpty().not()) {
            try {
                val intent = Intent(
                    requireActivity(),
                    Class.forName("com.dashboard.controller.DeepLinkTransActivity")
                )
                intent.putExtra("SCREEN_TYPE", screenType)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Toasty.success(requireContext(), "Coming Soon...", Toast.LENGTH_SHORT, true).show();
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

    private fun chooseDomain() {
        val intent = Intent(requireActivity(), CustomDomainActivity::class.java)
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
        intent.putExtra("bundleData", Gson().toJson(singleAddon))
        intent.putExtra("email", "ria@nowfloats.com")
        intent.putExtra("mobileNo", "9160004303")
        intent.putExtra("itemId", singleAddon.boost_widget_key)
        startActivity(intent)
    }

    private fun chooseVMN() {
        val intent = Intent(requireActivity(), CallTrackingActivity::class.java)
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
        intent.putExtra("bundleData", Gson().toJson(singleAddon))
        intent.putExtra("email", "ria@nowfloats.com")
        intent.putExtra("mobileNo", "9160004303")
        intent.putExtra("itemId", singleAddon.boost_widget_key)
        startActivity(intent)
    }

    fun featureEdgeCase(actionRequired: Int, featureState: Int) {

         if(actionRequired == 1 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4
            || featureState == 5 || featureState == 6)) {
            binding?.edgeCasesLayout?.visibility = View.VISIBLE
            binding?.btn1?.visibility = View.VISIBLE
            binding?.btn1?.text = "Contact Support"
            binding?.btn1?.setOnClickListener {
                featuredetails()
            }
            binding?.edgeCasesLayout?.setBackgroundResource(R.drawable.rounded_border_red_white_bg)
            binding?.edgeCaseTitle?.setText("Action Required")
            binding?.edgeCaseTitle?.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
            binding?.edgeCaseTitle?.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_error_red,
                0,
                0,
                0
            )
            binding?.edgeCaseDesc?.setText("You need to take action to activate this feature.")
        }

        else if (actionRequired == 2 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4
                     || featureState == 5 || featureState == 6))  {
            binding?.edgeCasesLayout?.visibility = View.VISIBLE
            binding?.btn1?.visibility = View.VISIBLE
            binding?.btn1?.text = "Choose Domain"
            binding?.btn1?.setOnClickListener {
                chooseDomain()
            }
            binding?.edgeCasesLayout?.setBackgroundResource(R.drawable.rounded_border_red_white_bg)
            binding?.edgeCaseTitle?.setText("Action Required")
            binding?.edgeCaseTitle?.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
            binding?.edgeCaseTitle?.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_error_red,
                0,
                0,
                0
            )
            binding?.edgeCaseDesc?.setText("You need to take action to activate this feature.")
        }

        else if(actionRequired == 3 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4
                     || featureState == 5 || featureState == 6))  {
            binding?.edgeCasesLayout?.visibility = View.VISIBLE
            binding?.btn1?.visibility = View.VISIBLE
            binding?.btn1?.text = "Choose VMN"
            binding?.btn1?.setOnClickListener {
             //   chooseVMN()
                Toasty.success(requireContext(), "Coming Soon...", Toast.LENGTH_SHORT, true).show();
            }
            binding?.edgeCasesLayout?.setBackgroundResource(R.drawable.rounded_border_red_white_bg)
            binding?.edgeCaseTitle?.setText("Action Required")
            binding?.edgeCaseTitle?.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
            binding?.edgeCaseTitle?.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_error_red,
                0,
                0,
                0
            )
            binding?.edgeCaseDesc?.setText("You need to take action to activate this feature.")
        }

        else if (actionRequired == 4 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4
                     || featureState == 5 || featureState == 6))  {
            binding?.edgeCasesLayout?.visibility = View.VISIBLE
            binding?.btn1?.visibility = View.VISIBLE
            binding?.btn1?.text = "Choose Email"
            binding?.btn1?.setOnClickListener {
                featuredetails()
            }
            binding?.edgeCasesLayout?.setBackgroundResource(R.drawable.rounded_border_red_white_bg)
            binding?.edgeCaseTitle?.setText("Action Required")
            binding?.edgeCaseTitle?.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
            binding?.edgeCaseTitle?.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_error_red,
                0,
                0,
                0
            )
            binding?.edgeCaseDesc?.setText("You need to take action to activate this feature.")
        }
        else if (actionRequired == 0 && featureState == 1) {
            binding?.edgeCasesLayout?.visibility = View.VISIBLE
            binding?.edgeCaseHyperlink?.visibility=View.GONE
            binding?.edgeCasesLayout?.setBackgroundResource(R.drawable.rounded_border_green_white_bg)
            binding?.edgeCaseTitle?.setText("Feature is currently active")
            binding?.edgeCaseTitle?.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )
            binding?.edgeCaseTitle?.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_checked,
                0,
                0,
                0
            )
//            binding?.edgeCaseDesc?.setText(
//                "Feature validity expiring on Aug 23, 2021. You\n" +
//                        "can extend validity by renewing it for a\n" +
//                        "longer duration."
//            )

        }
        else if (actionRequired == 0 && featureState == 3 || featureState == 4 || featureState == 5 || featureState == 6) {
            binding?.edgeCasesLayout?.visibility = View.VISIBLE
            binding?.edgeCaseHyperlink?.visibility=View.GONE
            edge_cases_layout.setBackgroundResource(R.drawable.rounded_border_skyblue_white_bg)
            edge_case_title.setText("Syncing information")
            edge_case_title.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_blue2
                )
            )
            edge_case_title.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_sync_blue,
                0,
                0,
                0
            )
            binding?.edgeCaseDesc?.setText(
                "We are working on syncing your information for this feature.it may take some time to get updated"
            )
            binding?.edgeCaseDesc?.visibility=View.VISIBLE
        }

    }
}