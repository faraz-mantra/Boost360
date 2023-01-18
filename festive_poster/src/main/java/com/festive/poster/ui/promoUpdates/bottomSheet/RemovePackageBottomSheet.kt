package com.festive.poster.ui.promoUpdates.bottomSheet

import android.app.Application
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.DataLoader
import com.festive.poster.R
import com.framework.BaseApplication
import com.framework.pref.UserSessionManager
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.remove_package_bottomsheet1.*
import java.util.*


class RemovePackageBottomSheet(var addonsListInCart: List<String>? = null, val addonID: String) : DialogFragment() {
    lateinit var root: View
    var addonDetails: FeaturesModel? = null

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.remove_package_bottomsheet1, container, false)
        getAddonDetails()
        return root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        close_btn.setOnClickListener {
            dismiss()
        }
        yes_button.setOnClickListener {
            if(addonDetails != null) {
                removeOtherBundlesAndAddExistingAddons(addonsListInCart!!, addonDetails!!)
                yes_button.isClickable = false
                yes_button.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.button_spherical_grey
                )
                yes_button.setTextColor(
                    requireContext().getResources().getColor(R.color.tv_color_BB)
                )
            }else{
                Toast.makeText(requireContext(), "Something went wrong.Try Later!!", Toast.LENGTH_LONG).show()
            }
        }
        goToCart_button.setOnClickListener {
            navigateToCart()
            dismiss()
        }
    }

    fun navigateToCart() {
        val session =  UserSessionManager(BaseApplication.instance)
        val intent = Intent(requireContext(), Class.forName("com.boost.cart.CartActivity"))
        intent.putExtra("expCode", session.fP_AppExperienceCode)
        intent.putExtra("isDeepLink", true)
        intent.putExtra("fpName", session.fPName)
        intent.putExtra("fpid", session.fPID)
        intent.putExtra("isOpenCardFragment", false)
        intent.putStringArrayListExtra(
            "userPurchsedWidgets",
            session.getStoreWidgets() as ArrayList<String>
        )
        if (session.userProfileEmail != null) {
            intent.putExtra("email", session.userProfileEmail)
        } else {
            intent.putExtra("email", getString(R.string.ria_customer_mail))
        }
        if (session.userPrimaryMobile != null) {
            intent.putExtra("mobileNo", session.userPrimaryMobile)
        } else {
            intent.putExtra("mobileNo", getString(R.string.ria_customer_number))
        }
        intent.putExtra("profileUrl", session.fPLogo)
        startActivity(intent)
    }

    fun spannableString() {
        val addonName = addonDetails!!.name
        val stringValue = SpannableString(
            "You are trying to add “$addonName“ but your cart contains a pack that includes this feature. Please go to cart and remove the pack if you want to add this feature."
        )

        stringValue.setSpan(
            StyleSpan(Typeface.BOLD),
            23,
            23 + addonName!!.length,
            0
        )
        desc.text = stringValue
    }

    fun removeOtherBundlesAndAddExistingAddons(addonsListInCart: List<String>, updatesModel: FeaturesModel){
        Completable.fromAction {
            AppDatabase.getInstance(Application())!!.cartDao().deleteCartItemsInList(addonsListInCart)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                val discount = 100 - updatesModel.discount_percent
                val paymentPrice = ((discount * updatesModel.price) / 100)
                val mrpPrice = updatesModel.price
                val cartItem = CartModel(
                    updatesModel.feature_id,
                    updatesModel.boost_widget_key,
                    updatesModel.feature_code,
                    updatesModel.name,
                    updatesModel.description,
                    updatesModel.primary_image,
                    paymentPrice,
                    mrpPrice,
                    updatesModel.discount_percent,
                    1,
                    1,
                    "features",
                    updatesModel.extended_properties,
                    updatesModel.widget_type ?: "",null
                )
                Completable.fromAction {
                    AppDatabase.getInstance(requireActivity().application)!!.cartDao()
                        .insertToCart(cartItem)
                }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
//                        DataLoader.updateCartItemsToFirestore(requireActivity().application)
                    }
                    .doOnError {
                        it.printStackTrace()
                    }
                    .subscribe()
            }
            .doOnError {
                Toast.makeText(requireContext(), "Not able to Delete the Add-ons!!", Toast.LENGTH_LONG).show()
            }
            .subscribe()
    }

    fun getAddonDetails(){
        CompositeDisposable().add(
            AppDatabase.getInstance(requireActivity().application)!!
                .featuresDao()
                .getFeaturesItemByWidgetKey(addonID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    addonDetails = it
                    spannableString()
                },{
                    Toast.makeText(requireContext(), "This Feature Not Available For Your account", Toast.LENGTH_LONG).show()
                    it.printStackTrace()
                })
        )
    }

}