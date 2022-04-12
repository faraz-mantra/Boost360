package com.framework.views.zero.old

import android.content.Context
import android.os.Bundle
import android.view.*
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
import com.framework.extensions.visible
import com.framework.views.zero.FragmentZeroCase
import com.framework.views.zero.ZeroCases
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

      fun isPremium(isPremium: Boolean?): AppZeroCaseBuilder {
        arguments!!.putBoolean("isPremium", isPremium ?: false)
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
        binding.cvPrimary.visible()
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
        binding.cvSecondary.visible()
        if (button.secondaryButtonBackground != null)
          binding.btnSecondary.setBackgroundColor(getColor(R.color.white))
        else binding.btnSecondary.background = ContextCompat.getDrawable(requireContext(), R.drawable.stroke_accent)
        binding.ctvBtnSecondaryTitle.text = button.secondaryButtonTitle
        if (button.secondaryButtonIconLeft != null)
          binding.civIconLeftSecondary.apply { setImageDrawable(getDrawable(requireContext(), button.secondaryButtonIconLeft));setTintColor(getColor(R.color.colorAccentLight)) }
      } else {
        binding.cvSecondary.gone()
      }
      // tertiary button properties
      if (button.tertiaryButtonTitle != null) {
        binding.cvTertiary.visible()
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

  fun setRootBG(@ColorRes color: Int){
      binding.rootLayout.setBackgroundColor(ContextCompat.getColor(requireActivity(),color))
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
  SERVICES, STAFF_LISTING, DOCTOR_PROFILE_LISTING, APPOINTMENT, PRODUCT,
  MY_BANK_ACCOUNT, IMAGE_GALLERY, LATEST_NEWS_UPADATES, CUSTOMER_MESSAGES,
  TESTIMONIAL, NEWS_LETTER_SUBSCRIPTION, ORDERS, SEASONAL_OFFERS, BUSINESS_CALLS,
  UPCOMING_BATCHES, FACULTY_MANAGEMENT, TEAM_MEMBERS, DOCTOR_PROFILE, PROJECTS,
  CUSTOM_PAGES, CLIENT_LOGOS, WEBSITE_FAQ, RESTURANT_STORY, MENU_PICTURES,
  BUSINESS_KEYBOARD, TABLE_BOOKING, ROOMS_LISTING, TOPPERS, RESTURANT_MENU, BROCHURES, SPA_SERVICES,
  SALON_SERVICES, RESTAURANT_SERVICES, EDUCATION_SERVICES, HOSPITAL_SERVICES
}


class AppRequestZeroCaseBuilder(
  private var AppZeroCases: AppZeroCases,
  private var onZeroCaseClicked: AppOnZeroCaseClicked,
  private var context: Context, private var isPremium: Boolean = true
) {

  var title: String = ""
  var primaryButtonTitle: String = ""
  var primaryButtonIconLeft: Int = -1

  constructor(
    AppZeroCases: AppZeroCases, onZeroCaseClicked: AppOnZeroCaseClicked, context: Context
  ) : this(AppZeroCases, onZeroCaseClicked, context, true)

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
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.add_bank_account),
              primaryButtonBackground = R.color.colorAccentLight,
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
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.upload_photos),
              primaryButtonBackground = R.color.colorAccentLight,
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
          ).setIcon(R.drawable.ic_no_appointment)
          .setToolBarTitle(context.getString(R.string.tool_bar_appointment_title))
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.book_an_appointment),
              primaryButtonBackground = R.color.colorAccentLight,
              primaryButtonIconLeft = R.drawable.ic_create_white,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(R.string.watch_how_it_works)
            )
          )
          .setListener(onZeroCaseClicked)

      }
      LATEST_NEWS_UPADATES ->
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder()
          .setTitle(context.getString(R.string.lets_start_promoting_your_business))
          .setDescription(
            context.getString(R.string.you_can_post_regarding)
          ).setIcon(R.drawable.ic_latest_news_updates_zero_case)
          .setToolBarTitle(context.getString(R.string.latest_news_and_updates))
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.post_an_update),
              primaryButtonBackground = R.color.colorAccentLight,
              primaryButtonIconLeft = R.drawable.ic_create_white,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
          .setListener(onZeroCaseClicked)
      SERVICES -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.lets_add_services_to_your_catalogue))
          .setDescription(context.getString(R.string.your_can_easily_add_detailed_information_about_your_services))
          .setIcon(R.drawable.ic_services_zero_case)
          .isPremium(isPremium)
          .setToolBarTitle(context.getString(R.string.services))
          .setFooterText(context.getString(R.string.all_customer_payments_will_be_sent_to_this))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.add_a_service),
              primaryButtonBackground = R.color.colorAccentLight,
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
          .setDescription(context.getString(R.string.product_description_))
          .setIcon(R.drawable.ic_resource_package)
          .isPremium(isPremium)
          .setToolBarTitle(context.getString(R.string.product))
          .setFooterText(context.getString(R.string.product_footer))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.add_a_product),
              primaryButtonBackground = R.color.colorAccentLight,
              primaryButtonIconLeft = R.drawable.ic_create_white,
              secondaryButtonIconLeft = R.drawable.ic_appointment_settings_secondary,
              secondaryButtonTitle = context.getString(R.string.setup_ecommerce),
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
          .isPremium(isPremium)
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
        if (isPremium) {
          title = context.getString(R.string.you_havent_posted_testimonial_yet)
          primaryButtonTitle = context.getString(R.string.add_a_testimonial)
          primaryButtonIconLeft = R.drawable.ic_create_white
        } else {
          title = context.getString(R.string.this_is_premium_feature)
          primaryButtonTitle = context.getString(R.string.activate_this_feature)
          primaryButtonIconLeft = R.drawable.ic_lockkey
        }
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(title)
          .setDescription(context.getString(R.string.testimonial_description_zero))
          .setIcon(R.drawable.ic_testimonial_zero_case)
          .setToolBarTitle(context.getString(R.string.tool_bar_testimonial))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = primaryButtonIconLeft,
              primaryButtonTitle = primaryButtonTitle,
              primaryButtonBackground = R.color.colorAccentLight,
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
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_newsletter_cta,
              primaryButtonTitle = context.getString(R.string.send_subscription_invite),
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      STAFF_LISTING -> {
        if (isPremium) {
          title = context.getString(R.string.no_staff_member_listed_yet)
          primaryButtonTitle = context.getString(R.string.add_a_staff)
          primaryButtonIconLeft = R.drawable.ic_create_white
        } else {
          title = context.getString(R.string.staff_listing_title)
          primaryButtonTitle = context.getString(R.string.activate_this_feature)
          primaryButtonIconLeft = R.drawable.ic_lockkey
        }

        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(title)
          .setDescription(context.getString(R.string.staff_listing_desc))
          .setIcon(R.drawable.ic_users_zero_case)
          .setToolBarTitle(context.getString(R.string.tool_bar_staff_listing))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = primaryButtonIconLeft,
              primaryButtonTitle = primaryButtonTitle,
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      DOCTOR_PROFILE_LISTING -> {
        if (isPremium) {
          title = context.getString(R.string.no_doctor_profile_listed_yet)
          primaryButtonTitle = context.getString(R.string.add_a_doctor)
          primaryButtonIconLeft = R.drawable.ic_create_white
        } else {
          title = context.getString(R.string.staff_listing_title)
          primaryButtonTitle = context.getString(R.string.activate_this_feature)
          primaryButtonIconLeft = R.drawable.ic_lockkey
        }

        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(title)
          .setDescription(context.getString(R.string.doctor_listing_desc))
          .setIcon(R.drawable.ic_doc_hos_zero_case)
          .setToolBarTitle(context.getString(R.string.tool_bar_doctor_listing))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = primaryButtonIconLeft,
              primaryButtonTitle = primaryButtonTitle,
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(R.string.watch_how_it_works)
            )
          )
      }
      ORDERS -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.orders_title))
          .setDescription(context.getString(R.string.orders_description))
          .setIcon(R.drawable.ic_archivebox)
          .setToolBarTitle(context.getString(R.string.tool_bar_orders))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_marketplace_cart,
              primaryButtonTitle = context.getString(R.string.create_an_offline_order),
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      SEASONAL_OFFERS -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder()
          .setTitle(context.getString(R.string.attract_more_customers))
          .setDescription(context.getString(R.string.increase_your_customer_base_with_promotions_and_offer_campaigns))
          .setIcon(R.drawable.ic_percent)
          .setToolBarTitle(context.getString(R.string.toolbar_seasonal_offers))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.add_offer),
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      BUSINESS_CALLS -> {
        var zeroCaseButton: ZeroCaseButton? = null
        if (isPremium) {
          title = context.getString(R.string.no_call_tracked_yet)
          zeroCaseButton = ZeroCaseButton(
            secondaryButtonIconLeft = R.drawable.exo_icon_play,
            secondaryButtonTitle = context.getString(
              R.string.watch_how_it_works
            )
          )
        } else {
          title = context.getString(R.string.this_is_premium_feature)
          primaryButtonTitle = context.getString(R.string.activate_this_feature)
          primaryButtonIconLeft = R.drawable.ic_lockkey
          zeroCaseButton = ZeroCaseButton(
            primaryButtonIconLeft = primaryButtonIconLeft,
            primaryButtonTitle = primaryButtonTitle,
            primaryButtonBackground = R.color.colorAccentLight,
            secondaryButtonIconLeft = R.drawable.exo_icon_play,
            secondaryButtonTitle = context.getString(
              R.string.watch_how_it_works
            )
          )
        }
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(
          title
        )
          .setDescription(context.getString(R.string.business_calls_description))
          .setIcon(R.drawable.ic_call_zero)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            zeroCaseButton
          )
      }

      //todo batch 2 screens
      UPCOMING_BATCHES -> {
        if (isPremium) {
          title = context.getString(R.string.display_your_course_batches)
          primaryButtonTitle = context.getString(R.string.create_a_batch)
          primaryButtonIconLeft = R.drawable.ic_create_white
        } else {
          title = context.getString(R.string.this_is_premium_feature)
          primaryButtonTitle = context.getString(R.string.activate_this_feature)
          primaryButtonIconLeft = R.drawable.ic_lockkey
        }
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(title)
          .setDescription(context.getString(R.string.list_the_upcoming_dates_of_your_courses_to_provide_students_and_parents_with_information_and_create_an_urgency_to_enrol_online))
          .setIcon(R.drawable.ic_usersfour)
          .setToolBarTitle(context.getString(R.string.upcoming_batches_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = primaryButtonIconLeft,
              primaryButtonTitle = primaryButtonTitle,
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      FACULTY_MANAGEMENT -> {
        if (isPremium) {
          title = context.getString(R.string.display_your_faculty_s_expertise)
          primaryButtonTitle = context.getString(R.string.add_a_faculty_member)
          primaryButtonIconLeft = R.drawable.ic_create_white
        } else {
          title = context.getString(R.string.this_is_premium_feature)
          primaryButtonTitle = context.getString(R.string.activate_this_feature)
          primaryButtonIconLeft = R.drawable.ic_lockkey
        }
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(title)
          .setDescription(context.getString(R.string.when_students_find_out_about_a_faculty_member_s_qualifications_experience_or_awards_they_will_feel_more_comfortable_booking_a_course))
          .setIcon(R.drawable.ic_chalkboardteacher)
          .setToolBarTitle(context.getString(R.string.faculty_management_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = primaryButtonIconLeft,
              primaryButtonTitle = primaryButtonTitle,
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      TEAM_MEMBERS -> {
        if (isPremium) {
          title = context.getString(R.string.introduce_your_team_members)
          primaryButtonTitle = context.getString(R.string.add_a_team_member)
          primaryButtonIconLeft = R.drawable.ic_create_white
        } else {
          title = context.getString(R.string.this_is_premium_feature)
          primaryButtonTitle = context.getString(R.string.activate_this_feature)
          primaryButtonIconLeft = R.drawable.ic_lockkey
        }
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(title)
          .setDescription(context.getString(R.string.team_members_description))
          .setIcon(R.drawable.ic_usersthree)
          .setToolBarTitle(context.getString(R.string.team_member_tool_bar_title))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = primaryButtonIconLeft,
              primaryButtonTitle = primaryButtonTitle,
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      DOCTOR_PROFILE -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder()
          .setTitle(context.getString(R.string.establish_trust_with_patients))
          .setDescription(context.getString(R.string.a_doctor_profile_makes_your_website_more_credible_resulting_in_increased_patient_traffic))
          .setIcon(R.drawable.ic_diagnosis)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      PROJECTS -> {
        if (isPremium) {
          title = context.getString(R.string.showcase_your_skills)
          primaryButtonTitle = context.getString(R.string.add_a_project)
          primaryButtonIconLeft = R.drawable.ic_create_white
        } else {
          title = context.getString(R.string.this_is_premium_feature)
          primaryButtonTitle = context.getString(R.string.activate_this_feature)
          primaryButtonIconLeft = R.drawable.ic_lockkey
        }
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(title)
          .setDescription(context.getString(R.string.highlight_your_expertise_by_listing_successfully_completed_projects_to_attract_potential_customers))
          .setIcon(R.drawable.ic_notebook)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = primaryButtonIconLeft,
              primaryButtonTitle = primaryButtonTitle,
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      TOPPERS -> {

        if (isPremium) {
          title = context.getString(R.string.feature_your_best_students)
          primaryButtonTitle = context.getString(R.string.add_a_topper)
          primaryButtonIconLeft = R.drawable.ic_create_white
        } else {
          title = context.getString(R.string.this_is_premium_feature)
          primaryButtonTitle = context.getString(R.string.activate_this_feature)
          primaryButtonIconLeft = R.drawable.ic_lockkey
        }

        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(title)
          .setDescription(context.getString(R.string.toppers_description))
          .setIcon(R.drawable.ic_topper)
          .setToolBarTitle(context.getString(R.string.toppers_title))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = primaryButtonIconLeft,
              primaryButtonTitle = primaryButtonTitle,
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      CUSTOM_PAGES -> {

        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.engage_your_potential_customers))
          .setDescription(context.getString(R.string.you_can_create_customized_pages_that_highlight_specific_products_and_services_upload_blog_posts_display_pictures_and_videos_to_keep_visitors_engaged))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_create_white,
              primaryButtonTitle = context.getString(R.string.add_a_custom_page),
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      CLIENT_LOGOS -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.show_off_your_clients))
          .setDescription(context.getString(R.string.showcase_the_logos_of_your_top_clients_on_your_website_to_generate_trust_and_credibility_with_customers))
          .setIcon(R.drawable.ic_phoneincoming)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .isPremium(isPremium)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_create_white,
              primaryButtonTitle = context.getString(R.string.add_a_logo),
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      WEBSITE_FAQ -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.help_your_potential_customers))
          .setDescription(context.getString(R.string.to_help_visitors_make_a_buying_decision_include_the_most_frequently_asked_questions_faqs_and_suitable_answers))
          .setIcon(R.drawable.ic_phoneincoming)
          .isPremium(isPremium)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.add_a_question),
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      RESTURANT_STORY -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.storytelling_connects_with_people))
          .setDescription(context.getString(R.string.share_a_compelling_story_behind_your_restaurant_to_build_a_personal_connection_with_visitors_and_turn_them_into_loyal_customers))
          .setIcon(R.drawable.ic_phoneincoming)
          .isPremium(isPremium)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_create_white,
              primaryButtonTitle = context.getString(R.string.tell_your_story),
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      MENU_PICTURES -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.make_your_food_irresistible))
          .setDescription(context.getString(R.string.upload_mouth_watering_pictures_of_your_menu_items_to_encourage_website_visitors_to_place_an_order))
          .setIcon(R.drawable.ic_phoneincoming)
          .isPremium(isPremium)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_create_white,
              primaryButtonTitle = context.getString(R.string.upload_menu_pictures),
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      BUSINESS_KEYBOARD -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.this_is_premium_feature))
          .setDescription(context.getString(R.string.use_boost_s_business_keyboard_to_instantly_share_your_latest_products_services_and_updates_with_customers_through_whatsapp_hike_telegram_messenger_and_other_popular_messaging_apps))
          .setIcon(R.drawable.ic_phoneincoming)
          .isPremium(isPremium)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      TABLE_BOOKING -> {
        if (isPremium) {
          title = context.getString(R.string.enable_online_table_reservations)
          primaryButtonIconLeft = R.drawable.ic_create_white
          primaryButtonTitle = context.getString(R.string.book_a_table)
        } else {
          title = context.getString(R.string.this_is_premium_feature)
          primaryButtonIconLeft = R.drawable.ic_lockkey
          primaryButtonTitle = context.getString(R.string.activate_this_feature)
        }
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(title)
          .setDescription(context.getString(R.string.the_key_to_a_happy_customer_is_convenience_and_no_waiting_time_at_the_restaurant))
          .setIcon(R.drawable.ic_phoneincoming)
          .isPremium(isPremium)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = primaryButtonIconLeft,
              primaryButtonTitle = primaryButtonTitle,
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      ROOMS_LISTING -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.display_your_room_s_features))
          .setDescription(context.getString(R.string.feature_all_available_rooms_and_their_amenities_so_that_guests_can_pick_and_book_conveniently_online))
          .setIcon(R.drawable.ic_phoneincoming)
          .isPremium(isPremium)
          .setToolBarTitle(context.getString(R.string.business_calls_tool_bar_title))
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.add_a_room),
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.ic_booking_setup,
              secondaryButtonTitle = context.getString(
                R.string.booking_setup
              ) ,
              tertiaryButtonIconLeft = R.drawable.exo_icon_play,
              tertiaryButtonTitle = context.getString(
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
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = R.drawable.ic_lockkey,
              primaryButtonTitle = context.getString(R.string.activate_this_feature),
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      BROCHURES -> {
        if (isPremium) {
          title = context.getString(R.string.engage_your_customers)
          primaryButtonIconLeft = R.drawable.ic_create_white
          primaryButtonTitle = context.getString(R.string.add_a_brochure)
        } else {
          title = context.getString(R.string.this_is_premium_feature)
          primaryButtonIconLeft = R.drawable.ic_lockkey
          primaryButtonTitle = context.getString(R.string.activate_this_feature)
        }
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(title)
          .setDescription(context.getString(R.string.brochures_desc))
          .setIcon(R.drawable.ic_brochures)
          .setToolBarTitle(context.getString(R.string.digital_brochures))
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonIconLeft = primaryButtonIconLeft,
              primaryButtonTitle = primaryButtonTitle,
              primaryButtonBackground = R.color.colorAccentLight,
              secondaryButtonIconLeft = R.drawable.exo_icon_play,
              secondaryButtonTitle = context.getString(
                R.string.watch_how_it_works
              )
            )
          )
      }
      SPA_SERVICES -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.showcase_your_services))
          .setDescription(context.getString(R.string.allow_your_customers_to_select_and_book_an_appointment_online_by_displaying_the_information_and_pricing_for_all_your_spa_services))
          .setIcon(R.drawable.ic_flowerlotus)
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.add_a_service),
              primaryButtonBackground = R.color.colorAccentLight,
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
      HOSPITAL_SERVICES -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.showcase_your_services))
          .setDescription(context.getString(R.string.make_it_possible_for_patients_to_book_appointments_online_by_listing_your_services_and_their_details))
          .setIcon(R.drawable.ic_policy)
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.add_a_service),
              primaryButtonBackground = R.color.colorAccentLight,
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
      SALON_SERVICES -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.showcase_your_services))
          .setDescription(context.getString(R.string.allow_your_customers_to_select_and_book_an_appointment_online_by_displaying_the_information_and_pricing_for_all_your_services))
          .setIcon(R.drawable.ic_scissors)
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.add_a_service),
              primaryButtonBackground = R.color.colorAccentLight,
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
      RESTAURANT_SERVICES -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.put_your_menu_on_display))
          .setDescription(context.getString(R.string.create_a_menu_for_your_restaurant_website_by_adding_dishes_and_their_prices))
          .setIcon(R.drawable.ic_pizza)
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.add_a_dish),
              primaryButtonBackground = R.color.colorAccentLight,
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
      EDUCATION_SERVICES -> {
        return AppFragmentZeroCase.Companion.AppZeroCaseBuilder().setTitle(context.getString(R.string.showcase_your_course_offerings))
          .setDescription(context.getString(R.string.provide_all_the_details_about_your_courses_including_faculty_timings_duration_and_the_option_for_students_to_enroll_online))
          .setIcon(R.drawable.ic_book)
          .isPremium(isPremium)
          .setListener(onZeroCaseClicked)
          .setButton(
            ZeroCaseButton(
              primaryButtonTitle = context.getString(R.string.add_a_course),
              primaryButtonBackground = R.color.colorAccentLight,
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
    }
  }
}



