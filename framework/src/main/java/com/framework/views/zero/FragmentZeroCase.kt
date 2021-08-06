package com.framework.views.zero

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources.getDrawable
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

      fun isPremium(isPremium: Boolean?): ZeroCaseBuilder {
        arguments!!.putBoolean("isPremium", isPremium?:false)
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
    val isPremium = arguments?.getBoolean("isPremium",false)
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
          binding?.btnPrimary?.setBackgroundColor(getColor(button.primaryButtonBackground))
        else binding?.btnPrimary?.setBackgroundColor(R.color.white!!)
        if (button.primaryButtonIconLeft != null)
          binding?.civIconLeft?.setImageDrawable(if (isPremium == true) getDrawable(requireContext(),R.drawable.ic_lockkey) else getDrawable(requireContext(), button.primaryButtonIconLeft))
        binding?.ctvBtnPrimaryTitle?.text = if(isPremium==true) getString(R.string.activate_this_feature) else button.primaryButtonTitle
      } else {
        binding?.cvPrimary?.gone()
      }
      // secondary button properties
      if (button.secondaryButtonTitle != null) {
        if (button.secondaryButtonBackground != null)
          binding?.btnSecondary?.setBackgroundColor(getColor(R.color.white!!))
        else binding?.btnSecondary?.background = ContextCompat.getDrawable(requireContext(), R.drawable.stroke_accent)
        binding?.ctvBtnSecondaryTitle?.text = button.secondaryButtonTitle
        if (button.secondaryButtonIconLeft != null)
          binding?.civIconLeftSecondary?.setImageDrawable(getDrawable(requireContext(), button.secondaryButtonIconLeft))
      } else {
        binding?.cvSecondary?.gone()
      }
      // tertiary button properties
      if (button.tertiaryButtonTitle != null) {
        if (button.tertiaryButtonBackground != null)
          binding?.btnTertiary?.setBackgroundColor(getColor(button.tertiaryButtonBackground))
        else binding?.btnTertiary?.setBackgroundColor(getColor(R.color.white!!))
        binding?.ctvBtnTitleTertiary?.text = button.tertiaryButtonTitle
        if (button.tertiaryButtonIconLeft != null)
          binding?.civIconLeftTertiary?.setImageDrawable(getDrawable(requireContext(), button.tertiaryButtonIconLeft))
      } else {
        binding?.cvTertiary?.gone()
      }
    } else {
      binding?.llBtnContainer?.visibility = View.GONE
    }
    baseActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
  }

  var callback = object : OnBackPressedCallback(true) {
    override fun handleOnBackPressed() {
      onZeroCaseClicked?.onBackPressed()
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
  fun onBackPressed()
}

data class ZeroCaseButton(
  val primaryButtonTitle: String? = null,
  val primaryButtonBackground: Int? = null,
  val secondaryButtonTitle: String? = null,
  val secondaryButtonBackground: Int? = null,
  val tertiaryButtonTitle: String? = null,
  val primaryButtonIconLeft: Int? = null,
  val secondaryButtonIconLeft: Int? = null,
  val tertiaryButtonIconLeft: Int? = null,
  val tertiaryButtonBackground: Int? = null,
) : Serializable


enum class ZeroCases {
  SERVICES, STAFF_LISTING, APPOINTMENT, PRODUCT, MY_BANK_ACCOUNT, IMAGE_GALLERY, LATEST_NEWS_UPADATES, CUSTOMER_MESSAGES, TESTIMONIAL, NEWS_LETTER_SUBSCRIPTION, ORDERS, SEASONAL_OFFERS, BUSINESS_CALLS, UPCOMING_BATCHES, FACULTY_MANAGEMENT, TEAM_MEMBERS, DOCTOR_PROFILE, PROJECTS, CUSTOM_PAGES, CLIENT_LOGOS, WEBSITE_FAQ, RESTURANT_STORY, MENU_PICTURES, BUSINESS_KEYBOARD, TABLE_BOOKING, ROOMS_LISTING, RESTURANT_MENU
}

class RequestZeroCaseBuilder(private var zeroCases: ZeroCases, private var onZeroCaseClicked: OnZeroCaseClicked, private var context: Context,private  var isPremium:Boolean?=false) {
  fun getRequest(): FragmentZeroCase.Companion.ZeroCaseBuilder {
    when (zeroCases) {
      MY_BANK_ACCOUNT -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.my_bank_acccount))
          .setDescription(
            context.getString(R.string.when_enabled_your_online_sales_will_be)
          ).setIcon(R.drawable.ic_bank_zero_case)
          .setToolBarTitle(context.getString(R.string.my_bank_acccount))
          .setFooterText(context.getString(R.string.all_customer_payments_will_be_sent_to_this))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.add_bank_account),
              primaryButtonBackground = R.color.colorAccent,
              primaryButtonIconLeft = R.drawable.ic_create_white,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(R.string.watch_how_it_works)
            )
          )
      }
      IMAGE_GALLERY -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.now_is_your_chance_to_show_off))
          .setDescription(context.getString(R.string.image_gallery_description)).setFooterText(
            context.getString(R.string.image_gallery_footer)
          ).setIcon(R.drawable.ic_image_gallery_zero_case)
          .setToolBarTitle(context.getString(R.string.tool_bar_image_gallery))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.upload_photos),
              primaryButtonBackground = R.color.colorAccent,
              primaryButtonIconLeft = R.drawable.ic_create_white,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(R.string.watch_how_it_works)
            )
          )

      }
      APPOINTMENT -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.no_appointment_yet))
          .setDescription(
            context.getString(R.string.appointment_description)
          ).setIcon(R.drawable.ic_calendarx)
          .setToolBarTitle(context.getString(R.string.tool_bar_appointment_title))
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.book_an_appointment),
              primaryButtonBackground = R.color.colorAccent,
              primaryButtonIconLeft = R.drawable.ic_create_white,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(R.string.watch_how_it_works)
            )
          )
          .setListener(onZeroCaseClicked)

      }
      LATEST_NEWS_UPADATES ->
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.lets_start_promoting_your_business))
          .setDescription(
            context.getString(R.string.you_can_post_regarding)
          ).setIcon(R.drawable.ic_latest_news_updates_zero_case)
          .setToolBarTitle(context.getString(R.string.latest_news_and_updates))
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.post_an_update),
              primaryButtonBackground = R.color.colorPrimary,
              primaryButtonIconLeft = R.drawable.ic_create_white,
              secondaryButtonIconLeft = R.drawable.ic_services_tutorial_tertiary_icon,
              secondaryButtonTitle = context.getString(
                R.string.appointment_setup
              )
            )
          )
          .setListener(onZeroCaseClicked)
      SERVICES -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.lets_add_services_to_your_catalogue))
          .setDescription(context.getString(R.string.your_can_easily_add_detailed_information_about_your_services))
          .setIcon(R.drawable.ic_services_zero_case)
          .setToolBarTitle(context.getString(R.string.services))
          .isPremium(isPremium)
          .setFooterText(context.getString(R.string.all_customer_payments_will_be_sent_to_this))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.add_a_service),
              primaryButtonBackground = R.color.colorPrimary,
              primaryButtonIconLeft = R.drawable.ic_create_white,
              secondaryButtonIconLeft = R.drawable.ic_appointment_settings_secondary,
              secondaryButtonTitle = context.getString(
                R.string.appointment_setup
              ),
              tertiaryButtonIconLeft = R.drawable.ic_services_tutorial_tertiary_icon,
              tertiaryButtonTitle = context.getString(R.string.watch_how_it_works)
            )
          )
      }
      PRODUCT -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.lets_add_product_to))
          .setDescription(context.getString(R.string.product_description))
          .setIcon(R.drawable.ic_resource_package)
          .setToolBarTitle(context.getString(R.string.product))
          .isPremium(isPremium)
          .setFooterText(context.getString(R.string.product_footer))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.add_a_product),
              primaryButtonBackground = R.color.colorPrimary,
              primaryButtonIconLeft = R.drawable.ic_create_white,
              secondaryButtonIconLeft = R.drawable.ic_appointment_settings_secondary,
              secondaryButtonTitle = context.getString(
                R.string.setup_ecommerce
              ),
              tertiaryButtonIconLeft = R.drawable.ic_services_tutorial_tertiary_icon,
              tertiaryButtonTitle = context.getString(R.string.watch_how_it_works)
            )
          )
      }
      CUSTOMER_MESSAGES -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.customer_messages_title))
          .setDescription(context.getString(R.string.customer_messages_description))
          .setIcon(R.drawable.ic_customer_messages_zero_case)
          .isPremium(isPremium)
          .setToolBarTitle(context.getString(R.string.tool_bar_customer_messages))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              secondaryButtonIconLeft = R.drawable.exo_icon_play, secondaryButtonTitle = context.getString(
                R.string.see_how_enqaries_works
              )
            )
          )
      }
      TESTIMONIAL -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.testimonial_title_zero))
          .setDescription(context.getString(R.string.testimonial_description_zero))
          .setIcon(R.drawable.ic_testimonial_zero_case)
          .setToolBarTitle(context.getString(R.string.tool_bar_testimonial))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      NEWS_LETTER_SUBSCRIPTION -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.news_subscription_title))
          .setDescription(context.getString(R.string.news_letter_description))
          .setIcon(R.drawable.ic_usercirclegear)
          .isPremium(isPremium)
          .setToolBarTitle(context.getString(R.string.tool_bar_news_subscription))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_email,
              primaryButtonTitle = context.getString(R.string.send_subscription_invite),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      STAFF_LISTING -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.staff_listing_title))
          .setDescription(context.getString(R.string.staff_listing_desc))
          .setIcon(R.drawable.ic_users_zero_case)
          .isPremium(isPremium)
          .setToolBarTitle(context.getString(R.string.tool_bar_staff_listing))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      ORDERS -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.orders_title))
          .setDescription(context.getString(R.string.orders_description))
          .setIcon(R.drawable.ic_archivebox)
          .isPremium(isPremium)
          .setToolBarTitle(context.getString(R.string.tool_bar_orders))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_marketplace_cart,
              primaryButtonTitle = context.getString(R.string.create_an_offline_order),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      SEASONAL_OFFERS -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.seasonal_offers))
          .setDescription(context.getString(R.string.seasonal_offer_description))
          .setIcon(R.drawable.ic_percent)
          .setToolBarTitle(context.getString(R.string.toolbar_seasonal_offers))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      BUSINESS_CALLS -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }

      //todo batch 2 screens
      UPCOMING_BATCHES -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.upcoming_batches_description))
          .setIcon(R.drawable.ic_usersfour)
          .setToolBarTitle(context.getString(R.string.upcoming_batches_tool_bar_title))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      FACULTY_MANAGEMENT -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.faculty_management_description))
          .setIcon(R.drawable.ic_chalkboardteacher)
          .setToolBarTitle(context.getString(R.string.faculty_management_tool_bar_title))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      TEAM_MEMBERS -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.team_members_description))
          .setIcon(R.drawable.ic_usersthree)
          .isPremium(isPremium)
          .setToolBarTitle(context.getString(R.string.team_member_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      DOCTOR_PROFILE -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_diagnosis)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      PROJECTS -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      CUSTOM_PAGES -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      CLIENT_LOGOS -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      WEBSITE_FAQ -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      RESTURANT_STORY -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      MENU_PICTURES -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      BUSINESS_KEYBOARD -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      TABLE_BOOKING -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      ROOMS_LISTING -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      RESTURANT_MENU -> {
        return FragmentZeroCase.Companion.ZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccent,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
    }
  }
}

