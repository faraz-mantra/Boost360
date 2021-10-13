package com.festive.poster.ui

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.FragmentPosterListBinding
import com.festive.poster.databinding.SheetEditShareMessageBinding
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson

class PosterListFragment: AppBaseFragment<FragmentPosterListBinding, FestivePosterViewModel>(),RecyclerItemClickListener {

    private var adapter: AppBaseRecyclerViewAdapter<PosterModel>?=null
    private var sharedViewModel: FestivePosterSharedViewModel?=null
    private val TAG = "PosterListFragment"

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
    }

    private fun observeCustomization() {
        sharedViewModel?.customizationDetails?.observe(viewLifecycleOwner,{
            Log.i(TAG, "observeCustomization: ${Gson().toJson(it)}")


            dataList?.forEach { template->
                template.keys.forEach { posterKeyModel ->
                    if (posterKeyModel.name=="Title"){
                        posterKeyModel.custom = it.name
                    }
                }
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
                dataList = response.Result.templates.toArrayList()
                dataList?.forEach { posterModel -> posterModel.isPurchased=true }
                dataList?.forEach { posterModel -> posterModel.greeting_message="Happy Navratri. Enjoy black friday sale upto 50% discount on our postselected merchandise." }
                observeCustomization()

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
            RecyclerViewActionType.POSTER_GREETING_MSG_CLICKED.ordinal->{
                item as PosterModel
                showEditGreetingMessageSheet(position)

            }
        }
    }

    private fun showEditGreetingMessageSheet(position: Int) {
        val sheet = BottomSheetDialog(requireActivity(),R.style.BottomSheetTheme)
        val sheetBinding = DataBindingUtil.inflate<SheetEditShareMessageBinding>(layoutInflater,R.layout.sheet_edit_share_message,null,false)
        sheet.setContentView(sheetBinding.root)
        sheetBinding.tvUpdateInfo.setOnClickListener {
            dataList?.get(position)?.greeting_message=sheetBinding.etDesc.text.toString()
            adapter?.notifyItemChanged(position)
            sheet.dismiss()
        }
        sheet.show()
    }
}