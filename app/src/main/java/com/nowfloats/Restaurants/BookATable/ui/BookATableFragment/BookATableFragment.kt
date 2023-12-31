package com.nowfloats.Restaurants.BookATable.ui.BookATableFragment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.marketplace.ui.home.MarketPlaceActivity
import com.boost.payment.base_class.BaseFragment
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.views.zero.old.AppFragmentZeroCase
import com.framework.views.zero.old.AppOnZeroCaseClicked
import com.framework.views.zero.old.AppRequestZeroCaseBuilder
import com.framework.views.zero.old.AppZeroCases
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nowfloats.Login.UserSessionManager
import com.nowfloats.Restaurants.API.RestaurantsAPIInterfaces
import com.nowfloats.Restaurants.API.model.DeleteBookTable.DeleteBookTableData
import com.nowfloats.Restaurants.API.model.GetBookTable.Data
import com.nowfloats.Restaurants.API.model.GetBookTable.GetBookTableData
import com.nowfloats.Restaurants.BookATable.BookATableActivity
import com.nowfloats.Restaurants.BookATable.adapter.BookTableAdapter
import com.nowfloats.Restaurants.BookATable.ui.BookATableDetailsFragement.BookATableDetailsFragment
import com.nowfloats.Restaurants.Interfaces.BookTableFragmentListener
import com.nowfloats.util.Methods
import com.thinksity.R
import com.thinksity.databinding.BookATableFragmentBinding
import kotlinx.android.synthetic.main.book_a_table_fragment.*
import kotlinx.android.synthetic.main.new_custome_app_bar.*
import org.json.JSONObject
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.android.AndroidLog
import retrofit.client.Response
import retrofit.converter.GsonConverter


class BookATableFragment : BaseFragment(), BookTableFragmentListener, AppOnZeroCaseClicked {

  lateinit var adapter: BookTableAdapter
  var session: UserSessionManager? = null
  var dataList: List<Data>? = null
  lateinit var zerothCaseFragmentZeroCase: AppFragmentZeroCase
  lateinit var binding: BookATableFragmentBinding

  companion object {
    fun newInstance() = BookATableFragment()
    fun newInstance(bundle: Bundle? = null): BookATableFragment {
      val fragment = BookATableFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    adapter = BookTableAdapter(ArrayList(), this)
    binding = DataBindingUtil.inflate(inflater, R.layout.book_a_table_fragment, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val isAdd = arguments?.getBoolean("is_add") ?: false
    book_table_recycler.adapter = adapter
    session = UserSessionManager(requireContext(), requireActivity())
    zerothCaseFragmentZeroCase = AppRequestZeroCaseBuilder(AppZeroCases.TABLE_BOOKING, this, requireActivity(), isPremium()).getRequest().build()
    requireActivity().supportFragmentManager.beginTransaction().replace(binding.childContainer.id, zerothCaseFragmentZeroCase).commit()

//
//    buy_item.setOnClickListener {
//      initiateBuyFromMarketplace()
//    }

    //setheader
    setHeader()
    initializeRecycler()

    if (isPremium()) {
      book_table_recycler.visibility = View.VISIBLE
      loadData()
    } else {
      book_table_recycler.visibility = View.GONE
      emptyView()
    }
    if (isAdd) btn_add?.performClick()
  }

  private fun isPremium(): Boolean {
    return session?.storeWidgets?.contains("BOOKTABLE") == true
  }

  private fun nonEmptyView() {
    setHasOptionsMenu(true)
    binding.mainlayout?.visible()
    binding.childContainer?.gone()
  }

  private fun emptyView() {
    setHasOptionsMenu(false)
    binding.mainlayout?.gone()
    binding.childContainer?.visible()

  }

  private fun initializeRecycler() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    book_table_recycler.apply {
      layoutManager = gridLayoutManager
    }
  }

  fun setHeader() {
    title.text = "Book a Table"
    back_button.setOnClickListener {
      (activity as? BookATableActivity)?.onBackPressed()
    }
    if (isPremium()) {
      btn_add.visibility = View.VISIBLE
      nonEmptyView()
      btn_add.setOnClickListener {
        val bookATableDetailsFragment = BookATableDetailsFragment.newInstance()
        val arg = Bundle()
        arg.putString("ScreenState", "new")
        bookATableDetailsFragment.arguments = arg
        (activity as BookATableActivity).addFragment(
          bookATableDetailsFragment,
          "BOOK_A_TABLE_DETAILS_FRAGMENT"
        )
      }
    } else {
      btn_add.visibility = View.GONE
      emptyView()
    }

  }


  override fun onBackPressed() {
    super.onBackPressed()
    loadData()
  }

  private fun loadData() {
    try {
      val query = JSONObject()
      query.put("WebsiteId", session!!.getFpTag())
      val APICalls = RestAdapter.Builder()
        .setEndpoint("https://webaction.api.boostkit.dev")
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setLog(AndroidLog("ggg"))
        .build()
        .create(RestaurantsAPIInterfaces::class.java)

      APICalls.getBookTable(query, 0, 1000, object : Callback<GetBookTableData?> {
        override fun success(getBookTableData: GetBookTableData?, response: Response) {
          if (getBookTableData == null || response.status != 200) {
            Toast.makeText(
              requireContext(),
              getString(R.string.something_went_wrong),
              Toast.LENGTH_SHORT
            ).show()
            return
          }
          dataList = getBookTableData.Data
          if (dataList?.isEmpty() == true) {
            emptyView()
          } else {
            updateRecyclerView()
            nonEmptyView()
          }
        }

        override fun failure(error: RetrofitError) {
          Methods.showSnackBarNegative(requireActivity(), getString(R.string.something_went_wrong))
        }
      })
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun updateRecyclerView() {
    adapter.updateList(dataList!!)
    adapter.notifyDataSetChanged()
  }

  override fun itemMenuOptionStatus(pos: Int, status: Boolean) {
    adapter.menuOption(pos, status)
    adapter.notifyDataSetChanged()
  }

  override fun editOptionClicked(data: Data) {
    adapter.menuOption(-1, false)
    adapter.notifyDataSetChanged()

    val bookATableDetailsFragment = BookATableDetailsFragment.newInstance()
    val arg = Bundle()
    arg.putString("ScreenState", "edit")
    arg.putString("data", Gson().toJson(data));
    bookATableDetailsFragment.arguments = arg
    (activity as BookATableActivity).addFragment(
      bookATableDetailsFragment,
      "BOOK_A_TABLE_DETAILS_FRAGMENT"
    )
  }

  override fun deleteOptionClicked(data: Data) {
    adapter.menuOption(-1, false)
    adapter.notifyDataSetChanged()
    try {
      val requestBody = DeleteBookTableData(
        "{_id:'" + data._id + "'}",
        "{\$set : {IsArchived: true }}"
      )

      val APICalls = RestAdapter.Builder()
        .setEndpoint("https://webaction.api.boostkit.dev")
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setLog(AndroidLog("ggg"))
        .setConverter(GsonConverter(GsonBuilder().setLenient().create()))
        .build()
        .create(RestaurantsAPIInterfaces::class.java)

      APICalls.deleteBookTable(requestBody, object : Callback<String?> {
        override fun success(data: String?, response: Response) {
          if (response != null && response.status == 200) {
            Log.d("deletePlacesAround ->", response.body.toString())
            Methods.showSnackBarPositive(
              requireActivity(),
              getString(R.string.successfully_deleted_)
            )
            loadData()
          } else {
            Methods.showSnackBarNegative(
              requireActivity(),
              getString(R.string.something_went_wrong)
            )
          }
        }

        override fun failure(error: RetrofitError) {
          if (error.response.status == 200) {
            Methods.showSnackBarPositive(
              requireActivity(),
              getString(R.string.successfully_deleted_)
            )
            loadData()
          } else {
            Methods.showSnackBarNegative(
              requireActivity(),
              getString(R.string.something_went_wrong)
            )
          }
        }
      })
    } catch (e: java.lang.Exception) {
      e.printStackTrace()
    }
  }

  private fun initiateBuyFromMarketplace() {
    val progressDialog = ProgressDialog(requireContext())
    val status = "Loading. Please wait..."
    progressDialog.setMessage(status)
    progressDialog.setCancelable(false)
    progressDialog.show()
    val intent = Intent(requireActivity(), MarketPlaceActivity::class.java)
    intent.putExtra("expCode", session!!.fP_AppExperienceCode)
    intent.putExtra("fpName", session!!.fpName)
    intent.putExtra("fpid", session!!.fpid)
    intent.putExtra("loginid", session!!.userProfileId)
    intent.putStringArrayListExtra("userPurchsedWidgets", ArrayList(session?.storeWidgets ?: arrayListOf()))
    intent.putExtra("fpTag", session!!.fpTag)
    if (session!!.userProfileEmail != null) {
      intent.putExtra("email", session!!.userProfileEmail)
    } else {
      intent.putExtra("email", getString(R.string.ria_customer_mail))
    }
    if (session!!.userPrimaryMobile != null) {
      intent.putExtra("mobileNo", session!!.userPrimaryMobile)
    } else {
      intent.putExtra("mobileNo", getString(R.string.ria_customer_number))
    }
    intent.putExtra("profileUrl", session!!.fpLogo)
    intent.putExtra("buyItemKey", "BOOKTABLE")
    startActivity(intent)
    Handler().postDelayed({
      progressDialog.dismiss()
      requireActivity().onBackPressed()
    }, 1000)
  }

  override fun primaryButtonClicked() {
    if (isPremium()) {
      val bookATableDetailsFragment = BookATableDetailsFragment.newInstance()
      val arg = Bundle()
      arg.putString("ScreenState", "new")
      bookATableDetailsFragment.arguments = arg
      (activity as BookATableActivity).addFragment(
        bookATableDetailsFragment,
        "BOOK_A_TABLE_DETAILS_FRAGMENT"
      )
    } else {
      initiateBuyFromMarketplace()
    }
  }

  override fun secondaryButtonClicked() {
    Toast.makeText(activity, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
  }

  override fun ternaryButtonClicked() {
  }

  override fun appOnBackPressed() {

  }
}