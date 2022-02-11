package com.nowfloats.education.toppers.ui.topperhome

import android.app.ProgressDialog
import android.content.Intent
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.marketplace.ui.home.MarketPlaceActivity
import com.framework.base.BaseFragment
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.views.fabButton.FloatingActionButton
import com.framework.views.zero.old.AppFragmentZeroCase
import com.framework.views.zero.old.AppOnZeroCaseClicked
import com.framework.views.zero.old.AppRequestZeroCaseBuilder
import com.framework.views.zero.old.AppZeroCases
import com.nowfloats.Login.UserSessionManager
import com.nowfloats.education.helper.Constants
import com.nowfloats.education.helper.Constants.SUCCESS
import com.nowfloats.education.helper.Constants.TOPPERS_DETAILS_FRAGMENT
import com.nowfloats.education.helper.Constants.TOPPERS_FRAGMENT
import com.nowfloats.education.helper.ItemClickEventListener
import com.nowfloats.education.toppers.ToppersActivity
import com.nowfloats.education.toppers.adapter.TopperAdapter
import com.nowfloats.education.toppers.model.Data
import com.nowfloats.education.toppers.ui.topperdetails.TopperDetailsFragment
import com.nowfloats.util.Utils
import com.thinksity.R
import com.thinksity.databinding.ToppersFragmentBinding
import org.koin.android.ext.android.inject
import java.util.ArrayList

class ToppersFragment : BaseFragment<ToppersFragmentBinding, BaseViewModel>(), ItemClickEventListener,
  AppOnZeroCaseClicked {

  private val myViewModel by inject<ToppersViewModel>()
  private val toppersAdapter: TopperAdapter by lazy { TopperAdapter(this) }
  private lateinit var toppersActivity: ToppersActivity
  private lateinit var zeroCaseFragment: AppFragmentZeroCase
  private var session:UserSessionManager?=null



  private fun initBatchesRecyclerview(view: View) {
    val recyclerview = view.findViewById<RecyclerView>(R.id.topper_recycler)
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    recyclerview.apply {
      layoutManager = gridLayoutManager
      adapter = toppersAdapter
    }

    if (Utils.isNetworkConnected(requireContext())) {
      showLoader(getString(R.string.loading_our_topper))
      myViewModel.getOurToppers()
    } else {
      showLongToast(resources.getString(R.string.noInternet))
    }
  }

  private fun initLiveDataObservers() {
    myViewModel.apply {
      ourTopperResponse.observe(viewLifecycleOwner, Observer {
        if (!it.Data.isNullOrEmpty()) {
          nonEmptyView()
          setRecyclerviewAdapter(it.Data)
        } else{
          emptyView()
          setRecyclerviewAdapter(arrayListOf())
        }
        hideLoader()
      })

      errorMessage.observe(viewLifecycleOwner, {
        hideLoader()
        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
      })

      deleteTopperResponse.observe(viewLifecycleOwner, Observer {
        if (!it.isNullOrBlank()) {
          if (it == SUCCESS) {
            Toast.makeText(requireContext(), getString(R.string.topper_deleted_successfully), Toast.LENGTH_SHORT).show()
            showLoader(getString(R.string.loading_topper))
            setDeleteTopperLiveDataValue("")
            myViewModel.getOurToppers()
          }
        }
        hideLoader()
      })
    }
  }

  private fun setRecyclerviewAdapter(topperResponseData: List<Data>) {
    toppersAdapter.items = topperResponseData
    toppersAdapter.notifyDataSetChanged()
  }

  fun setHeader(view: View) {
    val backButton: LinearLayout = view.findViewById(R.id.back_button)
    (view.findViewById(R.id.right_icon) as? ImageView)?.visibility = View.INVISIBLE
    val btnAdd: FloatingActionButton = view.findViewById(R.id.btn_add)
    val title: TextView = view.findViewById(R.id.title)
    title.text = getString(R.string.our_toppers)
    btnAdd.setOnClickListener {
      (activity as ToppersActivity).addFragment(
        TopperDetailsFragment.newInstance(),
        TOPPERS_DETAILS_FRAGMENT
      )
    }
    backButton.setOnClickListener { requireActivity().onBackPressed() }
  }

  override fun itemMenuOptionStatus(pos: Int, status: Boolean) {
    updateItemMenuOptionStatus(pos, status)
  }

  override fun onEditClick(data: Any, position: Int) {
    toppersAdapter.menuOption(position, false)
    (activity as ToppersActivity).addFragment(
      TopperDetailsFragment.newInstance(data as Data, true),
      TOPPERS_FRAGMENT
    )
  }

  override fun onDeleteClick(data: Any, position: Int) {
    toppersAdapter.menuOption(position, false)
    showLoader(getString(R.string.deleting_topper))
    myViewModel.deleteOurTopper(data as Data)
  }

  private fun updateItemMenuOptionStatus(position: Int, status: Boolean) {
    toppersAdapter.menuOption(position, status)
    toppersAdapter.notifyDataSetChanged()
  }

  companion object {
    fun newInstance(): ToppersFragment = ToppersFragment()
  }

  override fun getLayout(): Int {
    return R.layout.toppers_fragment
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    session = UserSessionManager(requireActivity(),requireActivity())
    zeroCaseFragment = AppRequestZeroCaseBuilder(AppZeroCases.TOPPERS, this, baseActivity,isPremium()).getRequest().build()

    addFragment(containerID = binding?.childContainer?.id, zeroCaseFragment,false)

    toppersActivity = activity as ToppersActivity
    binding?.root?.let {
      setHeader(it)
      if (isPremium()){
        initLiveDataObservers()
        initBatchesRecyclerview(it)
        nonEmptyView()
      }else{
        emptyView()
      }
    }

  }

  fun isPremium(): Boolean {
    return session?.storeWidgets?.contains(Constants.TOPPER_FEATURE)==true
  }

  private fun nonEmptyView() {
    setHasOptionsMenu(true)
    binding?.mainlayout?.visible()
    binding?.childContainer?.gone()
  }


  private fun emptyView() {
    setHasOptionsMenu(false)
    binding?.mainlayout?.gone()
    binding?.childContainer?.visible()

//    binding?.bookingRecycler?.gone()
//    binding?.errorView?.visible()
//    binding?.btnAdd?.gone()
  }

  override fun primaryButtonClicked() {
    if (isPremium()){
      (activity as ToppersActivity).addFragment(
        TopperDetailsFragment.newInstance(),
        TOPPERS_DETAILS_FRAGMENT
      )
    }else{
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

  private fun initiateBuyFromMarketplace() {
    session?.let {
      val progressDialog = ProgressDialog(requireActivity())
      val status = "Loading. Please wait..."
      progressDialog.setMessage(status)
      progressDialog.setCancelable(false)
      progressDialog.show()
      val intent = Intent(requireActivity(), MarketPlaceActivity::class.java)
      intent.putExtra("expCode", it.fP_AppExperienceCode)
      intent.putExtra("fpName", it.fpName)
      intent.putExtra("fpid", it.fpid)
      intent.putExtra("loginid", it.userProfileId)
      intent.putStringArrayListExtra("userPurchsedWidgets", ArrayList(session!!.storeWidgets))
      intent.putExtra("fpTag", it.fpTag)
      if (it.userProfileEmail != null) {
        intent.putExtra("email", it.userProfileEmail)
      } else {
        intent.putExtra("email", getString(R.string.ria_customer_mail))
      }
      if (it.userPrimaryMobile != null) {
        intent.putExtra("mobileNo", it.userPrimaryMobile)
      } else {
        intent.putExtra("mobileNo", getString(R.string.ria_customer_number))
      }
      intent.putExtra("profileUrl", it.fpLogo)
      intent.putExtra("buyItemKey", Constants.TOPPER_FEATURE)
      startActivity(intent)
      Handler().postDelayed({
        progressDialog.dismiss()
        requireActivity().finish()
      }, 1000)
    }
  }


}