package com.festive.poster.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.FragmentPosterListBinding
import com.festive.poster.models.PosterKeyModel
import com.festive.poster.models.PosterModel
import com.festive.poster.models.response.GetTemplatesResponse
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.festive.poster.viewmodels.FestivePosterViewModel
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.utils.toArrayList

class PosterListFragment: AppBaseFragment<FragmentPosterListBinding, FestivePosterViewModel>(),RecyclerItemClickListener {

    private var adapter: AppBaseRecyclerViewAdapter<PosterModel>?=null
    private var sharedViewModel: FestivePosterSharedViewModel?=null

    companion object {
        val BK_TAG="BK_TAG"
        @JvmStatic
        fun newInstance(tag:String): PosterListFragment {
            val bundle = Bundle().apply {
                putString(BK_TAG,tag)
            }
            val fragment =PosterListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    var packTag:String?=null
    private var session:UserSessionManager?=null
    private var dataList:ArrayList<PosterModel>?=null
    override fun getLayout(): Int {
        return R.layout.fragment_poster_list
    }

    override fun getViewModelClass(): Class<FestivePosterViewModel> {
        return FestivePosterViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        packTag= arguments?.getString(BK_TAG)

        sharedViewModel = ViewModelProvider(requireActivity()).get(FestivePosterSharedViewModel::class.java)
        session = UserSessionManager(requireActivity())
        setupList()
        observeCustomization()
    }

    private fun observeCustomization() {
        sharedViewModel?.customizationDetails?.observe(viewLifecycleOwner,{
            dataList?.forEach { template->
                template.Keys.find {key-> key.Name=="Title" }?.Custom= it.name
            }
            adapter?.notifyDataSetChanged()
        })
    }

    private fun setupList() {
        viewModel?.getTemplates(
            session?.fPID,
            session?.fpTag,
            arrayListOf(packTag!!)
        )?.observe(viewLifecycleOwner,{
            val response = it as? GetTemplatesResponse
            response?.let {
                dataList = response.Result.Templates.toArrayList()
                dataList?.forEach { posterModel -> posterModel.isPurchased=true }

                adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,dataList!!,this)
                binding?.rvPosters?.adapter = adapter
                binding?.rvPosters?.layoutManager = LinearLayoutManager(requireActivity())

            }
        })



    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when(actionType){
            RecyclerViewActionType.POSTER_TAP_TO_EDIT_CLICK.ordinal->{
                CustomizePosterSheet().show(requireActivity().supportFragmentManager,CustomizePosterSheet::class.java.name)

            }
        }
    }

}