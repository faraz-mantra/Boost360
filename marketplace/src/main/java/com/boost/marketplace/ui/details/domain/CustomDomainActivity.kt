package com.boost.marketplace.ui.details.domain

import android.app.ProgressDialog
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.data.api_model.CustomDomain.Domain
import com.boost.dbcenterapi.data.api_model.CustomDomain.DomainRequest
import com.boost.marketplace.R
import com.boost.marketplace.adapter.CustomDomainListAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityCustomDomainBinding
import com.framework.utils.hideKeyBoard


class CustomDomainActivity : AppBaseActivity<ActivityCustomDomainBinding, CustomDomainViewModel>() {

    lateinit var customDomainListAdapter: CustomDomainListAdapter
    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
    var experienceCode: String? = null
    var fpid: String? = null
    var totalFreeItemList: List<Domain>? = null

    lateinit var progressDialog: ProgressDialog

    override fun getLayout(): Int {
        return R.layout.activity_custom_domain
    }

    companion object {
        fun newInstance() = CustomDomainActivity()
    }

    override fun getViewModelClass(): Class<CustomDomainViewModel> {
        return CustomDomainViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()

        experienceCode = intent.getStringExtra("expCode")
        fpid = intent.getStringExtra("fpid")
        viewModel.setApplicationLifecycle(application, this)
        viewModel = ViewModelProviders.of(this).get(CustomDomainViewModel::class.java)
        customDomainListAdapter= CustomDomainListAdapter(this,ArrayList())
        progressDialog = ProgressDialog(this)

        binding?.help?.setOnClickListener {
            val dialogCard = CustomDomainHelpBottomSheet()
            dialogCard.show(this.supportFragmentManager, CustomDomainHelpBottomSheet::class.java.name)
        }
        binding?.btnSelect?.setOnClickListener {
            val dialogCard = ConfirmedCustomDomainBottomSheet()
            dialogCard.show(this.supportFragmentManager, ConfirmedCustomDomainBottomSheet::class.java.name)
        }
        binding?.btnSelectDomain?.setOnClickListener {
            val dialogCard = ConfirmedCustomDomainBottomSheet()
            dialogCard.show(this.supportFragmentManager, ConfirmedCustomDomainBottomSheet::class.java.name)
        }
        binding?.tv3?.setOnClickListener {
            val dialogCard = SSLCertificateBottomSheet()
            dialogCard.show(this.supportFragmentManager, SSLCertificateBottomSheet::class.java.name)
        }
        binding?.addonsBack?.setOnClickListener {
            finish()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(getResources().getColor(com.boost.cart.R.color.common_text_color))
        }

        loadData()
        initMVVM()
        initRecyclerView()

        binding?.etDomain?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!=null && p0?.length!! >1){
                    binding?.searchBtn?.visibility=View.VISIBLE
                    binding?.searchBtn?.setOnClickListener {
                        hideKeyBoard()
                        binding?.searchBtn?.visibility=View.GONE
                        binding?.tvTitle?.text="Search results"
                        updateAllItemBySearchValue(p0.toString())
                    }
                    binding?.ivCross?.visibility=View.VISIBLE
                } else{
                    binding?.tvTitle?.text="Suggested domains for you"
                    binding?.searchBtn?.visibility=View.GONE
                    binding?.searchResults?.visibility=View.GONE
                    binding?.ivCross?.visibility=View.GONE
                    totalFreeItemList?.let { updateFreeAddonsRecycler(it) }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding?.ivCross?.setOnClickListener {
            binding?.ivCross?.visibility=View.GONE
            binding?.searchResults?.visibility=View.GONE
            binding?.tvTitle?.text="Suggested domains for you"
            totalFreeItemList?.let { updateFreeAddonsRecycler(it) }
            binding?.etDomain?.text= null
            binding?.layoutAvailable?.visibility=View.GONE
            binding?.layoutNotAvailable?.visibility=View.GONE
        }
    }

    private fun loadData() {
        experienceCode?.let { DomainRequest(clientid, it) }?.let { viewModel.GetSuggestedDomains(it) }
    }

    private fun initMVVM() {
        viewModel.updateResult().observe(this, androidx.lifecycle.Observer {
            totalFreeItemList=it.domains
            updateFreeAddonsRecycler(it.domains)
        })

        viewModel.updatesLoader().observe(this, androidx.lifecycle.Observer {
            if (it) {
                binding?.scrollView?.visibility=View.GONE
                binding?.shimmerViewDomain?.visibility=View.VISIBLE
            } else {
                binding?.scrollView?.visibility=View.VISIBLE
                binding?.shimmerViewDomain?.visibility=View.GONE
            }
        })
    }

    fun initRecyclerView() {
        val gridLayoutManager = LinearLayoutManager(this )
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.rvCustomDomain?.apply {
            layoutManager = gridLayoutManager
            binding?.rvCustomDomain?.adapter = customDomainListAdapter
        }
    }

    private fun updateFreeAddonsRecycler(list: List<Domain>) {
        customDomainListAdapter.addupdates(list)
        customDomainListAdapter.notifyDataSetChanged()
        binding?.rvCustomDomain?.setFocusable(false)
    }

    fun updateAllItemBySearchValue(searchValue: String){
        val freeitemList: java.util.ArrayList<Domain> = arrayListOf()
        for(singleFreeFeature in totalFreeItemList!!){
            if(singleFreeFeature.name.lowercase().indexOf(searchValue.lowercase()) != -1) {
                binding?.tvTitle?.text="Search results"
                freeitemList.add(singleFreeFeature)

              if ( singleFreeFeature.name.lowercase().equals(searchValue.lowercase()) && searchValue.length >7 ) {
                  if (singleFreeFeature.isAvailable){
                      binding?.layoutAvailable?.visibility=View.VISIBLE
                      binding?.tv1?.text=singleFreeFeature.name
                  }
                  else {
                      binding?.layoutAvailable?.visibility=View.GONE
                      binding?.layoutNotAvailable?.visibility=View.VISIBLE
                      binding?.tv?.text=singleFreeFeature.name
                  }
              }
                else{
                  binding?.layoutAvailable?.visibility=View.GONE
                  binding?.layoutNotAvailable?.visibility=View.GONE
              }
                updateFreeAddonsRecycler(freeitemList)
            }
            else{
                binding?.tvTitle?.visibility=View.VISIBLE
                updateFreeAddonsRecycler(totalFreeItemList!!)
            }
        }
        updateFreeAddonsRecycler(freeitemList)
    }
}