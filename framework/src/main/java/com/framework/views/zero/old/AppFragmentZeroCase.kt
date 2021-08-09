package com.framework.views.zero.old

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.framework.R
import com.framework.databinding.FragmentZeroCaseBinding
import com.framework.extensions.gone
import com.framework.views.zero.old.AppZeroCases.*
import java.io.Serializable

class AppFragmentZeroCase : Fragment() {


  companion object {
    var onZeroCaseClicked: AppOnZeroCaseClicked? = null

    class AppZeroCaseBuilder {
      var arguments: Bundle? = null

      init {
        arguments = Bundle()
      }

      fun setTitle(title: String?): AppZeroCaseBuilder {
        arguments!!.putString("title", title)
        return this
      }

      fun setDescription(description: String?): AppZeroCaseBuilder {
        arguments!!.putString("description", description)
        return this
      }

      fun setToolBarTitle(title: String?): AppZeroCaseBuilder {
        arguments!!.putString("toolbar", title)
        return this
      }

      fun setFooterText(footerText: String?): AppZeroCaseBuilder {
        arguments!!.putString("footer", footerText)
        return this
      }

      fun setIcon(icon: Int): AppZeroCaseBuilder {
        arguments!!.putInt("icon", icon)
        return this
      }

      fun setButton(button: ZeroCaseButton): AppZeroCaseBuilder {
        arguments!!.putSerializable("buttons", button)
        return this
      }

      fun setListener(click: AppOnZeroCaseClicked): AppZeroCaseBuilder {
        onZeroCaseClicked = click
        return this
      }

      fun onZerothCaseListener(): AppOnZeroCaseClicked? {
        return onZeroCaseClicked
      }

      fun build(): AppFragmentZeroCase {
        val fragment = AppFragmentZeroCase()
        fragment.arguments = arguments
        return fragment
      }
    }
  }

  private lateinit var binding: FragmentZeroCaseBinding

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
   binding = FragmentZeroCaseBinding.inflate(inflater)
    binding.btnPrimary.setOnClickListener { onZeroCaseClicked?.primaryButtonClicked() }
    binding.btnSecondary.setOnClickListener { onZeroCaseClicked?.secondaryButtonClicked() }
    binding.btnTertiary.setOnClickListener { onZeroCaseClicked?.ternaryButtonClicked() }
    getData()
    return binding.root
  }


  fun getColor(@ColorRes color: Int): Int {
    return ResourcesCompat.getColor(resources, color, context?.theme)
  }


  private fun getData() {
    val title = arguments?.getString("title", null)
    val description = arguments?.getString("description", null)
    val toolbarTitle = arguments?.getString("toolbar", null)
    val icon = arguments?.getInt("icon")
    val footerText = arguments?.getString("footer")
    val button = arguments?.getSerializable("buttons") as? ZeroCaseButton
    binding.ctvTitle.text = title
    binding.ctvDesc.text = description
    activity?.actionBar?.title = (toolbarTitle)
    if (icon != null)
      binding.rivImageView.setImageResource(icon)
    if (footerText != null) {
      binding.ctvFooterText.text = footerText
    } else {
      binding.ctvFooterText.gone()
    }
    if (button != null) {
      // primary button properties
      if (button.primaryButtonTitle != null) {
        if (button.primaryButtonBackground != null)
          binding.btnPrimary.setBackgroundColor(getColor(button.primaryButtonBackground))
        else binding.btnPrimary.setBackgroundColor(getColor(R.color.white))
        if (button.primaryButtonIconLeft != null)
          binding.civIconLeft.setImageDrawable(getDrawable(requireContext(), button.primaryButtonIconLeft))
        binding.ctvBtnPrimaryTitle.text = button.primaryButtonTitle
      } else {
        binding.cvPrimary.gone()
      }
      // secondary button properties
      if (button.secondaryButtonTitle != null) {
        if (button.secondaryButtonBackground != null)
          binding.btnSecondary.setBackgroundColor(getColor(R.color.white))
        else binding.btnSecondary.background = ContextCompat.getDrawable(requireContext(), R.drawable.stroke_accent)
        binding.ctvBtnSecondaryTitle.text = button.secondaryButtonTitle
        if (button.secondaryButtonIconLeft != null)
          binding.civIconLeftSecondary.apply { setImageDrawable(getDrawable(requireContext(), button.secondaryButtonIconLeft));setTintColor(getColor(R.color.colorAccent)) }
      } else {
        binding.cvSecondary.gone()
      }
      // tertiary button properties
      if (button.tertiaryButtonTitle != null) {
        if (button.tertiaryButtonBackground != null)
          binding.btnTertiary.setBackgroundColor(getColor(button.tertiaryButtonBackground))
        else binding.btnTertiary.setBackgroundColor(getColor(R.color.white))
        binding.ctvBtnTitleTertiary.text = button.tertiaryButtonTitle
        if (button.tertiaryButtonIconLeft != null)
          binding.civIconLeftTertiary.setImageDrawable(getDrawable(requireContext(), button.tertiaryButtonIconLeft))
      } else {
        binding.cvTertiary.gone()
      }
    } else {
      binding.llBtnContainer.visibility = View.GONE
    }
  //  activity?.onBackPressedDispatcher?.addCallback(callback)
  }

  var callback = object : OnBackPressedCallback(true) {
    override fun handleOnBackPressed() {
      onZeroCaseClicked?.appOnBackPressed()
    }
  }

}

interface AppOnZeroCaseClicked {
  fun primaryButtonClicked()
  fun secondaryButtonClicked()
  fun ternaryButtonClicked()
  fun appOnBackPressed()
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


enum class AppZeroCases {
  SERVICES, STAFF_LISTING, APPOINTMENT, PRODUCT, MY_BANK_ACCOUNT, IMAGE_GALLERY, LATEST_NEWS_UPADATES, CUSTOMER_MESSAGES, TESTIMONIAL, NEWS_LETTER_SUBSCRIPTION, ORDERS, SEASONAL_OFFERS, BUSINESS_CALLS, UPCOMING_BATCHES, FACULTY_MANAGEMENT, TEAM_MEMBERS, DOCTOR_PROFILE, PROJECTS, CUSTOM_PAGES, CLIENT_LOGOS, WEBSITE_FAQ, RESTURANT_STORY, MENU_PICTURES, BUSINESS_KEYBOARD, TABLE_BOOKING, ROOMS_LISTING, RESTURANT_MENU,BROCHURES
}

class AppRequestZeroCaseBuilder(private var AppZeroCases: AppZeroCases, private var onZeroCaseClicked: AppOnZeroCaseClicked, private var context: Context) {
  fun getRequest(): AppFragmentZeroCase.Companion.AppZeroCaseBuilder {
    when (AppZeroCases) {
      MY_BANK_ACCOUNT -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.my_bank_acccount))
          .setDescription(
            context.getString(R.string.when_enabled_your_online_sales_will_be)
          ).setIcon(R.drawable.ic_bank_zero_case)
          .setToolBarTitle(context.getString(R.string.my_bank_acccount))
          .setFooterText(context.getString(R.string.all_customer_payments_will_be_sent_to_this))
          .setListener(onZeroCaseClicked)
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.now_is_your_chance_to_show_off))
          .setDescription(context.getString(R.string.image_gallery_description)).setFooterText(
            context.getString(R.string.image_gallery_footer)
          ).setIcon(R.drawable.ic_image_gallery_zero_case)
          .setToolBarTitle(context.getString(R.string.tool_bar_image_gallery))
          .setListener(onZeroCaseClicked)
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
      APPOINTMENT -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.no_appointment_yet))
          .setDescription(
            context.getString(R.string.appointment_description)
          ).setIcon(R.drawable.ic_calendarx)
          .setToolBarTitle(context.getString(R.string.tool_bar_appointment_title))
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.lets_start_promoting_your_business))
          .setDescription(
            context.getString(R.string.you_can_post_regarding)
          ).setIcon(R.drawable.ic_latest_news_updates_zero_case)
          .setToolBarTitle(context.getString(R.string.latest_news_and_updates))
          .setListener(onZeroCaseClicked)
      SERVICES -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.lets_add_services_to_your_catalogue))
          .setDescription(context.getString(R.string.your_can_easily_add_detailed_information_about_your_services))
          .setIcon(R.drawable.ic_services_zero_case)
          .setToolBarTitle(context.getString(R.string.services))
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.lets_add_product_to))
          .setDescription(context.getString(R.string.product_description))
          .setIcon(R.drawable.ic_resource_package)
          .setToolBarTitle(context.getString(R.string.product))
          .setFooterText(context.getString(R.string.product_footer))
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
      CUSTOMER_MESSAGES -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.customer_messages_title))
          .setDescription(context.getString(R.string.customer_messages_description))
          .setIcon(R.drawable.ic_customer_messages_zero_case)
          .setToolBarTitle(context.getString(R.string.tool_bar_customer_messages))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              secondaryButtonIconLeft = R.drawable.exo_icon_play, secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      TESTIMONIAL -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.testimonial_title_zero))
          .setDescription(context.getString(R.string.testimonial_description_zero))
          .setIcon(R.drawable.ic_testimonial_zero_case)
          .setToolBarTitle(context.getString(R.string.tool_bar_testimonial))
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.news_subscription_title))
          .setDescription(context.getString(R.string.news_letter_description))
          .setIcon(R.drawable.ic_usercirclegear)
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.staff_listing_title))
          .setDescription(context.getString(R.string.staff_listing_desc))
          .setIcon(R.drawable.ic_users_zero_case)
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.orders_title))
          .setDescription(context.getString(R.string.orders_description))
          .setIcon(R.drawable.ic_archivebox)
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.seasonal_offers))
          .setDescription(context.getString(R.string.seasonal_offer_description))
          .setIcon(R.drawable.ic_percent)
          .setToolBarTitle(context.getString(R.string.toolbar_seasonal_offers))
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.upcoming_batches_description))
          .setIcon(R.drawable.ic_usersfour)
          .setToolBarTitle(context.getString(R.string.upcoming_batches_tool_bar_title))
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.faculty_management_description))
          .setIcon(R.drawable.ic_chalkboardteacher)
          .setToolBarTitle(context.getString(R.string.faculty_management_tool_bar_title))
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.team_members_description))
          .setIcon(R.drawable.ic_usersthree)
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_diagnosis)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
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
      CLIENT_LOGOS -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
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
      RESTURANT_STORY -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
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
      MENU_PICTURES -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
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
      BUSINESS_KEYBOARD -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
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
      TABLE_BOOKING -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
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
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
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
      RESTURANT_MENU -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
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
      BROCHURES -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.brochures_desc))
          .setIcon(R.drawable.ic_brochures)
          .setToolBarTitle(context.getString(R.string.digital_brochures))
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
    }
  }
}



