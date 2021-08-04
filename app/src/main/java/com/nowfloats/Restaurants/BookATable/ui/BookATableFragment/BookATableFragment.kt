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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.boost.upgrades.UpgradeActivity
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
import com.nowfloats.util.Constants
import com.nowfloats.util.Methods
import com.thinksity.R
import kotlinx.android.synthetic.main.book_a_table_fragment.*
import kotlinx.android.synthetic.main.new_custome_app_bar.*
import org.json.JSONObject
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.android.AndroidLog
import retrofit.client.Response
import retrofit.converter.GsonConverter


class BookATableFragment : BaseFragment(), BookTableFragmentListener {

  lateinit var adapter: BookTableAdapter
  var session: UserSessionManager? = null
  var dataList: List<Data>? = null

  companion object {
    fun newInstance() = BookATableFragment()
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    adapter = BookTableAdapter(ArrayList(), this)

    return inflater.inflate(R.layout.book_a_table_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    book_table_recycler.adapter = adapter
    session = UserSessionManager(requireContext(), requireActivity())

    buy_item.setOnClickListener {
      initiateBuyFromMarketplace()
    }

    //setheader
    setHeader()
    initializeRecycler()

    if (session?.storeWidgets?.contains("BOOKTABLE") == true) {
      book_table_recycler.visibility = View.VISIBLE
      empty_layout.setVisibility(View.GONE)
      loadData()
    } else {
      book_table_recycler.visibility = View.GONE
      empty_layout.setVisibility(View.VISIBLE)
    }
  }

  private fun initializeRecycler() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    book_table_recycler.apply {
      layoutManager = gridLayoutManager
    }
  }

  fun setHeader() {
    title.text = "Book A Table"
    back_button.setOnClickListener {
      (activity as? BookATableActivity)?.onBackPressed()
    }
    if (session?.storeWidgets?.contains("BOOKTABLE") == true) {
      btn_add.visibility = View.VISIBLE
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
    } else btn_add.visibility = View.GONE

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
          updateRecyclerView()
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
    val intent = Intent(requireActivity(), UpgradeActivity::class.java)
    intent.putExtra("expCode", session!!.fP_AppExperienceCode)
    intent.putExtra("fpName", session!!.fpName)
    intent.putExtra("fpid", session!!.fpid)
    intent.putExtra("loginid", session!!.userProfileId)
    intent.putStringArrayListExtra("userPurchsedWidgets", Constants.StoreWidgets)
    intent.putExtra("fpTag", session!!.fpTag)
    if (session!!.fpEmail != null) {
      intent.putExtra("email", session!!.userProfileEmail)
    } else {
      intent.putExtra("email", "ria@nowfloats.com")
    }
    if (session!!.fpPrimaryContactNumber != null) {
      intent.putExtra("mobileNo", session!!.userPrimaryMobile)
    } else {
      intent.putExtra("mobileNo", "9160004303")
    }
    intent.putExtra("profileUrl", session!!.fpLogo)
    intent.putExtra("buyItemKey", "BOOKTABLE")
    startActivity(intent)
    Handler().postDelayed({
      progressDialog.dismiss()
      requireActivity().onBackPressed()
    }, 1000)
  }
}