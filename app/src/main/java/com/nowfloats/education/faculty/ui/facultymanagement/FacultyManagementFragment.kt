package com.nowfloats.education.faculty.ui.facultymanagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.framework.views.fabButton.FloatingActionButton
import com.nowfloats.education.faculty.FacultyActivity
import com.nowfloats.education.faculty.adapter.FacultyManagementAdapter
import com.nowfloats.education.faculty.model.Data
import com.nowfloats.education.faculty.ui.facultydetails.FacultyDetailsFragment
import com.nowfloats.education.helper.BaseFragment
import com.nowfloats.education.helper.Constants.FACULTY_DETAILS_FRAGMENT
import com.nowfloats.education.helper.Constants.SUCCESS
import com.nowfloats.education.helper.ItemClickEventListener
import com.nowfloats.util.Utils
import com.thinksity.R
import org.koin.android.ext.android.inject

class FacultyManagementFragment : BaseFragment(), ItemClickEventListener {

  private val viewModel by inject<FacultyManagementViewModel>()
  private val facultyManagementAdapter: FacultyManagementAdapter by lazy {
    FacultyManagementAdapter(
      this
    )
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.faculty_management_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setHeader(view)
    initLiveDataObservers()
    initBatchesRecyclerview(view)
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
          hideLoader()
          setRecyclerviewAdapter(it.Data)
        }
      })

      errorMessage.observe(viewLifecycleOwner, Observer {
        hideLoader()
        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
      })

      deleteFacultyResponse.observe(viewLifecycleOwner, Observer {
        if (!it.isNullOrBlank()) {
          if (it == SUCCESS) {
            hideLoader()
            Toast.makeText(
              requireContext(),
              getString(R.string.faculty_deleted_successfully),
              Toast.LENGTH_SHORT
            ).show()
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
}