package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.FragmentBrowseAllBinding
import com.festive.poster.models.*
import com.festive.poster.models.response.TemplateSaveActionBody
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.ui.TemplateDiffUtil
import com.festive.poster.utils.*
import com.festive.poster.viewmodels.PostUpdatesViewModel
import com.festive.poster.viewmodels.PromoUpdatesViewModel
import com.framework.base.BaseActivity
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.rest.NetworkResult
import com.framework.utils.convertListObjToString
import com.framework.utils.showToast
import com.framework.utils.toArrayList
import com.framework.webengageconstant.Promotional_Update_Browse_All_Loaded
import com.framework.webengageconstant.Promotional_Update_Category_Click

class BrowseAllFragment: AppBaseFragment<FragmentBrowseAllBinding, PostUpdatesViewModel>(),RecyclerItemClickListener {

    private var promoUpdatesViewModel: PromoUpdatesViewModel?=null
    private var session: UserSessionManager?=null
    private var selectedPos: Int=0
    private val DEFAULT_SELECTED_POS=0
    private var argTag:String?=null
    private var posterRvAdapter: AppBaseRecyclerViewAdapter<BrowseAllTemplate>?=null
    private var categoryAdapter: AppBaseRecyclerViewAdapter<BrowseAllCategory>?=null
    var categoryList = ArrayList<BrowseAllCategory>()

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
        initUi()
        getTemplatesData()
    }

    private fun initUi() {
        binding.rvCat.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPosters.layoutManager = LinearLayoutManager(requireActivity())
        categoryAdapter = AppBaseRecyclerViewAdapter(
            requireActivity() as BaseActivity<*, *>,
            categoryList,
            this
        )
        binding.rvCat.adapter = categoryAdapter

        posterRvAdapter = AppBaseRecyclerViewAdapter(
            requireActivity() as BaseActivity<*, *>,
            ArrayList(), this
        )
        binding.rvPosters.adapter = posterRvAdapter
        binding.rvPosters.layoutManager = LinearLayoutManager(requireActivity())
    }


    private fun getTemplatesData() {

        promoUpdatesViewModel?.browseAllLData?.observe(viewLifecycleOwner){
            when(it){
                is NetworkResult.Loading->{
                    if (posterRvAdapter?.isEmpty() == true)
                        showProgress()
                }
                is NetworkResult.Success->{
                    hideProgress()
                    val data = it.data?:return@observe
                    if (categoryList.isEmpty()){
                        setCatgories(data)
                        switchToSelectedItem(DEFAULT_SELECTED_POS,data[DEFAULT_SELECTED_POS].getParentTemplates()?.asBrowseAllModels())
                    }else{
                        switchToSelectedItem(selectedPos,data[selectedPos].getParentTemplates()?.asBrowseAllModels())
                        setCatgories(data)
                    }
                }
                is NetworkResult.Error->{
                    hideProgress()
                    showToast(it.msg)
                }
            }

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




    private fun setCatgories(it: java.util.ArrayList<CategoryUi>) {
        categoryList.clear()
        categoryList.addAll(it.asBrowseAllModels().toArrayList())
        selectCategory()
    }


    private fun switchToSelectedItem(positon:Int,newList: List<BrowseAllTemplate>?) {
        if (newList==null){
            return
        }
        selectedPos=positon
        selectCategory()
        posterRvAdapter?.setUpUsingDiffUtil(newList)
    }

    private fun selectCategory() {
        val selectedItem = categoryList[selectedPos]
        categoryList.forEach { it.isSelected = false }
        selectedItem.isSelected = true
        categoryAdapter?.notifyDataSetChanged()
        binding.tvCatTitle.text = selectedItem.name
        binding.tvCatSize.text = selectedItem.templates?.size.toString()
    }


    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when(actionType){
            RecyclerViewActionType.BROWSE_ALL_POSTER_CAT_CLICKED.ordinal->{
                WebEngageController.trackEvent(Promotional_Update_Category_Click)


                switchToSelectedItem(position,categoryList[position].templates)
            }
            RecyclerViewActionType.WHATSAPP_SHARE_CLICKED.ordinal->{
                posterWhatsappShareClicked(item as TemplateUi,
                    requireActivity() as BaseActivity<*, *>
                )
            }
            RecyclerViewActionType.POST_CLICKED.ordinal-> {
                posterPostClicked(item as TemplateUi, requireActivity() as AppBaseActivity<*, *>)
                }

            RecyclerViewActionType.POSTER_LOVE_CLICKED.ordinal->{
                callFavApi(item as TemplateUi)
            }
        }


        }






    private fun callFavApi(posterModel: TemplateUi) {
        promoUpdatesViewModel?.markAsFav(posterModel.isFavourite.not(),posterModel.id)
        promoUpdatesViewModel?.favStatus?.observe(viewLifecycleOwner){

            when(it){
                is NetworkResult.Loading->{
                    showProgress()
                }
                else->{
                    hideProgress()
                }
            }
        }

    }

    fun AppBaseRecyclerViewAdapter<BrowseAllTemplate>.setUpUsingDiffUtil(newList: List<BrowseAllTemplate>){
        val templateDiffUtil = TemplateDiffUtil(
            this.list,
            newList
        )

        val diffResult = DiffUtil.calculateDiff(templateDiffUtil)

        this.list.clear()
        this.list.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}