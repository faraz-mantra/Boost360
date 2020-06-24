package com.catlogservice.ui.information

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.catlogservice.R
import com.catlogservice.base.AppBaseFragment
import com.catlogservice.databinding.FragmentServiceInformationBinding
import com.catlogservice.viewmodel.ServiceViewModel
import kotlinx.android.synthetic.main.fragment_service_information.*

class ServiceInformationFragment : AppBaseFragment<FragmentServiceInformationBinding, ServiceViewModel>() {

    companion object {
        fun newInstance(): ServiceInformationFragment {
            return ServiceInformationFragment()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_service_information
    }

    override fun getViewModelClass(): Class<ServiceViewModel> {
        return ServiceViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        binding?.serverBy?.type?.hint = "e.g. Served by"
        binding?.serverBy?.value?.hint = "Mr. Shruti"
        binding?.duration?.type?.hint = "e.g Duration"
        binding?.duration?.value?.hint = "30 min"
        binding?.method?.type?.hint = "e.g. Method"
        binding?.method?.value?.hint = "Dry"

        setOnClickListener(binding?.cbFacebookPage, binding?.cbGoogleMerchantCenter, binding?.cbTwitterPage)

    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.cbFacebookPage -> {
                if (cbFacebookPage.isChecked) {
                    cbFacebookPage.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(baseActivity, R.color.colorAccent))
                }else{
                    cbFacebookPage.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(baseActivity, R.color.placeholder_bg))
                }
            }
            binding?.cbGoogleMerchantCenter -> {
                if (cbGoogleMerchantCenter.isChecked) {
                    cbGoogleMerchantCenter.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(baseActivity, R.color.colorAccent))
                }else{
                  cbGoogleMerchantCenter.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(baseActivity, R.color.placeholder_bg))
                }
            }
            binding?.cbTwitterPage -> {
                if (cbTwitterPage.isChecked) {
                    cbTwitterPage.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(baseActivity, R.color.colorAccent))
                } else {
                    cbTwitterPage.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(baseActivity, R.color.placeholder_bg))
                }
            }

        }
    }
}