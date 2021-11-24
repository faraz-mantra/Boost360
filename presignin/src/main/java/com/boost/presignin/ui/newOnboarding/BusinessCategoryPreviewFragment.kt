package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.LayoutBusinessCategoryPreviewBinding
import com.boost.presignin.databinding.LayoutSetUpMyWebsiteStep1Binding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.category.CategoryDataModel
import com.boost.presignin.model.category.CategoryDataModelOv2
import com.boost.presignin.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.boost.presignin.recyclerView.RecyclerItemClickListener
import com.boost.presignin.rest.response.ResponseDataCategoryOv2
import com.boost.presignin.viewmodel.CategoryVideoModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.webengageconstant.*

class BusinessCategoryPreviewFragment : AppBaseFragment<LayoutBusinessCategoryPreviewBinding, CategoryVideoModel>(),
    RecyclerItemClickListener {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): BusinessCategoryPreviewFragment {
            val fragment = BusinessCategoryPreviewFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var baseAdapter: AppBaseRecyclerViewAdapter<CategoryDataModelOv2>
    private var categoryList = ArrayList<CategoryDataModelOv2>()
    private val phoneNumber by lazy {
        arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
    }


    override fun getLayout(): Int {
      return R.layout.layout_business_category_preview
    }

    override fun getViewModelClass(): Class<CategoryVideoModel> {
        return CategoryVideoModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.tvNextStep, binding?.layoutMobile, binding?.layoutDesktop)

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.tvNextStep->{
                addFragment(R.id.inner_container,SetupMyWebsiteStep2Fragment.newInstance(Bundle().apply { putString(IntentConstant.EXTRA_PHONE_NUMBER.name,phoneNumber) }),true)
            }
            binding?.layoutMobile -> {
                setUpButtonSelectedUI()
            }
            binding?.layoutDesktop -> {
                setUpButtonSelectedUI(isMobilePreviewMode = false)
            }
        }
    }

    private fun setUpButtonSelectedUI(isMobilePreviewMode:Boolean = true) {
        if (isMobilePreviewMode){
            binding?.layoutMobile?.setBackgroundResource(R.drawable.ic_presignin_bg_yellow_solid_stroke)
            binding?.layoutDesktop?.setBackgroundResource(0)
            binding?.ivMobile?.visible()
            binding?.ivDesktop?.gone()
            binding?.ivWebsitePreview?.setImageResource(R.drawable.mobile_preview_website)
        }else{
            binding?.layoutMobile?.setBackgroundResource(0)
            binding?.layoutDesktop?.setBackgroundResource(R.drawable.ic_presignin_bg_yellow_solid_stroke)
            binding?.ivMobile?.gone()
            binding?.ivDesktop?.visible()
            binding?.ivWebsitePreview?.setImageResource(R.drawable.desktop_preview_website)
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {

        }

    }
}