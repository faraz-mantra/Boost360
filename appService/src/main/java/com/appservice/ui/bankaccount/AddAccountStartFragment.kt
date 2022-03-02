package com.appservice.ui.bankaccount

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentAddAccountStartBinding
import com.framework.constants.SupportVideoType
import com.framework.models.BaseViewModel
import com.framework.views.zero.old.AppFragmentZeroCase
import com.framework.views.zero.old.AppOnZeroCaseClicked
import com.framework.views.zero.old.AppRequestZeroCaseBuilder
import com.framework.views.zero.old.AppZeroCases

class AddAccountStartFragment : AppBaseFragment<FragmentAddAccountStartBinding, BaseViewModel>(), AppOnZeroCaseClicked {
  private val TAG = "AddAccountStartFragment"
  private lateinit var appZeroCases: AppFragmentZeroCase

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AddAccountStartFragment {
      val fragment = AddAccountStartFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_add_account_start
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.closeBtn, binding?.startBtn)
    (baseActivity as? AccountFragmentContainerActivity)?.setToolbarTitleNew(getString(R.string.my_bank_acccount))
    appZeroCases = AppRequestZeroCaseBuilder(AppZeroCases.MY_BANK_ACCOUNT, this, baseActivity).getRequest().build()
    addFragmentReplace(R.id.child_container, appZeroCases, false)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 202) {
      val output = Intent()
      output.putExtra(IntentConstant.USER_BANK_DETAIL.name, data?.getSerializableExtra(IntentConstant.USER_BANK_DETAIL.name))
      baseActivity.setResult(AppCompatActivity.RESULT_OK, output)
      baseActivity.finish()
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.closeBtn -> baseActivity.onNavPressed()
      binding?.startBtn -> arguments?.let {
        startFragmentAccountActivity(FragmentType.BANK_ACCOUNT_DETAILS, it, isResult = true, requestCode = 202)
      }
    }
  }

  override fun primaryButtonClicked() {
    arguments?.let {
      startFragmentAccountActivity(FragmentType.BANK_ACCOUNT_DETAILS, it, isResult = true, requestCode = 202)
    }
  }

  override fun secondaryButtonClicked() {
    //Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
    try {
      startActivity(Intent(baseActivity, Class.forName("com.onboarding.nowfloats.ui.supportVideo.SupportVideoPlayerActivity"))
          .putExtra(com.onboarding.nowfloats.constant.IntentConstant.SUPPORT_VIDEO_TYPE.name, SupportVideoType.PAYMENT_GATEWAY.value))
    } catch (e: ClassNotFoundException) {
      e.printStackTrace()
    }
  }

  override fun ternaryButtonClicked() {
    Log.i(TAG, "ternaryButtonClicked: ")
  }

  override fun appOnBackPressed() {

  }

}
