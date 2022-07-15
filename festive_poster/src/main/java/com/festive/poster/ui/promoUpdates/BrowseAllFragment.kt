package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.databinding.FragmentBrowseAllBinding
import com.festive.poster.models.*
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.utils.*
import com.festive.poster.viewmodels.PostUpdatesViewModel
import com.festive.poster.viewmodels.PromoUpdatesViewModel
import com.framework.base.BaseActivity
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.convertListObjToString
import com.framework.utils.convertStringToList
import com.framework.utils.toArrayList
import com.framework.webengageconstant.Promotional_Update_Browse_All_Loaded
import com.framework.webengageconstant.Promotional_Update_Category_Click
import com.google.gson.Gson
import kotlin.random.Random
import kotlin.random.nextInt

class BrowseAllFragment: AppBaseFragment<FragmentBrowseAllBinding, PostUpdatesViewModel>(),RecyclerItemClickListener {

    private var promoUpdatesViewModel: PromoUpdatesViewModel?=null
    private var session: UserSessionManager?=null
    private var selectedPos: Int=0
    private var argTag:String?=null
    private var posterRvAdapter: AppBaseRecyclerViewAdapter<PosterModel>?=null
    private var categoryAdapter: AppBaseRecyclerViewAdapter<PosterPackModel>?=null
    var categoryList:ArrayList<PosterPackModel>?=null

    override fun getLayout(): Int {
        return R.layout.fragment_browse_all
    }

    override fun getViewModelClass(): Class<PostUpdatesViewModel> {
        return PostUpdatesViewModel::class.java
    }
    companion object {

        val BK_SELECTED_POS_TAG="BK_SELECTED_POS"

        fun newInstance(selectedPosTag:String?=null): BrowseAllFragment {
            val bundle: Bundle = Bundle()
            bundle.putString(BK_SELECTED_POS_TAG,selectedPosTag)
            val fragment = BrowseAllFragment()
            fragment.arguments = bundle
            return fragment
        }


    }

    override fun onCreateView() {
        super.onCreateView()
        WebEngageController.trackEvent(Promotional_Update_Browse_All_Loaded)
        argTag = arguments?.getString(BK_SELECTED_POS_TAG)
        promoUpdatesViewModel = ViewModelProvider(requireActivity()).get(PromoUpdatesViewModel::class.java)
        getTemplatesData()
    }

    private fun getTemplatesData() {
        showProgress()
        promoUpdatesViewModel?.browseAllLData?.observe(viewLifecycleOwner){
            hideProgress()
            categoryList=it
            categoryList?.forEach { it.isSelected=false }
            selectedPos = categoryList?.indexOfFirst { it.tagsModel?.tag==argTag}?:0
            if (selectedPos==-1) selectedPos=0
            setDataOnUi()
        }
    }

    override fun onResume() {
        super.onResume()
        session = UserSessionManager(requireActivity())

        refreshUserWidgets()

    }

    private fun refreshUserWidgets() {
        viewModel?.getUserDetails(session?.fpTag, clientId)?.observe(this) {
            if (it.isSuccess()) {
                val detail = it as? CustomerDetails
                detail?.FPWebWidgets?.let { list ->
                    session?.storeFPDetails(
                        Key_Preferences.STORE_WIDGETS,
                        convertListObjToString(list)
                    )

                }
            }
        }
    }

    private fun setDataOnUi() {
        categoryList?.forEach {pack->

            pack.list_layout =RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()
            pack.posterList?.forEach {poster->
                poster.variants?.firstOrNull()?.svgUrl =getLottieUrl()
                poster.layout_id = RecyclerViewItemType.TEMPLATE_VIEW_FOR_RV.getLayout()
            }
        }

        switchToSelectedItem()


        categoryAdapter =AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,categoryList!!,this)
        binding.rvCat.adapter = categoryAdapter
        binding.rvCat.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)


    }

    fun getLottieUrl():String{
        val list = arrayListOf(
            "https://assets4.lottiefiles.com/packages/lf20_uyf6evkj.json",
            "https://assets2.lottiefiles.com/packages/lf20_pghdouhq.json",
            "https://assets2.lottiefiles.com/packages/lf20_w9bdffcb.json",
            "https://assets2.lottiefiles.com/packages/lf20_yg3asqro.json",
            "https://assets9.lottiefiles.com/private_files/lf30_dhkktwhk.json",
            "https://assets9.lottiefiles.com/packages/lf20_fo0grcos.json",
            "https://assets9.lottiefiles.com/packages/lf20_w5hernhv.json"
        )

        return list[Random.nextInt(0..6)]
    }
    private fun switchToSelectedItem() {
        val selectedItem = categoryList?.get(selectedPos)
        selectedItem?.isSelected =true
        binding?.tvCatTitle?.text = selectedItem?.tagsModel?.name
        binding?.tvCatSize?.text = selectedItem?.posterList?.size.toString()
        categoryList?.get(selectedPos)?.posterList?.let {
            posterRvAdapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,
                it,this)
            binding?.rvPosters?.adapter = posterRvAdapter
            binding?.rvPosters?.layoutManager = LinearLayoutManager(requireActivity())
        }

    }


    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when(actionType){
            RecyclerViewActionType.BROWSE_ALL_POSTER_CAT_CLICKED.ordinal->{
                WebEngageController.trackEvent(Promotional_Update_Category_Click)

                categoryList?.forEach { it.isSelected =false }
                categoryList?.get(position)?.isSelected=true
                categoryAdapter?.notifyDataSetChanged()
                selectedPos = position
                switchToSelectedItem()
            }
            RecyclerViewActionType.WHATSAPP_SHARE_CLICKED.ordinal->{
                posterWhatsappShareClicked(item as PosterModel,
                    requireActivity() as BaseActivity<*, *>
                )
            }
            RecyclerViewActionType.POST_CLICKED.ordinal-> {
                posterPostClicked(item as PosterModel, requireActivity() as AppBaseActivity<*, *>)
                }

            RecyclerViewActionType.POSTER_LOVE_CLICKED.ordinal->{
                callFavApi(item as PosterModel)
            }
        }


        }




    private fun callFavApi(posterModel: PosterModel) {
        viewModel?.favPoster(session?.fPID,session?.fpTag,posterModel.id,)?.observe(viewLifecycleOwner){
            if (it.isSuccess()){
                posterModel.details?.Favourite= posterModel.details?.Favourite?.not() == true
            }
        }

    }
}