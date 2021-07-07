package com.nowfloats.education.unlockfeature

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.boost.upgrades.UpgradeActivity
import com.nowfloats.Login.UserSessionManager
import com.nowfloats.education.model.UnlockFeatureModel
import com.nowfloats.util.Constants
import com.thinksity.R
import com.thinksity.databinding.UnlockFeatureBinding


class UnlockFeatureFragment(
  private val session: UserSessionManager?,
  private val unlockFeatureModel: UnlockFeatureModel
) : Fragment() {

  private lateinit var binding: UnlockFeatureBinding

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = UnlockFeatureBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setHeader(view)

    binding.unlockFeature = unlockFeatureModel

    binding.buyItem.setOnClickListener {
      initiateBuyFromMarketplace()
    }
  }

  private fun initiateBuyFromMarketplace() {
    session?.let {
      val progressDialog = ProgressDialog(requireActivity())
      val status = "Loading. Please wait..."
      progressDialog.setMessage(status)
      progressDialog.setCancelable(false)
      progressDialog.show()
      val intent = Intent(requireActivity(), UpgradeActivity::class.java)
      intent.putExtra("expCode", it.fP_AppExperienceCode)
      intent.putExtra("fpName", it.fpName)
      intent.putExtra("fpid", it.fpid)
      intent.putExtra("loginid", it.userProfileId)
      intent.putStringArrayListExtra("userPurchsedWidgets", Constants.StoreWidgets)
      intent.putExtra("fpTag", it.fpTag)
      if (it.fpEmail != null) {
        intent.putExtra("email", it.fpEmail)
      } else {
        intent.putExtra("email", getString(R.string.ria_customer_mail))
      }
      if (it.fpPrimaryContactNumber != null) {
        intent.putExtra("mobileNo", it.fpPrimaryContactNumber)
      } else {
        intent.putExtra("mobileNo", getString(R.string.ria_customer_number))
      }
      intent.putExtra("profileUrl", it.fpLogo)
      intent.putExtra("buyItemKey", unlockFeatureModel.buyItemKey)
      startActivity(intent)
      Handler().postDelayed({
        progressDialog.dismiss()
        requireActivity().finish()
      }, 1000)
    }
  }

  fun setHeader(view: View) {
    val backButton: LinearLayout = view.findViewById(R.id.back_button)
    val rightIcon: ImageView = view.findViewById(R.id.right_icon)
    val title: TextView = view.findViewById(R.id.title)
    title.text = unlockFeatureModel.titleFeatureName
    rightIcon.setImageResource(R.drawable.ic_delete_white_outerline)
    rightIcon.visibility = View.GONE
    backButton.setOnClickListener { requireActivity().onBackPressed() }
  }

  companion object {
    fun newInstance(session: UserSessionManager?, unlockFeatureModel: UnlockFeatureModel) =
      UnlockFeatureFragment(session, unlockFeatureModel)
  }
}