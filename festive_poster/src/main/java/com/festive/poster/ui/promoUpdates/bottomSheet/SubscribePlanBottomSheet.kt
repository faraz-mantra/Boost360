package com.festive.poster.ui.promoUpdates.bottomSheet

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.festive.poster.R
import com.festive.poster.constant.Constants
import com.festive.poster.databinding.BsheetSubscribePlanValidityBinding
import com.festive.poster.models.response.UpgradeGetDataFeature
import com.festive.poster.models.response.UpgradeGetDataResponse
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.utils.MarketPlaceUtils
import com.festive.poster.viewmodels.FestivePosterViewModel
import com.framework.BaseApplication
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SubscribePlanBottomSheet : BaseBottomSheetDialog<BsheetSubscribePlanValidityBinding, FestivePosterViewModel>() {

    private var feature_promo: UpgradeGetDataFeature?=null
    var session:UserSessionManager?=null
    enum class Validity{
        MONTHLY,
        YEARLY
    }
    var validity:Validity?=null
    companion object {
        @JvmStatic
        fun newInstance(): SubscribePlanBottomSheet {
            val bundle = Bundle().apply {}
            val fragment = SubscribePlanBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_subscribe_plan_validity
    }

    override fun getViewModelClass(): Class<FestivePosterViewModel> {
        return FestivePosterViewModel::class.java
    }

    override fun onCreateView() {
        /*binding?.btnGetPack?.setOnClickListener {
            dismiss()
        }*/
        getPriceOfPosterPacks()
        session = UserSessionManager(requireActivity())


        binding?.btnGetPack?.setOnClickListener {
            addItemToCart()
        }

        binding?.rivCloseBottomSheet?.setOnClickListener {
            dismiss()
        }

        binding?.linearRegularAmount?.setOnClickListener {
            updatePackageSelectionUI(0)
        }

        binding?.linearDiscountAmount?.setOnClickListener {
            updatePackageSelectionUI(1)
        }
    }

    private fun getPriceOfPosterPacks() {
        viewModel?.getUpgradeData()?.observeOnce(viewLifecycleOwner, {
            val response = it as? UpgradeGetDataResponse
            response?.let {
                    feature_promo= response.Data.firstOrNull()?.features?.find { feature ->
                        feature.feature_code == Constants.PROMO_WIDGET_KEY
                    }

                if (feature_promo!=null){
                    binding!!.progressBar.gone()
                    binding!!.dataLayout.visible()
                    binding!!.tvMonthPrice.text = getString(R.string.placeholder_per_month,feature_promo!!.price.toInt())
                    binding!!.tvYearPrice.text = getString(R.string.placeholder_per_year,feature_promo!!.price.times(12).toInt())
                    updatePackageSelectionUI(1)
                }


            }
        })
    }

    fun addItemToCart() {
        feature_promo?.let {
            val price = if (validity==Validity.MONTHLY){
                it.price
            }else{
                it.price.times(12)
            }
            val minPurchaseMonth = if (validity==Validity.MONTHLY){
                1
            }else{
                12
            }
            val discountPrice = (it.discount_percent*price).div(100)
            val discountedPrice = price.minus(discountPrice)
            val cartItem = CartModel(it._kid,it.boost_widget_key,it.feature_code,it.name,
                it.description_title,null,discountedPrice,price,discountPrice.toInt(),
                1,minPurchaseMonth,"features")

            Completable.fromAction {
                AppDatabase.getInstance(BaseApplication.instance)!!.cartDao()
                    .insertToCart(cartItem)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    MarketPlaceUtils.launchCartActivity(requireActivity())
                }
                .doOnError {

                }
                .subscribe()
        }

    }
    private fun updatePackageSelectionUI(buttonPositionSelected: Int = 0){
        if (buttonPositionSelected == 0){
            validity = Validity.MONTHLY
            binding?.ivSelectionIndicator1?.setImageResource(R.drawable.ic_tick_green_round)
            binding?.ivSelectionIndicator1?.setBackgroundResource(0)
            binding?.ivSelectionIndicator2?.setImageResource(0)
            binding?.ivSelectionIndicator2?.setBackgroundResource(R.drawable.bg_grey_stroke_circle)
            binding?.ivOfferPercent1?.visible()
            binding?.ivOfferPercent2?.gone()
            binding?.linearRegularAmount?.setBackgroundResource(R.drawable.bg_yellow_stroke_et)
            binding?.linearDiscountAmount?.setBackgroundResource(R.drawable.bg_grey_stroke_et)
            binding?.btnGetPack?.text = getString(R.string.get_monthly_pack_for_placeholder,feature_promo?.price?.toInt())
        }else{
            validity =Validity.YEARLY
            binding?.ivSelectionIndicator1?.setImageResource(0)
            binding?.ivSelectionIndicator1?.setBackgroundResource(R.drawable.bg_grey_stroke_circle)
            binding?.ivSelectionIndicator2?.setImageResource(R.drawable.ic_tick_green_round)
            binding?.ivSelectionIndicator2?.setBackgroundResource(0)
            binding?.ivOfferPercent1?.gone()
            binding?.ivOfferPercent2?.visible()
            binding?.linearRegularAmount?.setBackgroundResource(R.drawable.bg_grey_stroke_et)
            binding?.linearDiscountAmount?.setBackgroundResource(R.drawable.bg_yellow_stroke_et)
            binding?.btnGetPack?.text = getString(R.string.get_annual_pack_for_placeholder,feature_promo?.price?.times(12)?.toInt())

        }
    }

    private fun gotoMarketPlace() {
        MarketPlaceUtils.initiateAddonMarketplace(session!!,true,"",Constants.PROMO_WIDGET_KEY,true,requireContext())
    }

}