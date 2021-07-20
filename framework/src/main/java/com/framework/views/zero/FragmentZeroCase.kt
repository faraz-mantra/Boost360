package com.framework.views.zero

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
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

      fun setListener(click: OnZeroCaseClicked): ZeroCaseBuilder {
        onZeroCaseClicked = click
        return this
      }
      fun onZerothCaseListener(): OnZeroCaseClicked? {
        return onZeroCaseClicked
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
        if (button.primaryButtonBackground != null)
          binding?.btnPrimary?.setBackgroundColor(button.primaryButtonBackground)
          else binding?.btnPrimary?.setBackgroundColor(R.color.white!!)
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
          binding?.btnSecondary?.setBackgroundColor(R.color.white!!)
        else binding?.btnSecondary?.background = ContextCompat.getDrawable(requireContext(),R.drawable.stroke_accent)
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
          binding?.btnTertiary?.setBackgroundColor(button.tertiaryButtonBackground)
        else binding?.btnTertiary?.setBackgroundColor(R.color.white!!)
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
  SERVICES, STAFF, APPOINTMENT, PRODUCT ,MY_BANK_ACCOUNT , IMAGE_GALLERY,LATEST_NEWS_UPADATES
}

class RequestZeroCaseBuilder(private var zeroCases: ZeroCases, private var onZeroCaseClicked: OnZeroCaseClicked, private  var context: Context) {
  fun getRequest(): FragmentZeroCase.Companion.ZeroCaseBuilder {
    return when (zeroCases) {
      MY_BANK_ACCOUNT -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.you_have_not_added_your_bank))
          .setDescription(
            context.getString(R.string.when_enabled_your_online_sales_will_be)
          ).setIcon(R.drawable.ic_bank_zero_case)
          .setToolBarTitle(context.getString(R.string.my_bank_acccount))
          .setFooterText(context.getString(R.string.all_customer_payments_will_be_sent_to_this))
          .setListener(onZeroCaseClicked)
          .setButton(ZeroCaseButton(primaryButtonTitle = context.getString(R.string.add_bank_account), primaryButtonBackground = R.color.colorAccent,primaryButtonIconLeft = R.drawable.ic_create_white,secondaryButtonIconLeft = R.drawable.exo_icon_play,secondaryButtonTitle = context.getString(R.string.watch_how_it_works) ))
      }
      IMAGE_GALLERY -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.now_is_your_chance_to_show_off))
          .setDescription("Make an impact and stand out with beautiful images of your business.").setFooterText(
            "Supported format: JPEG, PNG\n" +
                "Recommended dimension: Square (min. 800*800px)\n" +
                "Max file size: 800kb"
          ).setIcon(R.drawable.ic_image_gallery_zero_case)
          .setToolBarTitle("Image Gallery")
          .setFooterText("Supported format: JPEG, PNG\n" +
              "Recommended dimension: Square (min. 800*800px)\n" +
              "Max file size: 800kb")
          .setListener( onZeroCaseClicked)
          .setButton(ZeroCaseButton(primaryButtonTitle = context.getString(R.string.add_bank_account), primaryButtonBackground = R.color.colorAccent,primaryButtonIconLeft = R.drawable.ic_create_white,secondaryButtonIconLeft = R.drawable.exo_icon_play,secondaryButtonTitle = context.getString(R.string.watch_how_it_works) ))

      }
      STAFF -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle("Now is your chance to show off!")
          .setDescription("Make an impact and stand out with beautiful images of your business.").setFooterText(
            "Supported format: JPEG, PNG\n" +
                "Recommended dimension: Square (min. 800*800px)\n" +
                "Max file size: 800kb"
          ).setIcon(R.drawable.ic_image_gallery_zero_case)
          .setToolBarTitle("")
          .setListener(onZeroCaseClicked)
      }
      APPOINTMENT -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle("Now is your chance to show off!")
          .setDescription("Make an impact and stand out with beautiful images of your business.").setFooterText(
            "Supported format: JPEG, PNG\n" +
                "Recommended dimension: Square (min. 800*800px)\n" +
                "Max file size: 800kb"
          ).setIcon(R.drawable.ic_image_gallery_zero_case)
          .setToolBarTitle("")
          .setButton(ZeroCaseButton())
          .setListener(onZeroCaseClicked)

      }
      LATEST_NEWS_UPADATES ->
        return FragmentZeroCase.Companion.ZeroCaseBuilder().
        setTitle(context.getString(R.string.lets_start_promoting_your_business))
          .setDescription(context.getString(R.string.you_can_post_regarding)
          ).setIcon(R.drawable.ic_latest_news_updates_zero_case)
          .setToolBarTitle(context.getString(R.string.latest_news_and_updates))
          .setListener(onZeroCaseClicked)
      SERVICES ->{
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.lets_add_services_to_your_catalogue))
          .setDescription(context.getString(R.string.your_can_easily_add_detailed_information_about_your_services))
          .setIcon(R.drawable.ic_services_zero_case)
          .setToolBarTitle(context.getString(R.string.services))
          .setFooterText(context.getString(R.string.all_customer_payments_will_be_sent_to_this))
          .setListener(onZeroCaseClicked)
          .setButton(ZeroCaseButton(primaryButtonTitle =context.getString(R.string.add_a_service), primaryButtonBackground = R.color.colorPrimary,primaryButtonIconLeft = R.drawable.ic_create_white,secondaryButtonIconLeft = R.drawable.ic_appointment_settings_secondary,secondaryButtonTitle = context.getString(
                      R.string.appointment_setup) ,tertiaryButtonIconLeft =R.drawable.ic_services_tutorial_tertiary_icon,tertiaryButtonTitle = context.getString(R.string.watch_how_it_works) ))
      }
      PRODUCT -> TODO()
    }
    }

}
