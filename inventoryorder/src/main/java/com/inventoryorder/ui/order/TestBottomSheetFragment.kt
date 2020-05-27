package com.inventoryorder.ui.order

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.FragmentTestBottomSheetBinding
import com.inventoryorder.model.bottomsheet.PickInventoryNatureModel
import com.inventoryorder.ui.createappointment.PickInventoryNatureBottomSheetDialog

class TestBottomSheetFragment : BaseFragment<FragmentTestBottomSheetBinding,BaseViewModel>(){

    private var pickInventoryNatureBottomSheetDialog : PickInventoryNatureBottomSheetDialog ? = null
    private var selectPickInventoryNatureList = PickInventoryNatureModel().getData()
    var setBackground : Boolean = false

    companion object{
        fun newInstance( bundle: Bundle? = null) : TestBottomSheetFragment{
            val fragment =  TestBottomSheetFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_test_bottom_sheet
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.llTestBottomSheet)

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.llTestBottomSheet ->{
                binding?.frameLayoutVisibility?.visibility = View.VISIBLE
                showBottomSheetDialogPickInventoryNature()
            }

        }
    }


    private fun showBottomSheetDialogPickInventoryNature(){
        pickInventoryNatureBottomSheetDialog = PickInventoryNatureBottomSheetDialog()
        pickInventoryNatureBottomSheetDialog?.onDoneClicked = { selectPickInventoryNatureList (it)}
        pickInventoryNatureBottomSheetDialog?.setList( selectPickInventoryNatureList )
        pickInventoryNatureBottomSheetDialog?.show(this.parentFragmentManager, PickInventoryNatureBottomSheetDialog::class.java.name)

    }

    private fun selectPickInventoryNatureList(list: PickInventoryNatureModel?) {
        selectPickInventoryNatureList.forEach { it.isInventorySelected = (it.inventoryName == list?.inventoryName) }

    }


}