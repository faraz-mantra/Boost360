package com.nowfloats.education.toppers.ui.topperhome

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
import com.nowfloats.education.helper.BaseFragment
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
import org.koin.android.ext.android.inject

class ToppersFragment : BaseFragment(), ItemClickEventListener {

    private val viewModel by inject<ToppersViewModel>()
    private val toppersAdapter: TopperAdapter by lazy { TopperAdapter(this) }
    private lateinit var toppersActivity: ToppersActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.toppers_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toppersActivity = activity as ToppersActivity
        setHeader(view)
        initLiveDataObservers()
        initBatchesRecyclerview(view)
    }

    private fun initBatchesRecyclerview(view: View) {
        val recyclerview = view.findViewById<RecyclerView>(R.id.topper_recycler)
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerview.apply {
            layoutManager = gridLayoutManager
            adapter = toppersAdapter
        }

        if (Utils.isNetworkConnected(requireContext())) {
            showLoader("Loading Our Toppers")
            viewModel.getOurToppers()
        } else {
            showToast("No Internet!")
        }
    }

    private fun initLiveDataObservers() {
        viewModel.apply {
            ourTopperResponse.observe(viewLifecycleOwner, Observer {
                if (!it.Data.isNullOrEmpty()) {
                    hideLoader()
                    setRecyclerviewAdapter(it.Data)
                }
            })

            errorMessage.observe(viewLifecycleOwner, Observer {
                hideLoader()
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            })

            deleteTopperResponse.observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrBlank()) {
                    if (it == SUCCESS) {
                        hideLoader()
                        Toast.makeText(requireContext(), "Topper deleted successfully", Toast.LENGTH_SHORT).show()
                        showLoader("Loading Toppers")
                        setDeleteTopperLiveDataValue("")
                        viewModel.getOurToppers()
                    }
                }
            })
        }
    }

    private fun setRecyclerviewAdapter(topperResponseData: List<Data>) {
        toppersAdapter.items = topperResponseData
        toppersAdapter.notifyDataSetChanged()
    }

    fun setHeader(view: View) {
        val rightButton: LinearLayout = view.findViewById(R.id.right_icon_layout)
        val backButton: LinearLayout = view.findViewById(R.id.back_button)
        val rightIcon: ImageView = view.findViewById(R.id.right_icon)
        val title: TextView = view.findViewById(R.id.title)
        title.text = getString(R.string.our_toppers)
        rightIcon.setImageResource(R.drawable.ic_add_white)
        rightButton.setOnClickListener {
            (activity as ToppersActivity).addFragment(TopperDetailsFragment.newInstance(), TOPPERS_DETAILS_FRAGMENT)
        }
        backButton.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun itemMenuOptionStatus(pos: Int, status: Boolean) {
        updateItemMenuOptionStatus(pos, status)
    }

    override fun onEditClick(data: Any, position: Int) {
        toppersAdapter.menuOption(position, false)
        (activity as ToppersActivity).addFragment(TopperDetailsFragment.newInstance(data as Data, true), TOPPERS_FRAGMENT)
    }

    override fun onDeleteClick(data: Any, position: Int) {
        toppersAdapter.menuOption(position, false)
        showLoader("Deleting Topper")
        viewModel.deleteOurTopper(data as Data)
    }

    private fun updateItemMenuOptionStatus(position: Int, status: Boolean) {
        toppersAdapter.menuOption(position, status)
        toppersAdapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(): ToppersFragment = ToppersFragment()
    }
}