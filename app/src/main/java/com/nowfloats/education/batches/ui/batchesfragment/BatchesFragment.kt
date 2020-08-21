package com.nowfloats.education.batches.ui.batchesfragment

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
import com.boost.upgrades.utils.Utils

import com.nowfloats.education.batches.BatchesActivity
import com.nowfloats.education.batches.adapter.BatchesAdapter
import com.nowfloats.education.batches.model.Data
import com.nowfloats.education.batches.ui.batchesdetails.BatchesDetailsFragment
import com.nowfloats.education.helper.BaseFragment
import com.nowfloats.education.helper.Constants.BATCHES_DETAILS_FRAGMENT
import com.nowfloats.education.helper.Constants.SUCCESS
import com.nowfloats.education.helper.ItemClickEventListener
import com.thinksity.R
import org.koin.android.ext.android.inject

class BatchesFragment : BaseFragment(), ItemClickEventListener {

    private val viewModel by inject<BatchesViewModel>()
    private val batchesAdapter: BatchesAdapter by lazy { BatchesAdapter(this) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.batches_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHeader(view)
        initLiveDataObservers()
        initBatchesRecyclerview(view)
    }

    private fun initBatchesRecyclerview(view: View) {
        val recyclerview = view.findViewById<RecyclerView>(R.id.batches_recycler)
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerview.apply {
            layoutManager = gridLayoutManager
            adapter = batchesAdapter
        }

        if (Utils.isConnectedToInternet(requireContext())) {
            showLoader("Loading batches")
            viewModel.getUpcomingBatches()
        } else {
            showToast("No Internet!")
        }
    }

    private fun initLiveDataObservers() {
        viewModel.apply {
            upcomingBatchResponse.observe(viewLifecycleOwner, Observer {
                if (!it.Data.isNullOrEmpty()) {
                    hideLoader()
                    setRecyclerviewAdapter(it.Data)
                }
            })

            errorResponse.observe(viewLifecycleOwner, Observer {
                hideLoader()
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            })

            deleteBatchResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (!it.isNullOrBlank()) {
                    if (it == SUCCESS) {
                        hideLoader()
                        Toast.makeText(requireContext(), "Batch deleted successfully", Toast.LENGTH_SHORT).show()
                        showLoader("Loading batches")
                        setDeleteBatchesLiveDataValue("")
                        getUpcomingBatches()
                    }
                }
            })
        }
    }

    private fun setRecyclerviewAdapter(batchesResponseData: List<Data>) {
        batchesAdapter.items = batchesResponseData
        batchesAdapter.notifyDataSetChanged()
    }

    fun setHeader(view: View) {
        val rightButton: LinearLayout = view.findViewById(R.id.right_icon_layout)
        val backButton: LinearLayout = view.findViewById(R.id.back_button)
        val rightIcon: ImageView = view.findViewById(R.id.right_icon)
        val title: TextView = view.findViewById(R.id.title)
        title.text = getString(R.string.upcoming_batches)
        rightIcon.setImageResource(R.drawable.ic_add_white)
        rightButton.setOnClickListener {
            (activity as BatchesActivity).addFragment(BatchesDetailsFragment.newInstance(), BATCHES_DETAILS_FRAGMENT)
        }
        backButton.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun itemMenuOptionStatus(pos: Int, status: Boolean) {
        updateItemMenuOptionStatus(pos, status)
    }

    override fun onEditClick(data: Any, position: Int) {
        batchesAdapter.menuOption(position, false)
        (activity as BatchesActivity).addFragment(BatchesDetailsFragment.newInstance(data as Data), BATCHES_DETAILS_FRAGMENT)
    }

    override fun onDeleteClick(data: Any, position: Int) {
        batchesAdapter.menuOption(position, false)
        showLoader("Deleting batch")
        viewModel.deleteUpcomingBatch(data as Data)
    }

    private fun updateItemMenuOptionStatus(position: Int, status: Boolean) {
        batchesAdapter.menuOption(position, status)
        batchesAdapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(): BatchesFragment = BatchesFragment()
    }
}