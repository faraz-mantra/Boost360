package com.appservice.ui.paymentgateway

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentKycDetailsBinding
import com.appservice.ui.catlogService.widgets.ClickType
import com.appservice.ui.catlogService.widgets.ImagePickerBottomSheet
import com.appservice.utils.getBitmap
import com.framework.extensions.visible
import com.framework.imagepicker.ImagePicker
import com.framework.models.BaseViewModel
import java.io.File


class KYCDetailsFragment : AppBaseFragment<FragmentKycDetailsBinding, BaseViewModel>(){

    private var useStoredBankAccount: Boolean = true
    private var imagePickerMultiple: Boolean ?= null
    private var bankStatementImage: File ?= null
    private var additionalDocs: ArrayList<File> =  ArrayList<File>()
    var gridView: GridView? = null


    companion object{
        @JvmStatic
        fun newInstance(bundle: Bundle? = null) : KYCDetailsFragment{
            val fragment = KYCDetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_kyc_details
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(
                binding?.btnSubmitDetails,
                binding?.bankAccountToggleGroup,
                binding?.btnRetakePanImage,
                binding?.btnBankStatementPicker,
                binding?.btnAdditionalDocs,
                binding?.btnClearBankStatementImage
        )
        radioButtonToggle()

        // Setup additional docs preview gridview
        gridView = binding?.gvAdditionalDocs
        gridView?.onItemClickListener = AdapterView.OnItemClickListener{ parent, v, position, id ->
            Log.d("Parent", parent.toString())
            Log.d("v", v.toString())
            Log.d("position", position.toString())
            Log.d("id", id.toString())

            additionalDocs.removeAt(position)
            val adapter = context?.let {
                AdditionalDocsGridviewAdapter(it,
                        R.layout.item_additional_doc_preview, additionalDocs)
            }
            gridView?.adapter = adapter
            // Adjust height of gridview. Causing crash as of now
//            setGridViewHeightBasedOnChildren(gridView, 3)
        }

        // Remove the upload docs preview
        binding?.bankStatementView?.visibility = View.GONE
        binding?.additionalDocsView?.visibility = View.GONE

        var panImagePath = arguments?.getString("PAN CARD IMAGE")
        var panImageUri = Uri.parse(panImagePath)
        binding?.imagePanCard?.setImageURI(panImageUri)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.btnSubmitDetails -> arguments?.let { startFragmentPaymentActivity(FragmentType.KYC_STATUS, it)}
            binding?.btnRetakePanImage -> {
                requireActivity().onBackPressed()
            }
            binding?.btnBankStatementPicker -> openImagePicker(false)
            binding?.btnAdditionalDocs -> openImagePicker(true)
            binding?.btnClearBankStatementImage -> {
                binding?.bankStatementView?.visibility = View.GONE
                bankStatementImage = null
            }
        }
    }

    private fun openImagePicker(allowMultiple: Boolean) {
        var filterSheet = ImagePickerBottomSheet()
        filterSheet.onClicked = {openImagePicker(it, allowMultiple)}
        filterSheet.show(this@KYCDetailsFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
    }

    private fun openImagePicker(it: ClickType, allowMultiple: Boolean){
        val type = when (it){
            ClickType.CAMERA -> ImagePicker.Mode.CAMERA
            ClickType.GALLERY -> ImagePicker.Mode.GALLERY
        }
        imagePickerMultiple = allowMultiple
        ImagePicker.Builder(baseActivity).mode(type)
                .compressLevel(ImagePicker.ComperesLevel.SOFT).directory(ImagePicker.Directory.DEFAULT)
                .extension(ImagePicker.Extension.PNG).allowMultipleImages(allowMultiple)
                .enableDebuggingMode(true).build()
    }

    private fun radioButtonToggle(){
        binding?.rbStoredAccount?.isChecked = true
        binding?.bankAccountToggleGroup?.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{
            group, id ->
            val radio = binding?.bankAccountToggleGroup?.findViewById<RadioButton>(id)
            if(radio?.id == binding?.rbAddDifferentAccount?.id){
                binding?.llAddBankDetails?.visibility = View.VISIBLE
            }else if(radio?.id == binding?.rbStoredAccount?.id){
                binding?.llAddBankDetails?.visibility = View.GONE
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK){
            val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as ArrayList<String>
            if(mPaths.size > 0){
                // Only a single bank statement file is coming.
                if(this.imagePickerMultiple == false){
                    bankStatementImage = File(mPaths[0])
                    binding?.bankStatementView?.visible()
                    bankStatementImage?.getBitmap()?.let{binding?.ivBankStatement?.setImageBitmap(it)}
                }
                // Multiple files might come. These are the additional docs
                else{
                    additionalDocsViewPopulation(mPaths)
                }
            }
        }
    }

    private fun additionalDocsViewPopulation(mPaths: ArrayList<String>){
        // Some docs may already be added. If space for at least one more, then process
        if(additionalDocs.size < 5){
            if(mPaths.size + additionalDocs.size > 5){
                Toast.makeText(context, "Only 5 files are allowed. Discarding the rest.", Toast.LENGTH_LONG).show()
            }
            // Using up to 5 files
            var index: Int = additionalDocs.size
            // Add all files
            while(index < 5 && mPaths.isNotEmpty()){
                additionalDocs.add(File(mPaths[0]))
                mPaths.removeAt(0)
                index++
            }

            val adapter = context?.let {
                AdditionalDocsGridviewAdapter(it,
                        R.layout.item_additional_doc_preview, additionalDocs)
            }
            gridView?.adapter = adapter
//            setGridViewHeightBasedOnChildren(gridView, 3)
        }else{
            Toast.makeText(context, "Only 5 files are allowed.", Toast.LENGTH_LONG).show()
        }
    }


    // Dynamically set height of GridView for Additional Docs display based on number of children
    private fun setGridViewHeightBasedOnChildren(gridView: GridView?, noOfColumns: Int?){
        var gridViewAdapter: ListAdapter? = gridView?.adapter

        if(gridViewAdapter == null){
            return
        }

        var totalHeight: Int //total height to set on grid view

        val items = gridViewAdapter.count //no. of items in the grid

        val rows: Int //no. of rows in grid


        val listItem = gridViewAdapter.getView(0, null, gridView)
        listItem.measure(0, 0)
        totalHeight = listItem.measuredHeight

        val x: Float
        if (items > noOfColumns!!) {
            x = (items / noOfColumns!!).toFloat()

            //Check if exact no. of rows of rows are available, if not adding 1 extra row
            rows = if (items % noOfColumns!! !== 0) {
                (x + 1) as Int
            } else {
                x.toInt()
            }
            totalHeight *= rows

            //Adding any vertical space set on grid view
            totalHeight += gridView!!.verticalSpacing * rows
        }
        //Setting height on grid view
        val params = gridView!!.layoutParams
        params.height = totalHeight
        gridView!!.layoutParams = params
    }
}