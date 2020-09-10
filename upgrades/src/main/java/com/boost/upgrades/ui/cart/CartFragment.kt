package com.boost.upgrades.ui.cart

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.CartAddonsAdaptor
import com.boost.upgrades.adapter.CartPackageAdaptor
import com.boost.upgrades.adapter.CartRenewalAdaptor
import com.boost.upgrades.data.api_model.GetAllFeatures.response.Bundles
import com.boost.upgrades.data.api_model.GetAllFeatures.response.ExtendedProperty
import com.boost.upgrades.data.api_model.GetAllFeatures.response.IncludedFeature
import com.boost.upgrades.data.api_model.GetAllFeatures.response.PrimaryImage
//import com.boost.upgrades.data.api_model.PurchaseOrder.request.*
import com.boost.upgrades.data.api_model.PurchaseOrder.requestV2.*
import com.boost.upgrades.data.api_model.PurchaseOrder.response.CreatePurchaseOrderResponse
import com.boost.upgrades.data.model.BundlesModel
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.data.model.CouponsModel
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.data.renewalcart.CreateCartStateRequest
import com.boost.upgrades.data.renewalcart.RenewalPurchasedRequest
import com.boost.upgrades.data.renewalcart.RenewalResult
import com.boost.upgrades.database.LocalStorage
import com.boost.upgrades.interfaces.CartFragmentListener
import com.boost.upgrades.ui.home.HomeFragment
import com.boost.upgrades.ui.packages.PackageFragment
import com.boost.upgrades.ui.payment.PaymentFragment
import com.boost.upgrades.ui.popup.CouponPopUpFragment
import com.boost.upgrades.ui.popup.GSTINPopUpFragment
import com.boost.upgrades.ui.popup.TANPopUpFragment
import com.boost.upgrades.utils.*
import com.boost.upgrades.utils.Constants.Companion.COUPON_POPUP_FRAGEMENT
import com.boost.upgrades.utils.Constants.Companion.GSTIN_POPUP_FRAGEMENT
import com.boost.upgrades.utils.Constants.Companion.TAN_POPUP_FRAGEMENT
import com.boost.upgrades.utils.DateUtils.parseDate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.cart_fragment.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CartFragment : BaseFragment(), CartFragmentListener {

  lateinit var root: View

  lateinit var localStorage: LocalStorage

  var customerId: String = ""

  lateinit var cartList: ArrayList<CartModel>

  lateinit var featuresList: List<FeaturesModel>

  lateinit var bundlesList: List<BundlesModel>

  lateinit var renewalList: List<RenewalResult>

  var bundles_in_cart = false
  var default_validity_months = 1

  var total = 0.0

  var grandTotal = 0.0

  var GSTINNumber: String? = null
  var TANNumber: String? = null

  var taxValue = 0.0

  var validCouponCode: CouponsModel? = null

  var couponDiscountAmount = 0.0

  lateinit var progressDialog: ProgressDialog

  //    private var cartAdapter = CartAdapter(ArrayList())
  lateinit var cartPackageAdaptor: CartPackageAdaptor
  lateinit var cartAddonsAdaptor: CartAddonsAdaptor
  lateinit var cartRenewalAdaptor: CartRenewalAdaptor

  val couponPopUpFragment = CouponPopUpFragment()

  val gstinPopUpFragment = GSTINPopUpFragment()

  val tanPopUpFragment = TANPopUpFragment()

  lateinit var prefs: SharedPrefs

  companion object {
    fun newInstance() = CartFragment()
  }

  private lateinit var viewModel: CartViewModel

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    root = inflater.inflate(R.layout.cart_fragment, container, false)
    localStorage = LocalStorage.getInstance(context!!)!!

    progressDialog = ProgressDialog(requireContext())

    cartPackageAdaptor = CartPackageAdaptor(ArrayList(), this)
    cartAddonsAdaptor = CartAddonsAdaptor(ArrayList(), this)
    cartRenewalAdaptor = CartRenewalAdaptor(ArrayList(), this)
    prefs = SharedPrefs(activity as UpgradeActivity)

    WebEngageController.trackEvent("ADDONS_MARKETPLACE Cart Initialised", "ADDONS_MARKETPLACE Cart", "")

    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(requireActivity()).get(CartViewModel::class.java)

    initializePackageRecycler()
    initializeAddonsRecycler()
    initializeRenewalRecycler()
    initializeErrorObserver()
    initMvvM()
    checkRenewalItemDeepLinkClick()

    //show applyed coupon code
    if (prefs.getApplyedCouponDetails() != null) {
      validCouponCode = prefs.getApplyedCouponDetails()
      discount_coupon_title.text = validCouponCode!!.coupon_key
      cart_apply_coupon.visibility = View.GONE
      discount_coupon_remove.visibility = View.VISIBLE
    } else {
      validCouponCode = null
      discount_coupon_remove.visibility = View.GONE
      cart_apply_coupon.visibility = View.VISIBLE
      discount_coupon_title.text = "Discount coupon"
    }

    discount_coupon_remove.setOnClickListener {
      discount_coupon_remove.visibility = View.GONE
      cart_apply_coupon.visibility = View.VISIBLE
      discount_coupon_title.text = "Discount coupon"

      //clear coupon
      validCouponCode = null

      //remove saved orderdetails and coupondetails from prefs
      prefs.storeCartOrderInfo(null)
      prefs.storeApplyedCouponDetails(null)

      totalCalculation()
    }

    cart_continue_submit.setOnClickListener {
      if (prefs.getCartOrderInfo() != null) {
        proceedToPayment(prefs.getCartOrderInfo()!!)
      } else if (total > 0 && ::cartList.isInitialized && ::featuresList.isInitialized || ::renewalList.isInitialized) {
        val renewalItems = cartList.filter { it.item_type == "renewals" } as? List<CartModel>
        if (renewalItems.isNullOrEmpty().not()) {
          createCartStateRenewal(renewalItems)
        } else createPurchaseOrder(null)
      } else {
        Toasty.error(requireContext(), "Invalid items found in the cart. Please re-launch the Marketplace.", Toast.LENGTH_SHORT).show()
      }
    }

    back_button12.setOnClickListener {
      (activity as UpgradeActivity).onBackPressed()
    }

    cart_view_details.setOnClickListener {
      cart_main_scroller.post {
        cart_main_scroller.fullScroll(View.FOCUS_DOWN)
      }
    }

    cart_apply_coupon.setOnClickListener {
      couponPopUpFragment.show(
          (activity as UpgradeActivity).supportFragmentManager,
          COUPON_POPUP_FRAGEMENT
      )
    }

    cart_apply_coupon.setOnClickListener {
      couponPopUpFragment.show(
          (activity as UpgradeActivity).supportFragmentManager,
          COUPON_POPUP_FRAGEMENT
      )
    }
    enter_gst_number.setOnClickListener {
      gstinPopUpFragment.show(
          (activity as UpgradeActivity).supportFragmentManager,
          GSTIN_POPUP_FRAGEMENT
      )
    }

    remove_gstin_number.setOnClickListener {
      GSTINNumber = null
      gstin_layout1.visibility = View.VISIBLE
      gstin_layout2.visibility = View.GONE
      fill_in_gstin_value.text = ""
    }

    enter_tan_number.setOnClickListener {
      tanPopUpFragment.show(
          (activity as UpgradeActivity).supportFragmentManager,
          TAN_POPUP_FRAGEMENT
      )
    }

    all_recommended_addons.setOnClickListener {
      (activity as UpgradeActivity).goBackToRecommentedScreen()
    }

    months_validity_edit_inc.setOnClickListener {
      if (!bundles_in_cart) {
        default_validity_months++
        months_validity.text = default_validity_months.toString() + " months"

        prefs.storeCartOrderInfo(null)
        totalCalculation()

        Toasty.success(requireContext(), "Validity increased by 1 month.", Toast.LENGTH_SHORT, true).show()
      }
    }

    months_validity_edit_dsc.setOnClickListener {
      if (!bundles_in_cart) {
        if (default_validity_months > 1) {
          default_validity_months--

          prefs.storeCartOrderInfo(null)
          totalCalculation()

          Toasty.warning(requireContext(), "Validity reduced by 1 month.", Toast.LENGTH_SHORT, true).show()
        }
        if (default_validity_months > 1)
          months_validity.text = default_validity_months.toString() + " months"
        else
          months_validity.text = default_validity_months.toString() + " month"
      }
    }

  }

  private fun createCartStateRenewal(renewalItems: List<CartModel>?) {
    val widgetList = arrayListOf<com.boost.upgrades.data.renewalcart.Widget>()
    renewalItems?.forEach { widgetList.add(com.boost.upgrades.data.renewalcart.Widget(it.item_id, it.boost_widget_key)) }

    val request = CreateCartStateRequest((activity as UpgradeActivity).clientid, (activity as UpgradeActivity).fpid, "RENEWAL", widgetList)
    viewModel.createCartStateRenewal(request)
    viewModel.createCartRenewalResult().observeOnce(Observer { createPurchaseOrder(it.cartStateId) })
  }

  private fun checkRenewalItemDeepLinkClick() {
    val ac = (activity as UpgradeActivity)
    if (ac.isBackCart.not() && (ac.isDeepLink || ac.isOpenCardFragment)) {
      val currentDate = DateUtils.getCurrentDate().parseDate(DateUtils.FORMAT_MM_DD_YYYY)
      val deepLinkDay = (activity as UpgradeActivity).deepLinkDay
      val dateAmount = DateUtils.getAmountDate(deepLinkDay).parseDate(DateUtils.FORMAT_MM_DD_YYYY)
      val request = if (deepLinkDay <= -1) {
        RenewalPurchasedRequest(floatingPointId = (activity as UpgradeActivity).fpid, clientId = (activity as UpgradeActivity).clientid,
            widgetStatus = RenewalPurchasedRequest.WidgetStatus.EXPIRED.name, dateFilter = RenewalPurchasedRequest.DateFilter.EXPIRY_DATE.name,
            startDate = dateAmount, endDate = currentDate)
      } else {
        RenewalPurchasedRequest(floatingPointId = (activity as UpgradeActivity).fpid, clientId = (activity as UpgradeActivity).clientid,
            widgetStatus = RenewalPurchasedRequest.WidgetStatus.ACTIVE.name, nextWidgetStatus = RenewalPurchasedRequest.NextWidgetStatus.RENEWAL.name,
            dateFilter = RenewalPurchasedRequest.DateFilter.EXPIRY_DATE.name, startDate = currentDate, endDate = dateAmount)
      }

      viewModel.allPurchasedWidgets(request)
      viewModel.renewalResult().observeOnce(Observer { result ->
        renewalList = result?.filter { it.renewalStatus() == RenewalResult.RenewalStatus.PENDING.name } ?: ArrayList()
        if (renewalList.isNotEmpty()) {
          val list = arrayListOf<CartModel>()
          renewalList.forEach { renewal -> list.add(saveRenewalData(renewal)) }
          cartList = list
          total_months_layout.visibility = View.GONE
          renewal_layout.visibility = View.VISIBLE
          addons_layout.visibility = View.GONE
          package_layout.visibility = View.GONE
          updateRenewal(cartList)
          totalCalculation()
        } else {
          Toasty.warning(requireContext(), "Renewal order not found").show()
          ac.isBackCart = true
          (activity as UpgradeActivity).onBackPressed()
        }
      })
    } else loadData()
  }

  private fun saveRenewalData(renewal: RenewalResult): CartModel {
    var minMonth = 1
    if (renewal.expiry?.key == "DAYS") minMonth = renewal.expiry?.valueDays()?.div(30) ?: 1
    return CartModel(renewal.widgetId ?: "", renewal.widgetKey ?: "", "",
        renewal.name ?: "", renewal.desc ?: "", renewal.images?.firstOrNull() ?: "",
        renewal.netPrice ?: 0.0, renewal.price ?: 0.0, (renewal.discount ?: 0.0).toInt(),
        1, minMonth, "renewals", null)
  }

  fun updateRenewal(renewal: List<CartModel>) {
    cartRenewalAdaptor.renewalNotify(renewal)
    cartRenewalAdaptor.notifyDataSetChanged()
  }

  private fun createPurchaseOrder(cartStateId: String?) {
    var couponCode: String? = null
    var couponDiscountPercentage: Int = 0
    if (validCouponCode != null) {
      couponCode = validCouponCode!!.coupon_key
      couponDiscountPercentage = validCouponCode!!.discount_percent
    }
    val purchaseOrders = ArrayList<PurchaseOrder>()
    val renewalItems = cartList.filter { it.item_type == "renewals" } as? List<CartModel>
    if (renewalItems.isNullOrEmpty().not()) {
      val widgetList = ArrayList<Widget>()
      var netAmount = 0.0
      renewalItems?.forEach { item ->
        val data = renewalList.firstOrNull { it.widgetId == item.item_id }
        netAmount += item.price
        val widget = Widget(data?.category ?: "", ConsumptionConstraint("DAYS", 30), "", item.description_title,
            item.discount, Expiry("DAYS", 30), listOf(), true, true, item.item_name ?: "",
            item.price, item.MRPPrice, null, 1, "MONTHLY", item.boost_widget_key ?: "", item.item_id)
        widgetList.add(widget)
      }
      purchaseOrders.add(PurchaseOrder(couponCode, couponDiscountPercentage, null, netAmount, widgetList))
    } else {
      val featureWidgetList = ArrayList<Widget>()
      var featureNetPrice = 0.0
      for (item in cartList) {
//        val widgetList = ArrayList<Widget>()
        val bundleWidgetList = ArrayList<Widget>()
        var extendProps: List<ExtendedProperty>? = null
        var outputExtendedProps = ArrayList<Property>()
        var extraPurchaseOrderDetails: ExtraPurchaseOrderDetails? = null
        var bundleNetPrice = 0.0
        var bundleDiscount = 0

        if (item.extended_properties != null && item.extended_properties!!.length > 0) {
          try {
            val objectType = object : TypeToken<List<ExtendedProperty>>() {}.type
            extendProps = Gson().fromJson<List<ExtendedProperty>>(item.extended_properties, objectType)

            if (extendProps != null) {
              for (prop in extendProps) {
                if (prop.key != null && prop.value != null) {
                  outputExtendedProps.add(Property(
                      Key = prop.key,
                      Value = prop.value
                  ))
                }
              }

            }
          } catch (ex: Exception) {
            ex.printStackTrace()
          }
        }


        if (item.item_type.equals("features")) {
          var mrp_price = item.MRPPrice
          val discount = 100 - item.discount
          var netPrice = (discount * mrp_price) / 100

          var validity_days = 30
          var net_quantity = 1

          if (!bundles_in_cart && default_validity_months > 1) {
            validity_days = 30 * default_validity_months

            netPrice = netPrice * default_validity_months
            net_quantity = default_validity_months
            mrp_price = mrp_price * default_validity_months
          }

          //adding widget netprice to featureNetprice to get GrandTotal In netPrice.
          featureNetPrice += netPrice

          featureWidgetList.add(Widget(
              "",
              ConsumptionConstraint(
                  "DAYS",
                  30
              ),
              "",
              item.description_title,
              item.discount,
              Expiry(
                  "DAYS",
                  validity_days
              ),
              listOf(),
              true,
              true,
              item.item_name!!,
              netPrice,
              mrp_price,
              if (outputExtendedProps.size > 0) outputExtendedProps else null,
              net_quantity,
              "MONTHLY",
              item.boost_widget_key!!,
              item.item_id
          ))
        } else if (item.item_type.equals("bundles")) {
          if (::bundlesList.isInitialized && bundlesList.size > 0) {
            for (singleBundle in bundlesList) {
              if (singleBundle.bundle_id.equals(item.item_id)) {
                val outputBundleProps: ArrayList<Property> = arrayListOf()
                outputBundleProps.add(Property(
                    Key = singleBundle.bundle_id,
                    Value = singleBundle.name!!
                ))
                extraPurchaseOrderDetails = ExtraPurchaseOrderDetails(
                    null,
                    singleBundle.primary_image,
                    singleBundle.name,
                    outputBundleProps)
                bundleDiscount = singleBundle.overall_discount_percent
                val includedFeatures = Gson().fromJson<List<IncludedFeature>>(singleBundle.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
                for (singleIndludedFeature in includedFeatures) {
                  for (singleFeature in featuresList) {
                    if (singleIndludedFeature.feature_code.equals(singleFeature.feature_code)) {

                      val netPrice = (singleFeature.price - ((singleFeature.price * singleIndludedFeature.feature_price_discount_percent) / 100))

                      //adding bundle netPrice
                      bundleNetPrice += netPrice * singleBundle.min_purchase_months
                      bundleWidgetList.add(Widget(
                          "",
                          ConsumptionConstraint(
                              "DAYS",
                              30 * singleBundle.min_purchase_months
                          ),
                          "",
                          singleFeature.description_title,
                          singleIndludedFeature.feature_price_discount_percent,
                          Expiry(
                              "DAYS",
                              30 * singleBundle.min_purchase_months
                          ),
                          listOf(),
                          true,
                          true,
                          singleFeature.name!!,
                          netPrice.toDouble() * singleBundle.min_purchase_months,
                          singleFeature.price.toDouble() * singleBundle.min_purchase_months,
                          if (outputExtendedProps.size > 0) outputExtendedProps else null,
                          1,
                          "MONTHLY",
                          singleFeature.boost_widget_key,
                          singleFeature.feature_id
                      ))
                      break
                    }
                  }
                }
                //bundle level discount
                if (bundleDiscount > 0) {
                  bundleNetPrice = Math.round(bundleNetPrice - ((bundleNetPrice * bundleDiscount) / 100)).toDouble()
                }
                break
              }
            } //bundle forloop completion

            purchaseOrders.add(
                PurchaseOrder(
                    couponCode,
                    bundleDiscount, //Discount of the bundle/package/order without tax.
                    extraPurchaseOrderDetails,
                    bundleNetPrice,
                    bundleWidgetList
                )
            )

          }// bundle end
        }//bundle type if end


//        purchaseOrders.add(
//            PurchaseOrder(
//                couponCode,
//                bundleDiscount, //Discount of the bundle/package/order without tax.
//                extraPurchaseOrderDetails,
//                bundleNetPrice,
//                widgetList
//            )
//        )
      }// end of cart item for loop

      if(featureWidgetList.size>0) {    //this is used only for single widgets
        purchaseOrders.add(
                PurchaseOrder(
                        couponCode,
                        0,
                        null,
                        featureNetPrice,
                        featureWidgetList
                )
        )
      }
    } // if end of new order

    var keysToBeActivated = ArrayList<String>()

    for (item in purchaseOrders) {
      if (item.Widgets != null) {
        for (widget in item.Widgets) {
          if (!keysToBeActivated.contains(widget.WidgetKey)) {
            keysToBeActivated.add(widget.WidgetKey)
          }
        }
      }
    }
    prefs.storeFeatureKeysInLastOrder(keysToBeActivated.toMutableSet())
    prefs.storeFeaturesCountInLastOrder(purchaseOrders.count())

    viewModel.InitiatePurchaseOrder(
        CreatePurchaseOrderV2(
            (activity as UpgradeActivity).clientid,
            (activity as UpgradeActivity).fpid!!,
            PaymentDetails(
                "INR",
                couponDiscountPercentage, //[Double] Discount Percentage of the the payment(Coupon code discount)
                "RAZORPAY",
                TaxDetails(
                    GSTINNumber,
                    0,
                    null,
                    18),
                grandTotal),
            (if (cartStateId.isNullOrEmpty()) "NEW" else "RENEWAL"),
            purchaseOrders,
            (cartStateId ?: "")
        )
    )
  }

  fun loadData() {
    viewModel.getCartItems()
    viewModel.getAllFeatures()
    viewModel.getAllBundles()
  }

  @SuppressLint("FragmentLiveDataObserve")
  fun initMvvM() {
    viewModel.cartResult().observe(this, Observer {
      if (it.isNullOrEmpty().not()) {
        cartList = it as ArrayList<CartModel>
        WebEngageController.trackEvent("ADDONS_MARKETPLACE Full_Cart Loaded", "Cart Size:" + it.size, "")
        empty_cart.visibility = View.GONE
        cart_main_layout.visibility = View.VISIBLE
        val features = arrayListOf<CartModel>()
        val bundles = arrayListOf<CartModel>()
        for (items in it) {
          if (items.item_type.equals("features")) {
            features.add(items)
          } else if (items.item_type.equals("bundles")) {
            bundles.add(items)
          }
        }
        if (features.size > 0) {
          updateAddons(features)
          addons_layout.visibility = View.VISIBLE
        } else {
          addons_layout.visibility = View.GONE
        }
        if (bundles.size > 0) {
          bundles_in_cart = true
          updatePackage(bundles)
          for (bundle in bundles) {
            if (bundle.min_purchase_months > default_validity_months)
              default_validity_months = bundle.min_purchase_months
          }
          if (default_validity_months > 0)
            months_validity.text = default_validity_months.toString() + " months"
          else
            months_validity.text = default_validity_months.toString() + " month"
          months_validity_edit_inc.visibility = View.GONE
          months_validity_edit_dsc.visibility = View.GONE
          package_layout.visibility = View.VISIBLE
        } else {
          bundles_in_cart = false
          default_validity_months = 1
          months_validity.text = default_validity_months.toString() + " month"
          months_validity_edit_inc.visibility = View.VISIBLE
          months_validity_edit_dsc.visibility = View.VISIBLE
          package_layout.visibility = View.GONE
        }
        totalCalculation()
      } else {
        WebEngageController.trackEvent("ADDONS_MARKETPLACE Empty_Cart Loaded", "ADDONS_MARKETPLACE Empty_Cart Loaded", "")
        empty_cart.visibility = View.VISIBLE
        cart_main_layout.visibility = View.GONE

        months_validity_edit_inc.visibility = View.GONE
        months_validity_edit_dsc.visibility = View.GONE
        months_validity.text = "- -"

        //clear coupon
        validCouponCode = null

        //remove saved orderdetails from prefs
        prefs.storeCartOrderInfo(null)
        prefs.storeApplyedCouponDetails(null)

        //clear viewModel data
        viewModel.clearValidCouponResult()
      }
    })

    viewModel.getPurchaseOrderResponse().observe(this, Observer {
      if (it != null) {
        prefs.storeLatestPurchaseOrderId(it.Result.OrderId)
        prefs.storeLatestPurchaseOrderTotalPrice(it.Result.TotalPrice.toFloat())

        //original cart amount and coupon discount added to shareprefs
        prefs.storeCartOriginalAmount((total + couponDiscountAmount).toFloat())
        prefs.storeCouponDiscountPercentage(if (validCouponCode == null) 0 else validCouponCode!!.discount_percent)

        //saving cartOrderInfo
        prefs.storeCartOrderInfo(it)

        //store transaction id for cart
        prefs.storeTransactionIdFromCart(it.Result.TransactionId)

        proceedToPayment(it)
      }
    })

    viewModel.getLoaderStatus().observe(this, Observer {
      if (it) {
        val status = viewModel.getAPIRequestStatus()
        progressDialog.setMessage(status)
        progressDialog.setCancelable(false) // disable dismiss by tapping outside of the dialog
        progressDialog.show()
      } else {
        progressDialog.dismiss()
      }
    })

    viewModel.getGSTIN().observe(this, Observer {
      if (it != null) {
        Log.i("getGSTIN >> ", it)
        GSTINNumber = it
        gstin_layout1.visibility = View.GONE
        gstin_layout2.visibility = View.VISIBLE
        fill_in_gstin_value.text = it
      }
    })

    viewModel.getTAN().observe(this, Observer {
      if (it != null) {
        Log.i("getTAN >> ", it)
        TANNumber = it
        enter_tan_number.visibility = View.GONE
        entered_tan_number.visibility = View.VISIBLE
        entered_tan_number.text = it
      }
    })

    //getting all features
    viewModel.updateAllFeaturesResult().observe(this, Observer {
      featuresList = it
    })

    //getting all bunles
    viewModel.updateAllBundlesResult().observe(this, Observer {
      bundlesList = it
    })

    //getting valid Coupon Code
    viewModel.updateValidCouponResult().observe(this, Observer {
      if (it != null) {
        //clear stored cartOrderInfo
        prefs.storeCartOrderInfo(null)

        //save coupon Details
        prefs.storeApplyedCouponDetails(it)

        validCouponCode = it
        discount_coupon_title.text = validCouponCode!!.coupon_key
        cart_apply_coupon.visibility = View.GONE
        discount_coupon_remove.visibility = View.VISIBLE
        totalCalculation()
      } else {
        validCouponCode = null
      }
    })
  }

  fun updatePackage(features: List<CartModel>) {
    cartPackageAdaptor.addupdates(features)
    cartPackageAdaptor.notifyDataSetChanged()
  }

  fun updateAddons(features: List<CartModel>) {
    cartAddonsAdaptor.addupdates(features)
    cartAddonsAdaptor.notifyDataSetChanged()
  }


  private fun initializePackageRecycler() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    cart_package_recycler.apply {
      layoutManager = gridLayoutManager
      cart_package_recycler.adapter = cartPackageAdaptor

    }
  }

  private fun initializeAddonsRecycler() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    cart_addons_recycler.apply {
      layoutManager = gridLayoutManager
      cart_addons_recycler.adapter = cartAddonsAdaptor
    }
  }

  private fun initializeRenewalRecycler() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    cart_renewal_recycler.apply {
      layoutManager = gridLayoutManager
      adapter = cartRenewalAdaptor
    }
  }

  private fun initializeErrorObserver() {
    viewModel.updatesError().observeOnce(Observer { Toasty.error(requireContext(), it, Toast.LENGTH_SHORT).show() })
  }


  fun totalCalculation() {
    if (::cartList.isInitialized) {
      total = 0.0
      var couponDisount = 0
      if (validCouponCode != null) {
        couponDisount = validCouponCode!!.discount_percent
        coupon_discount_title.text = "Coupon discount(" + couponDisount.toString() + "%)"
      } else {
        coupon_discount_title.text = "Coupon discount"
      }
      if (cartList != null && cartList.size > 0) {
        for (item in cartList) {
          if (!bundles_in_cart && item.item_type.equals("features"))
            total += (item.price * default_validity_months)
          else
            total += item.price
        }
        cart_amount_value.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(total)
        couponDiscountAmount = total * couponDisount / 100
        coupon_discount_value.text = "-₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(couponDiscountAmount)
        total -= couponDiscountAmount
        val temp = (total * 18) / 100
        taxValue = Math.round(temp * 100) / 100.0
        grandTotal = (Math.round((total + taxValue) * 100) / 100.0)
        igst_value.text = "+₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(taxValue)
        order_total_value.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)
        cart_grand_total.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)
        footer_grand_total.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)
      }
    }
  }


  override fun deleteCartAddonsItem(itemID: String) {
    viewModel.deleteCartItems(itemID)
    //remove saved orderdetails from prefs
    prefs.storeCartOrderInfo(null)
  }

  override fun showBundleDetails(itemID: String) {
    var selectedBundle: Bundles? = null
    for (item in bundlesList) {
      if (item.bundle_id == itemID) {
        val temp = Gson().fromJson<List<IncludedFeature>>(item.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
        selectedBundle = Bundles(
            item.bundle_id,
            temp,
            item.min_purchase_months,
            item.name,
            item.overall_discount_percent,
            PrimaryImage(item.primary_image),
            item.target_business_usecase,
            Gson().fromJson<List<String>>(item.exclusive_to_categories, object : TypeToken<List<String>>() {}.type),
            null
        )
        break
      }
    }
    if (selectedBundle != null) {
      val packageFragment = PackageFragment.newInstance()
      val args = Bundle()
      args.putString("bundleData", Gson().toJson(selectedBundle))
      args.putBoolean("showCartIcon", false)
      packageFragment.arguments = args
      (activity as UpgradeActivity).addFragment(packageFragment, Constants.PACKAGE_FRAGMENT)
    } else {
      Toasty.info(requireContext(), "Something went wrong!! Try Later...").show()
    }
  }

  override fun actionClickRenewal(position: Int, renewalResult: CartModel, action: Int) {
    when (action) {
      1 -> {
        if (cartList.size > position) {
          cartList.removeAt(position)
          cartRenewalAdaptor.renewalNotify(cartList)
          cartRenewalAdaptor.notifyDataSetChanged()
          totalCalculation()
          prefs.storeCartOrderInfo(null)
          if (cartList.isEmpty()) {
            empty_cart.visibility = View.VISIBLE
            cart_main_layout.visibility = View.GONE
          }
        }
      }
    }
  }

  fun proceedToPayment(result: CreatePurchaseOrderResponse) {
    val paymentFragment = PaymentFragment.newInstance()
    val args = Bundle()
    args.putString("customerId", customerId)
    args.putDouble("amount", result.Result.TotalPrice)// pass in currency subunits. For example, paise. Amount: 1000 equals ₹10
    args.putString("order_id", result.Result.OrderId)
    args.putString("transaction_id", result.Result.TransactionId)
    args.putString("email", (activity as UpgradeActivity).email)
    args.putString("currency", "INR")
    args.putString("contact", (activity as UpgradeActivity).mobileNo)
    paymentFragment.arguments = args
    (activity as UpgradeActivity).addFragment(
        paymentFragment,
        Constants.PAYMENT_FRAGMENT
    )
  }

  fun isRenewalListNotEmpty(): Boolean {
    return ::cartList.isInitialized && cartList.isNotEmpty() && ::renewalList.isInitialized && renewalList.isNotEmpty()
  }
}
