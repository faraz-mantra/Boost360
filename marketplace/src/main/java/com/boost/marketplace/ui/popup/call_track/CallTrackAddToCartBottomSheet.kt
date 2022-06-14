package com.boost.marketplace.ui.popup.call_track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.ui.details.call_track.CallTrackingActivity
import kotlinx.android.synthetic.main.popup_request_callback_custom_domain.*

class CallTrackAddToCartBottomSheet : DialogFragment() {
    lateinit var root: View
    lateinit var prefs: SharedPrefs
    var addonDetails: FeaturesModel? = null
    var itemInCartStatus = false
    protected lateinit var viewModel: ViewModel



    companion object {
        fun newInstance() = RequestCallbackBottomSheet()
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.fullscreen_color)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.call_tracking_add_to_cart_popup, container, false)
        prefs = SharedPrefs(activity as CallTrackingActivity)
        return root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        back_btn.setOnClickListener {
            dismiss()
        }
        tv_call_expert.setOnClickListener {
            addonDetails = prefs.getAddonDetails()

            if (!itemInCartStatus) {
                if (addonDetails == null) {
                    prefs.storeCartOrderInfo(null)
//                    viewModel.addItemToCart1(addonDetails!!, this)
                    val event_attributes: HashMap<String, Any> = HashMap()
                    addonDetails!!.name?.let { it1 -> event_attributes.put("Addon Name", it1) }
                    event_attributes.put("Addon Price", addonDetails!!.price)
//                            event_attributes.put(
//                                "Addon Discounted Price",
//                                getDiscountedPrice(addonDetails!!.price, addonDetails!!.discount_percent)
//                            )
                    // event_attributes.put("Addon Discount %", addonDetails!!.discount_percent)
                    //  event_attributes.put("Addon Validity", 1)
                    event_attributes.put("Addon Feature Key", addonDetails!!.boost_widget_key)
//                            addonDetails!!.target_business_usecase?.let { it1 ->
//                                event_attributes.put(
//                                    "Addon Tag",
//                                    it1
//                                )
//                            }
//                            WebEngageController.trackEvent(
//                                ADDONS_MARKETPLACE_FEATURE_ADDED_TO_CART,
//                                ADDONS_MARKETPLACE,
//                                event_attributes
//                            )
//                            if (addonDetails!!.feature_code == "CUSTOM_PAYMENTGATEWAY")
//                                WebEngageController.trackEvent(
//                                    SELF_BRANDED_PAYMENT_GATEWAY_REQUESTED,
//                                    SELF_BRANDED_PAYMENT_GATEWAY,
//                                    NO_EVENT_VALUE
//                                )
//                            badgeNumber = badgeNumber + 1
//
//                            Constants.CART_VALUE = badgeNumber
//
//
//                            add_item_to_cart.background = ContextCompat.getDrawable(
//                                applicationContext,
//                                R.drawable.grey_button_click_effect
//                            )
//                            add_item_to_cart.setTextColor(getResources().getColor(R.color.tv_color_BB))
//                            add_item_to_cart.text = getString(R.string.added_to_cart)
                    itemInCartStatus = true




                }
            }


            dismiss()
        }

    }
}