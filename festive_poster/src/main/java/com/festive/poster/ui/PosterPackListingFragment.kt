package com.festive.poster.ui

import android.util.Log
import android.view.Menu
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.FragmentPosterPackListingBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import java.io.File

class PosterPackListingFragment:
    AppBaseFragment<FragmentPosterPackListingBinding, BaseViewModel>(),RecyclerItemClickListener {

    private  val TAG = "PosterPackListingFragme"
    private var adapter: AppBaseRecyclerViewAdapter<PosterPackModel>?=null
    private var sharedViewModel:FestivePosterSharedViewModel?=null
    private var dataList:ArrayList<PosterPackModel>?=null
    companion object {
        @JvmStatic
        fun newInstance(): PosterPackListingFragment {
            return PosterPackListingFragment()
        }
    }
    override fun getLayout(): Int {
        return R.layout.fragment_poster_pack_listing
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        sharedViewModel = ViewModelProvider(requireActivity()).get(FestivePosterSharedViewModel::class.java)
        setObserver()
        setupList()
    }

    private fun setObserver() {
        sharedViewModel?.customizationDetails?.observe(viewLifecycleOwner,{

            Log.i(TAG, "customizationDetails Observer: ")
            dataList?.get(0)?.posterList?.get(0)?.map = mapOf("IMAGE_PATH" to File(it.imgPath).name,
                "Beautiful Smiles" to it.name)

            adapter?.notifyDataSetChanged()

        })
    }


    private fun setupList() {


        dataList = arrayListOf(
            PosterPackModel("Navratri",10, arrayListOf(PosterModel(null),PosterModel(null))),
            PosterPackModel("Navratri",10, arrayListOf(PosterModel(null))),
            PosterPackModel("Navratri",10, arrayListOf(PosterModel(null))),
            )

        adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,
            dataList!!,this)
        binding?.rvPosters?.adapter = adapter
        binding?.rvPosters?.layoutManager = LinearLayoutManager(requireActivity())
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

    }

    override fun onChildClick(
        childPosition: Int,
        parentPosition: Int,
        childItem: BaseRecyclerViewItem?,
        parentItem: BaseRecyclerViewItem?,
        actionType: Int
    ) {
        when(actionType){
            RecyclerViewActionType.POSTER_TAP_TO_EDIT_CLICK.ordinal->{
                CustomizePosterSheet().show(requireActivity().supportFragmentManager,CustomizePosterSheet::class.java.name)
            }
        }
    }


}
