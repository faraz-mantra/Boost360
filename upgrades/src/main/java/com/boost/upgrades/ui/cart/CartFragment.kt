package com.boost.upgrades.ui.cart

//import com.boost.upgrades.data.api_model.PurchaseOrder.request.*
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
import com.boost.upgrades.ui.packages.PackageFragment
import com.boost.upgrades.ui.payment.PaymentFragment
import com.boost.upgrades.ui.popup.CouponPopUpFragment
import com.boost.upgrades.ui.popup.GSTINPopUpFragment
import com.boost.upgrades.ui.popup.TANPopUpFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.Constants.Companion.COUPON_POPUP_FRAGEMENT
import com.boost.upgrades.utils.Constants.Companion.GSTIN_POPUP_FRAGEMENT
import com.boost.upgrades.utils.Constants.Companion.TAN_POPUP_FRAGEMENT
import com.boost.upgrades.utils.DateUtils.FORMAT_MM_DD_YYYY
import com.boost.upgrades.utils.DateUtils.getAmountDate
import com.boost.upgrades.utils.DateUtils.getCurrentDate
import com.boost.upgrades.utils.DateUtils.parseDate
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.WebEngageController
import com.boost.upgrades.utils.observeOnce
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.cart_fragment.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.math.roundToInt

class CartFragment : BaseFragment(), CartFragmentListener {

  lateinit var root: View

  lateinit var localStorage: LocalStorage

  var customerId: String = ""

  lateinit var cartList: List<CartModel>

  lateinit var featuresList: List<FeaturesModel>
  lateinit var bundlesList: List<BundlesModel>
  lateinit var renewalList: List<RenewalResult>

  var total = 0.0

  var grandTotal = 0.0

  var GSTINNumber: String? = null
  var TANNumber: String? = null

  var taxValue = 0.0

  var validCouponCode: CouponsModel? = null

  var couponDiscountAmount = 0.0
  var isDeepLinking = false

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
    isDeepLinking = (activity as UpgradeActivity).isFirebaseDeepLink
    checkRenewalItemDeepLinkClick()
    initMvvM()
    //show applyed coupon code
    if (prefs.getApplyedCouponDetails() != null) {
      validCouponCode = prefs.getApplyedCouponDetails()
      discount_coupon_title.text = validCouponCode?.coupon_key
      cart_apply_coupon.visibility = View.GONE
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
      (activity as UpgradeActivity).popFragmentFromBackStack()
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

  }

  private fun initializeErrorObserver() {
    viewModel.updatesError().observeOnce(Observer { Toasty.error(requireContext(), it, Toast.LENGTH_SHORT).show() })
  }

  private fun createCartStateRenewal(renewalItems: List<CartModel>?) {
    val widgetList = arrayListOf<com.boost.upgrades.data.renewalcart.Widget>()
    renewalItems?.forEach { widgetList.add(com.boost.upgrades.data.renewalcart.Widget(it.boost_widget_key, it.item_id)) }

    val request = CreateCartStateRequest((activity as UpgradeActivity).clientid, (activity as UpgradeActivity).fpid, "RENEWAL", widgetList)
    viewModel.createCartStateRenewal(request)
    viewModel.createCartRenewalResult().observeOnce(Observer { createPurchaseOrder(it.cartStateId) })
  }

  private fun createPurchaseOrder(cartStateId: String?) {
    var couponCode: String? = null
    var couponDiscountPercentage: Int = 0
    if (validCouponCode != null) {
      couponCode = validCouponCode!!.coupon_key
      couponDiscountPercentage = validCouponCode!!.discount_percent
    }
    val purchaseOrders = ArrayList<PurchaseOrder>()
    for (item in cartList) {
      val widgetList = ArrayList<Widget>()
      var extendProps: List<ExtendedProperty>? = null
      val outputExtendedProps = ArrayList<Property>()
      var extraPurchaseOrderDetails: ExtraPurchaseOrderDetails? = null
      var bundleNetPrice = 0.0
      var bundleDiscount = 0
      if (item.extended_properties.isNullOrEmpty().not()) {
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

      if (item.item_type == "renewals") {
        val data = renewalList.firstOrNull { it.widgetId == item.item_id }
        val discount = 100 - item.discount
        val netPrice = (discount * item.MRPPrice) / 100
        val widget = Widget(data?.category ?: "", ConsumptionConstraint("DAYS", 30), "", item.description_title,
            item.discount, Expiry("DAYS", 30), listOf(), true, true, item.item_name!!,
            netPrice, item.MRPPrice, (if (outputExtendedProps.size > 0) outputExtendedProps else null), 1, "MONTHLY", item.boost_widget_key!!)
        widgetList.add(widget)
      } else if (item.item_type == "features") {
        renewalList
        val discount = 100 - item.discount
        val netPrice = (discount * item.MRPPrice) / 100

        widgetList.add(Widget(
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
                30
            ),
            listOf(),
            true,
            true,
            item.item_name!!,
            netPrice,
            item.MRPPrice,
            if (outputExtendedProps.size > 0) outputExtendedProps else null,
            1,
            "MONTHLY",
            item.boost_widget_key!!
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
                  if (singleIndludedFeature.feature_code.equals(singleFeature.boost_widget_key)) {

                    val netPrice = (singleFeature.price - ((singleFeature.price * singleIndludedFeature.feature_price_discount_percent) / 100))

                    //adding bundle netPrice
                    bundleNetPrice += netPrice * singleBundle.min_purchase_months
                    widgetList.add(Widget(
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
                        singleFeature.boost_widget_key
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
          }
        }
      }


      purchaseOrders.add(
          PurchaseOrder(
              couponCode,
              bundleDiscount, //Discount of the bundle/package/order without tax.
              extraPurchaseOrderDetails,
              bundleNetPrice,
              widgetList
          )
      )
    }

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

    //handling coupon discount
//                var finalPayment: Double = 0.0
//                if(couponDiscountPercentage>0){
//                    finalPayment = grandTotal + couponDiscountAmount
//                }else{
//                    finalPayment = grandTotal
//                }

    viewModel.InitiatePurchaseOrder(
//                        CreatePurchaseOrderRequest(
//                                (activity as UpgradeActivity).clientid,
//                                (activity as UpgradeActivity).fpid!!,
//                                PaymentDetails(
//                                        "INR",
//                                        0,
//                                        "RAZORPAY",
//                                        TaxDetails(
//                                                GSTINNumber,
//                                                0,
//                                                null,
//                                                18),
//                                        grandTotal),
//                                widgetsToBeBought,
//                                null
//                        )
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
            "NEW",
            purchaseOrders,
            (cartStateId ?: "")
        )
    )
  }

  fun loadData() {
//        viewModel.requestCustomerId(CustomerIDRequest((activity as UpgradeActivity).clientid, "ANDROID",
//        (activity as UpgradeActivity).email!!, (activity as UpgradeActivity).loginid!!, (activity as UpgradeActivity).fpName!!,
//        (activity as UpgradeActivity).mobileNo!!, "RAZORPAY", com.boost.upgrades.data.api_model.customerId.create.TaxDetails(null, 0, null, 0))

    viewModel.getCartItems()
    viewModel.getAllFeatures()
    viewModel.getAllBundles()
  }

  private fun checkRenewalItemDeepLinkClick() {
    val currentDate = getCurrentDate().parseDate(FORMAT_MM_DD_YYYY)
    val sevenDayDate = getAmountDate(7).parseDate(FORMAT_MM_DD_YYYY)
    viewModel.allPurchasedWidgets(RenewalPurchasedRequest(floatingPointId = (activity as UpgradeActivity).fpid, clientId = (activity as UpgradeActivity).clientid,
        widgetStatus = RenewalPurchasedRequest.WidgetStatus.ACTIVE.name, nextWidgetStatus = RenewalPurchasedRequest.NextWidgetStatus.RENEWAL.name,
        dateFilter = RenewalPurchasedRequest.DateFilter.EXPIRY_DATE.name, startDate = currentDate, endDate = sevenDayDate))
    viewModel.renewalResult().observeOnce(Observer {
      renewalList = it ?: ArrayList()
      if (renewalList.isNotEmpty()) {
        val idItems = arrayListOf<String>()
        renewalList.forEach { res -> res.widgetId?.let { id -> idItems.add(id) } }
        viewModel.getCartsByIds(idItems)
        viewModel.cartResultItems().observeOnce(Observer { cartList ->
          viewModel.loaderShow()
          renewalList.forEach { renewal ->
            if (isDeepLinking && cartList.isNullOrEmpty()) {
              saveRenewalData(false, renewal)
            } else if (cartList.isNotEmpty()) {
              val data = cartList?.firstOrNull { it1 -> it1.item_id == renewal.widgetId }
              if (data != null) saveRenewalData(true, renewal)
            }
          }
          Timer().schedule(2000) { loadData() }
        })
      } else loadData()
    })
  }

  private fun saveRenewalData(isUpdate: Boolean, renewal: RenewalResult) {
    var minMonth = 1
    if (renewal.expiry?.key == "DAYS") minMonth = renewal.expiry?.valueDays()?.div(30) ?: 1
    val cartModel = CartModel(renewal.widgetId ?: "", renewal.widgetKey ?: "",
        renewal.name ?: "", renewal.desc ?: "", renewal.images?.firstOrNull() ?: "",
        renewal.netPrice ?: 0.0, renewal.price ?: 0.0, (renewal.discount ?: 0.0).toInt(),
        1, minMonth, "renewals", null)
    if (isUpdate) viewModel.updateItemToCart(cartModel)
    else viewModel.addItemToCart(cartModel)
  }

  @SuppressLint("FragmentLiveDataObserve")
  fun initMvvM() {
    viewModel.cartResult().observe(this, Observer {
      if (it.isNullOrEmpty().not()) {
        cartList = it
        WebEngageController.trackEvent("ADDONS_MARKETPLACE Full_Cart Loaded", "Cart Size:" + it.size, "")
        empty_cart.visibility = View.GONE
        cart_main_layout.visibility = View.VISIBLE
        val features = arrayListOf<CartModel>()
        val bundles = arrayListOf<CartModel>()
        val renewals = arrayListOf<CartModel>()
        for (items in it) {
          when (items.item_type) {
            "features" -> features.add(items)
            "bundles" -> bundles.add(items)
            "renewals" -> renewals.add(items)
          }
        }
        if (features.size > 0) {
          updateAddons(features)
          addons_layout.visibility = View.VISIBLE
        } else {
          addons_layout.visibility = View.GONE
        }
        if (bundles.size > 0) {
          updatePackage(bundles)
          package_layout.visibility = View.VISIBLE
        } else {
          package_layout.visibility = View.GONE
        }
        if (renewals.size > 0) {
          updateRenewal(renewals)
          renewal_layout.visibility = View.VISIBLE
        } else {
          renewal_layout.visibility = View.GONE
        }
        totalCalculation()
      } else {
        WebEngageController.trackEvent("ADDONS_MARKETPLACE Empty_Cart Loaded", "ADDONS_MARKETPLACE Empty_Cart Loaded", "")
        empty_cart.visibility = View.VISIBLE
        cart_main_layout.visibility = View.GONE


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

        proceedToPayment(it)
      }
    })
    viewModel.getLoaderStatus().observe(this, Observer {
      if (it) {
        if ((::progressDialog.isInitialized && progressDialog.isShowing).not()) {
          val status = viewModel.getAPIRequestStatus()
          progressDialog.setMessage(status)
          progressDialog.setCancelable(false) // disable dismiss by tapping outside of the dialog
          progressDialog.show()
        }
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

    //getting all renewal Purchase
    viewModel.renewalResult().observe(this, Observer {
      renewalList = it ?: ArrayList()
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
        discount_coupon_title.text = validCouponCode?.coupon_key
        cart_apply_coupon.visibility = View.GONE
        totalCalculation()
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

  fun updateRenewal(renewal: List<CartModel>) {
    cartRenewalAdaptor.renewalNotify(renewal)
    cartRenewalAdaptor.notifyDataSetChanged()
  }


  private fun initializePackageRecycler() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    cart_package_recycler.apply {
      layoutManager = gridLayoutManager
      adapter = cartPackageAdaptor
    }
  }

  private fun initializeAddonsRecycler() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    cart_addons_recycler.apply {
      layoutManager = gridLayoutManager
      adapter = cartAddonsAdaptor
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


  fun totalCalculation() {
    if (::cartList.isInitialized) {
      total = 0.0
      var couponDisount = 0
      if (validCouponCode != null) {
        couponDisount = validCouponCode?.discount_percent ?: 0
        coupon_discount_title.text = "Coupon discount($couponDisount%)"
      }
      if (cartList.isNullOrEmpty().not()) {
        for (item in cartList) {
          total += item.price
        }
        cart_amount_value.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(total)
        couponDiscountAmount = total * couponDisount / 100
        coupon_discount_value.text = "-₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(couponDiscountAmount)
        total -= couponDiscountAmount
        val temp = (total * 18) / 100
        taxValue = (temp * 100).roundToInt() / 100.0
        grandTotal = (((total + taxValue) * 100).roundToInt() / 100.0)
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
      1 -> deleteCartAddonsItem(renewalResult.item_id)
    }
  }

  fun proceedToPayment(result: CreatePurchaseOrderResponse) {
    val paymentFragment = PaymentFragment.newInstance()
    val args = Bundle()
    args.putString("customerId", customerId)
    args.putDouble("amount", result.Result.TotalPrice)// pass in currency subunits. For example, paise. Amount: 1000 equals ₹10
    args.putString("order_id", result.Result.OrderId)
    args.putString("email", (activity as UpgradeActivity).email)
    args.putString("currency", "INR")
    args.putString("contact", (activity as UpgradeActivity).mobileNo)
    paymentFragment.arguments = args
    (activity as UpgradeActivity).addFragment(
        paymentFragment,
        Constants.PAYMENT_FRAGMENT
    )
  }

}
