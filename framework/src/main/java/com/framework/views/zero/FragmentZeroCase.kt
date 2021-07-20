package com.framework.views.zero

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import com.framework.R
import com.framework.base.BaseFragment
import com.framework.databinding.FragmentZeroCaseBinding
import com.framework.extensions.gone
import com.framework.models.BaseViewModel
import com.framework.views.zero.ZeroCases.*
import java.io.Serializable

class FragmentZeroCase : BaseFragment<FragmentZeroCaseBinding, BaseViewModel>() {
  override fun getLayout(): Int {
    return R.layout.fragment_zero_case
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  companion object {
    var onZeroCaseClicked: OnZeroCaseClicked? = null

    class ZeroCaseBuilder {
      var arguments: Bundle? = null

      init {
        arguments = Bundle()
      }

      fun setTitle(title: String?): ZeroCaseBuilder {
        arguments!!.putString("title", title)
        return this
      }

      fun setDescription(description: String?): ZeroCaseBuilder {
        arguments!!.putString("description", description)
        return this
      }

      fun setToolBarTitle(title: String?): ZeroCaseBuilder {
        arguments!!.putString("toolbar", title)
        return this
      }

      fun setFooterText(footerText: String?): ZeroCaseBuilder {
        arguments!!.putString("footer", footerText)
        return this
      }

      fun setIcon(icon: Int): ZeroCaseBuilder {
        arguments!!.putInt("icon", icon)
        return this
      }

      fun setButton(button: ZeroCaseButton): ZeroCaseBuilder {
        arguments!!.putSerializable("buttons", button)
        return this
      }

      fun setListner(click: OnZeroCaseClicked): ZeroCaseBuilder {
        onZeroCaseClicked = click
        return this
      }

      fun build(): FragmentZeroCase {
        val fragment = FragmentZeroCase()
        fragment.arguments = arguments
        return fragment
      }
    }
  }

  override fun onCreateView() {
    setOnClickListener(binding?.cvPrimary, binding?.cvSecondary, binding?.cvTertiary)
    getData()
  }

  private fun getData() {
    val title = arguments?.getString("title", null)
    val description = arguments?.getString("description", null)
    val toolbarTitle = arguments?.getString("toolbar", null)
    val icon = arguments?.getInt("icon")
    val footerText = arguments?.getString("footer")
    val button = arguments?.getSerializable("buttons") as? ZeroCaseButton
    binding?.ctvTitle?.text = title
    binding?.ctvDesc?.text = description
    setToolbarTitle(toolbarTitle)
    if (icon != null)
      binding?.rivImageView?.setImageResource(icon)
    if (footerText != null) {
      binding?.ctvFooterText?.text = footerText
    } else {
      binding?.ctvFooterText?.gone()
    }
    if (button != null) {
      // primary button properties
      if (button.primaryButtonTitle != null) {
        if (button.primaryButtonBackground != null) {
          binding?.cvPrimary?.setCardBackgroundColor(ColorStateList.valueOf(button.primaryButtonBackground))
        } else binding?.cvPrimary?.setCardBackgroundColor(ColorStateList.valueOf(R.color.transparent!!))
        if (button.primaryButtonIconLeft != null)
          binding?.civIconLeft?.setImageResource(button.primaryButtonIconLeft)
        if (button.primaryButtonIconRight != null)
          binding?.civIconRight?.setImageResource(button.primaryButtonIconRight)
        binding?.ctvBtnPrimaryTitle?.text = button.primaryButtonTitle
      } else {
        binding?.cvPrimary?.gone()
      }
      // secondary button properties
      if (button.secondaryButtonTitle != null) {
        if (button.secondaryButtonBackground != null)
          binding?.cvSecondary?.setCardBackgroundColor(ColorStateList.valueOf(button.secondaryButtonBackground))
        else binding?.cvSecondary?.setCardBackgroundColor(ColorStateList.valueOf(R.color.transparent!!))
        binding?.ctvBtnSecondaryTitle?.text = button.secondaryButtonTitle
        if (button.secondaryButtonIconLeft != null)
          binding?.civIconLeftSecondary?.setImageResource(button.secondaryButtonIconLeft)
        if (button.secondaryButtonIconRight != null)
          binding?.civIconRightSecondary?.setImageResource(button.secondaryButtonIconRight)
      } else {
        binding?.cvSecondary?.gone()
      }
      // tertiary button properties
      if (button.tertiaryButtonTitle != null) {
        if (button.tertiaryButtonBackground != null)
          binding?.cvTertiary?.setCardBackgroundColor(ColorStateList.valueOf(button.tertiaryButtonBackground))
        else binding?.cvTertiary?.setCardBackgroundColor(ColorStateList.valueOf(R.color.transparent!!))
        binding?.ctvBtnTitleTertiary?.text = button.tertiaryButtonTitle
        if (button.tertiaryButtonIconLeft != null)
          binding?.civIconLeftTertiary?.setImageResource(button.tertiaryButtonIconLeft)
        if (button.tertiaryButtonIconRight != null)
          binding?.civIconRightTertiary?.setImageResource(button.tertiaryButtonIconRight)
      } else {
        binding?.cvTertiary?.gone()
      }
    } else {
      binding?.llBtnContainer?.visibility = View.GONE
    }

  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.cvPrimary -> {
        onZeroCaseClicked?.primaryButtonClicked()
      }
      binding?.cvSecondary -> {
        onZeroCaseClicked?.secondaryButtonClicked()
      }
      binding?.cvTertiary -> {
        onZeroCaseClicked?.ternaryButtonClicked()
      }
    }
  }
}

interface OnZeroCaseClicked {
  fun primaryButtonClicked()
  fun secondaryButtonClicked()
  fun ternaryButtonClicked()
}

data class ZeroCaseButton(
  val primaryButtonTitle: String? = null,
  val primaryButtonBackground: Int? = null,
  val secondaryButtonTitle: String? = null,
  val secondaryButtonBackground: Int? = null,
  val tertiaryButtonTitle: String? = null,
  val primaryButtonIconRight: Int? = null,
  val primaryButtonIconLeft: Int? = null,
  val secondaryButtonIconRight: Int? = null,
  val secondaryButtonIconLeft: Int? = null,
  val tertiaryButtonIconLeft: Int? = null,
  val tertiaryButtonIconRight: Int? = null,
  val tertiaryButtonBackground: Int? = null,
) : Serializable


enum class ZeroCases {
  SERVICES, STAFF, APPOINTMENT, PRODUCT ,MY_BANK_ACCOUNT
}

class ZeroCaseObjectBuilder(private var zeroCases: ZeroCases, private var context: Context) {
  fun getRequest(): FragmentZeroCase.Companion.ZeroCaseBuilder {
    return when (zeroCases) {
      MY_BANK_ACCOUNT -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle("You’ve not added your bank account yet.")
          .setDescription(
            "When enabled, your online sales will be automatically credited to your bank account within 48 hours."
          ).setIcon(R.drawable.ic_bank_zero_case)
          .setToolBarTitle("My Bank Account")
          .setFooterText("All customer payments will be sent to this account. The handling fee will depend on the payment method selected in the e-commerce/appointment setup.")
          .setListner(context as OnZeroCaseClicked)
          .setButton(ZeroCaseButton(primaryButtonTitle = "Add bank account", primaryButtonBackground = R.color.colorAccent))
      }
      PRODUCT -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle("Now is your chance to show off!")
          .setDescription("Make an impact and stand out with beautiful images of your business.").setFooterText(
            "Supported format: JPEG, PNG\n" +
                "Recommended dimension: Square (min. 800*800px)\n" +
                "Max file size: 800kb"
          ).setIcon(R.drawable.ic_services_zero_case)
          .setToolBarTitle("")
          .setListner(context as OnZeroCaseClicked)
      }
      STAFF -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle("Now is your chance to show off!")
          .setDescription("Make an impact and stand out with beautiful images of your business.").setFooterText(
            "Supported format: JPEG, PNG\n" +
                "Recommended dimension: Square (min. 800*800px)\n" +
                "Max file size: 800kb"
          ).setIcon(R.drawable.ic_services_zero_case)
          .setToolBarTitle("")
          .setListner(context as OnZeroCaseClicked)
      }
      APPOINTMENT -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle("Now is your chance to show off!")
          .setDescription("Make an impact and stand out with beautiful images of your business.").setFooterText(
            "Supported format: JPEG, PNG\n" +
                "Recommended dimension: Square (min. 800*800px)\n" +
                "Max file size: 800kb"
          ).setIcon(R.drawable.ic_services_zero_case)
          .setToolBarTitle("")
          .setListner(context as OnZeroCaseClicked)

      }
      PRODUCT ->
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle("Now is your chance to show off!")
          .setDescription("Make an impact and stand out with beautiful images of your business.").setFooterText(
            "Supported format: JPEG, PNG\n" +
                "Recommended dimension: Square (min. 800*800px)\n" +
                "Max file size: 800kb"
          ).setIcon(R.drawable.ic_services_zero_case)
          .setToolBarTitle("")
          .setListner(context as OnZeroCaseClicked)

      else -> {}
    }
    }

}
