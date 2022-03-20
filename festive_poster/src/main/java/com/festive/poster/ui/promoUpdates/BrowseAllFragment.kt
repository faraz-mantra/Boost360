package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
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

class BrowseAllFragment: AppBaseFragment<FragmentBrowseAllBinding, PostUpdatesViewModel>(),RecyclerItemClickListener {

    private var session: UserSessionManager?=null
    private var selectedPos: Int=0
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

        val BK_POSTER_PACK_LIST="POSTER_PACK_LIST"
        val BK_SELECTED_POS="BK_SELECTED_POS"

        fun newInstance(dataList:ArrayList<PosterPackModel>,selectedPos:Int): BrowseAllFragment {
            val bundle: Bundle = Bundle()
            bundle.putString(BK_POSTER_PACK_LIST,Gson().toJson(dataList))
            bundle.putInt(BK_SELECTED_POS,selectedPos)
            val fragment = BrowseAllFragment()
            fragment.arguments = bundle
            return fragment
        }


    }

    override fun onCreateView() {
        super.onCreateView()
        WebEngageController.trackEvent(Promotional_Update_Browse_All_Loaded)
        selectedPos = arguments?.getInt(BK_SELECTED_POS)?:0
        categoryList = convertStringToList<PosterPackModel>(arguments?.getString(
            BK_POSTER_PACK_LIST)!!)?.toArrayList()
       setDataOnUi()
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
                poster.layout_id = RecyclerViewItemType.TEMPLATE_VIEW_FOR_RV.getLayout()
            }
        }

        switchToSelectedItem()


        categoryAdapter =AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,categoryList!!,this)
        binding?.rvCat?.adapter = categoryAdapter
        binding?.rvCat?.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)


    }

    private fun switchToSelectedItem() {
        val selectedItem = categoryList?.get(selectedPos)
        selectedItem?.isSelected =true
        binding?.tvCatTitle?.text = selectedItem?.tagsModel?.name
        binding?.tvCatSize?.text = selectedItem?.posterList?.size.toString()
        posterRvAdapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,
            categoryList?.get(selectedPos)?.posterList!!,this)
        binding?.rvPosters?.adapter = posterRvAdapter
        binding?.rvPosters?.layoutManager = LinearLayoutManager(requireActivity())
    }

    fun setDummyCats(){
        categoryList = ArrayList<PosterPackModel>()

        //dummy data
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))
        categoryList?.add(PosterPackModel(
            PosterPackTagModel("","","","",false,-1),
            null,0.0,false,RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT.getLayout()))


        categoryAdapter =AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,categoryList!!,this)
        binding?.rvCat?.adapter = categoryAdapter
        binding?.rvCat?.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)

        setupDummyPosterList()

    }

    fun setupDummyPosterList(){
        val dataList = arrayListOf(
            PosterModel(
                true,
                "",
                PosterDetailsModel(
                    "", false, 0.0, "",
                    false
                ),
                "",
                ArrayList(),
                ArrayList(),
                "",
                ArrayList(),
                "",
                RecyclerViewItemType.TEMPLATE_VIEW_FOR_RV.getLayout(),
            ),
            PosterModel(
                true,
                "",
                PosterDetailsModel(
                    "", false, 0.0, "",
                    false
                ),
                "",
                ArrayList(),
                ArrayList(),
                "",
                ArrayList(),
                "",
                RecyclerViewItemType.TEMPLATE_VIEW_FOR_RV.getLayout(),
            )
        )

        posterRvAdapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>,dataList,this)
        binding?.rvPosters?.adapter = posterRvAdapter
        binding?.rvPosters?.layoutManager = LinearLayoutManager(requireActivity())


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
                posterPostClicked(item as PosterModel, requireActivity() as BaseActivity<*, *>)
                }
            }

        }


    override fun onChildClick(
        childPosition: Int,
        parentPosition: Int,
        childItem: BaseRecyclerViewItem?,
        parentItem: BaseRecyclerViewItem?,
        actionType: Int
    ) {
        when(actionType){
            RecyclerViewActionType.POSTER_LOVE_CLICKED.ordinal->{
                callFavApi(childItem as PosterModel)
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