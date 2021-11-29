package com.nowfloats.education.faculty.ui.facultymanagement

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.UpgradeActivity
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.views.fabButton.FloatingActionButton
import com.framework.views.zero.old.AppFragmentZeroCase
import com.framework.views.zero.old.AppOnZeroCaseClicked
import com.framework.views.zero.old.AppRequestZeroCaseBuilder
import com.framework.views.zero.old.AppZeroCases
import com.nowfloats.Login.UserSessionManager
import com.nowfloats.education.faculty.FacultyActivity
import com.nowfloats.education.faculty.adapter.FacultyManagementAdapter
import com.nowfloats.education.faculty.model.Data
import com.nowfloats.education.faculty.ui.facultydetails.FacultyDetailsFragment
import com.nowfloats.education.helper.BaseFragment
import com.nowfloats.education.helper.Constants
import com.nowfloats.education.helper.Constants.FACULTY_DETAILS_FRAGMENT
import com.nowfloats.education.helper.Constants.SUCCESS
import com.nowfloats.education.helper.ItemClickEventListener
import com.nowfloats.util.Utils
import com.thinksity.R
import com.thinksity.Specific
import com.thinksity.databinding.FacultyManagementFragmentBinding
import org.koin.android.ext.android.inject
import java.util.ArrayList

class FacultyManagementFragment : BaseFragment(), ItemClickEventListener, AppOnZeroCaseClicked {

  private val viewModel by inject<FacultyManagementViewModel>()
  private val facultyManagementAdapter: FacultyManagementAdapter by lazy {
    FacultyManagementAdapter(
      this
    )
  }
  private lateinit var fragmentZeroCase: AppFragmentZeroCase
  private lateinit var binding: FacultyManagementFragmentBinding
  private lateinit var userSessionManager: UserSessionManager
  private val TAG = "FacultyManagementFragme"
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.faculty_management_fragment, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Log.i(TAG, "onViewCreated: ")
    userSessionManager = UserSessionManager(requireActivity(), requireActivity())
    this.fragmentZeroCase = AppRequestZeroCaseBuilder(
      AppZeroCases.FACULTY_MANAGEMENT, this, requireActivity(), isPremium()
    ).getRequest().build()
    requireActivity().supportFragmentManager.beginTransaction().replace(
      binding.childContainer.id, fragmentZeroCase
    ).commit()

    setHeader(view)
    initLiveDataObservers()
    if (isPremium()) {
      nonEmptyView()
      initBatchesRecyclerview(view)
    } else {
      emptyView()
    }
  }


  private fun isPremium(): Boolean {
    return userSessionManager.storeWidgets?.contains(Constants.FACULTY_MANAGEMENT_FEATURE) == true
  }

  private fun nonEmptyView() {
    setHasOptionsMenu(true)
    binding.mainlayout.visible()
    binding.childContainer.gone()
  }

  private fun emptyView() {
    setHasOptionsMenu(false)
    binding.mainlayout.gone()
    binding.childContainer.visible()

  }

  private fun initBatchesRecyclerview(view: View) {
    val recyclerview = view.findViewById<RecyclerView>(R.id.faculty_recycler)
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    recyclerview.apply {
      layoutManager = gridLayoutManager
      adapter = facultyManagementAdapter
    }

    if (Utils.isNetworkConnected(requireContext())) {
      showLoader("Loading Faculty Management")
      viewModel.getOurFaculty()
    } else {
      showToast("No Internet!")
    }
  }

  private fun initLiveDataObservers() {
    viewModel.apply {
      ourFacultyResponse.observe(viewLifecycleOwner, Observer {
        if (!it.Data.isNullOrEmpty()) {
          nonEmptyView()
          Log.i(TAG, "data not empty")
          setRecyclerviewAdapter(it.Data)
        } else {
          Log.i(TAG, "data empty")
          emptyView()
        }
        hideLoader()
      })

      errorMessage.observe(viewLifecycleOwner, Observer {
        hideLoader()
        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
      })

      deleteFacultyResponse.observe(viewLifecycleOwner, Observer {
        if (!it.isNullOrBlank()) {
          hideLoader()
          if (it == SUCCESS) {
            Toast.makeText(requireContext(), getString(R.string.faculty_deleted_successfully), Toast.LENGTH_SHORT).show()
            showLoader("Loading Faculty")
            setDeleteFacultyLiveDataValue("")
            viewModel.getOurFaculty()
          }
        }
      })
    }
  }

  private fun setRecyclerviewAdapter(batchesResponseData: List<Data>) {
    facultyManagementAdapter.items = batchesResponseData
    facultyManagementAdapter.notifyDataSetChanged()
  }

  private fun setHeader(view: View) {
    val backButton: LinearLayout = view.findViewById(R.id.back_button)
    (view.findViewById(R.id.right_icon) as? ImageView)?.visibility = View.INVISIBLE
    val btnAdd: FloatingActionButton = view.findViewById(R.id.btn_add)
    val title: TextView = view.findViewById(R.id.title)
    title.text = getString(R.string.faculty_management)
    btnAdd.setOnClickListener {
      (activity as FacultyActivity).addFragment(
        FacultyDetailsFragment.newInstance(),
        FACULTY_DETAILS_FRAGMENT
      )
    }
    backButton.setOnClickListener { requireActivity().onBackPressed() }
  }

  override fun itemMenuOptionStatus(pos: Int, status: Boolean) {
    updateItemMenuOptionStatus(pos, status)
  }

  override fun onEditClick(data: Any, position: Int) {
    facultyManagementAdapter.menuOption(position, false)
    (activity as FacultyActivity).addFragment(
      FacultyDetailsFragment.newInstance(data as Data),
      FACULTY_DETAILS_FRAGMENT
    )
  }

  override fun onDeleteClick(data: Any, position: Int) {
    facultyManagementAdapter.menuOption(position, false)
    showLoader(getString(R.string.deleting_faculty))
    viewModel.deleteOurFaculty(data as Data)
  }

  private fun updateItemMenuOptionStatus(position: Int, status: Boolean) {
    facultyManagementAdapter.menuOption(position, status)
    facultyManagementAdapter.notifyDataSetChanged()
  }

  companion object {
    fun newInstance(): FacultyManagementFragment = FacultyManagementFragment()
  }

  override fun primaryButtonClicked() {
    if (isPremium()) {
      (activity as FacultyActivity).addFragment(
        FacultyDetailsFragment.newInstance(),
        FACULTY_DETAILS_FRAGMENT
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

  private fun initiateBuyFromMarketplace() {
    userSessionManager.let {
      val progressDialog = ProgressDialog(requireActivity())
      val status = "Loading. Please wait..."
      progressDialog.setMessage(status)
      progressDialog.setCancelable(false)
      progressDialog.show()
      val intent = Intent(requireActivity(), UpgradeActivity::class.java)
      intent.putExtra("expCode", it.fP_AppExperienceCode)
      intent.putExtra("fpName", it.fpName)
      intent.putExtra("fpid", it.fpid)
      intent.putExtra("loginid", it.userProfileId)
      intent.putStringArrayListExtra("userPurchsedWidgets", ArrayList(userSessionManager.getStoreWidgets()))
      intent.putExtra("fpTag", it.fpTag)
      if (it.userProfileEmail != null) {
        intent.putExtra("email", it.userProfileEmail)
      } else {
        intent.putExtra("email", Specific.CONTACT_EMAIL_ID)
      }
      if (it.userPrimaryMobile != null) {
        intent.putExtra("mobileNo", it.userPrimaryMobile)
      } else {
        intent.putExtra("mobileNo", Specific.CONTACT_PHONE_ID)
      }
      intent.putExtra("profileUrl", it.fpLogo)
      intent.putExtra("buyItemKey", Constants.FACULTY_MANAGEMENT_FEATURE)
      startActivity(intent)
      Handler().postDelayed({
        progressDialog.dismiss()
        requireActivity().finish()
      }, 1000)
    }
  }

}