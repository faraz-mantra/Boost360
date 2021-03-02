package com.appservice.appointment

import android.os.Bundle
import android.view.*
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.RecyclerViewItemType
import com.appservice.databinding.FragmentCreateCategoryBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catalog.startFragmentActivity
import com.framework.models.BaseViewModel
import kotlinx.android.synthetic.main.fragment_create_category.*
import kotlinx.android.synthetic.main.toolbar_catalog.*
import java.io.Serializable
import java.util.*

class CreateCategoryFragment : AppBaseFragment<FragmentCreateCategoryBinding, BaseViewModel>(), RecyclerItemClickListener {
    private var list: MutableList<Category> = mutableListOf(Category(name = "Books", 2),
            Category("Stationary", 6),
            Category("Novel", 8),
            Category("Science", 10),
            Category("Rebook", 25))

    override fun getLayout(): Int {
        return R.layout.fragment_create_category
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
//        setToolbarTitle("Categories (${list.size})")
        binding?.rvCategory?.apply {
            val adapterN = AppBaseRecyclerViewAdapter(baseActivity, ArrayList(list), this@CreateCategoryFragment)
            adapter = adapterN
        }
setOnClickListener(binding?.btnAddNewCategory)
    }

    companion object {
        fun newInstance(): CreateCategoryFragment {
            val args = Bundle()

            val fragment = CreateCategoryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_service_listing, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_service_configuration -> {
                startFragmentActivity(FragmentType.APPOINTMENT_SETTINGS)
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnAddNewCategory -> {
                showCreateCategory()
            }
        }
    }

    private fun showCreateCategory() {
        val bottomSheetCreateCategory = BottomSheetCreateCategory()
        bottomSheetCreateCategory.show(parentFragmentManager, BottomSheetCreateCategory::class.java.name)
    }
}

data class Category(var name: String, var countItems: Int? = 0, var isSelected: Boolean? = false, var recyclerViewItem: Int = RecyclerViewItemType.CREATE_CATEGORY_ITEM_VIEW.getLayout()) : AppBaseRecyclerViewItem,Serializable {
    override fun getViewType(): Int {
        return recyclerViewItem
    }

}