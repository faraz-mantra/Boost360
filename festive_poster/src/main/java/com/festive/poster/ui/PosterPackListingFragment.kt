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
import com.festive.poster.models.KeyModel
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.festive.poster.viewmodels.FestivePosterViewModel
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.google.gson.Gson
import java.io.File

class PosterPackListingFragment:
    AppBaseFragment<FragmentPosterPackListingBinding, FestivePosterViewModel>(),RecyclerItemClickListener {

    private  val TAG = "PosterPackListingFragme"
    private var adapter: AppBaseRecyclerViewAdapter<PosterPackModel>?=null
    private var sharedViewModel:FestivePosterSharedViewModel?=null
    private var dataList:ArrayList<PosterPackModel>?=null
    private var session:UserSessionManager?=null
    companion object {
        @JvmStatic
        fun newInstance(): PosterPackListingFragment {
            return PosterPackListingFragment()
        }
    }
    override fun getLayout(): Int {
        return R.layout.fragment_poster_pack_listing
    }

    override fun getViewModelClass(): Class<FestivePosterViewModel> {
        return FestivePosterViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        session = UserSessionManager(requireActivity())
        sharedViewModel = ViewModelProvider(requireActivity()).get(FestivePosterSharedViewModel::class.java)
        setObserver()
        setupList()
    }

    private fun setObserver() {
        sharedViewModel?.customizationDetails?.observe(viewLifecycleOwner,{

            Log.i(TAG, "customizationDetails Observer: ")


            adapter?.notifyDataSetChanged()

        })
    }


    private fun setupList() {

        viewModel?.getTemplateConfig(session?.fPID,session?.fpTag)
            ?.observe(viewLifecycleOwner,{
                Log.i(TAG, "template config: ${Gson().toJson(it)}")
            })

        val keyList = arrayListOf(KeyModel(
            "https://file-examples-com.github.io/uploads/2017/10/file_example_JPG_100kB.jpg","",10,"IMAGE_PATH","Image"),
            KeyModel(
                "Hello boost 36","",10,"Beautiful Smiles","Text"))


        dataList = arrayListOf(
            PosterPackModel("Navratri",10, arrayListOf(PosterModel(null,Keys = keyList),PosterModel(null,Keys = keyList))),
            PosterPackModel("Navratri",10, arrayListOf(PosterModel(null,Keys = keyList))),
            PosterPackModel("Navratri",10, arrayListOf(PosterModel(null,Keys = keyList))),
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
