package com.inventoryorder.ui.createappointment

import android.content.DialogInterface
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetPickInventoryNatureBinding
import com.inventoryorder.model.bottomsheet.PickInventoryNatureModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener

class PickInventoryNatureBottomSheetDialog : BaseBottomSheetDialog<BottomSheetPickInventoryNatureBinding, BaseViewModel>(), RecyclerItemClickListener {

    private var list = ArrayList<PickInventoryNatureModel>()
    var adapter: AppBaseRecyclerViewAdapter<PickInventoryNatureModel>? = null
    var onDoneClicked: (pickInventoryNatureModel: PickInventoryNatureModel?) -> Unit = {}

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_pick_inventory_nature
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    fun setList(list: ArrayList<PickInventoryNatureModel>) {
        this.list.clear()
        this.list.addAll(list)
    }

    override fun onCreateView() {
        setRecyclerViewPickInventoryNature()
        setOnClickListener(binding?.buttonDone, binding?.tvCancel)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.buttonDone -> {
                dismiss()
            }
            binding?.tvCancel -> {
                dismiss()
            }

        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun setRecyclerViewPickInventoryNature() {
        binding?.recyclerViewPickInventoryNature?.post {
            adapter = AppBaseRecyclerViewAdapter(baseActivity, list ,this)
            val linearLayoutManager = LinearLayoutManager(baseActivity,LinearLayoutManager.VERTICAL,false)
//            binding?.recyclerViewPickInventoryNature?.addItemDecoration(
//                 DividerItemDecorationPIN(AppCompatResources.getDrawable(baseActivity,R.drawable.horizontal_dashed_line),  true,  true))
            binding?.recyclerViewPickInventoryNature?.layoutManager = linearLayoutManager
            binding?.recyclerViewPickInventoryNature?.adapter = adapter
            binding?.recyclerViewPickInventoryNature?.let { adapter?.runLayoutAnimation(it) }
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        val selectItem = item as PickInventoryNatureModel
        list.forEach { it.isInventorySelected =  ( it.inventoryTypeSelectedIcon == selectItem.inventoryTypeSelectedIcon ) }
        adapter?.notifyDataSetChanged()
        onDoneClicked(selectItem)
//        dismiss()
    }


}