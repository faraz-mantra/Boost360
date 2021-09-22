package com.onboarding.nowfloats.ui

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.base.BaseDialogFragment
import com.framework.base.BaseResponse
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.showKeyBoard
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.DialogCitySearchBinding
import com.onboarding.nowfloats.extensions.afterTextChanged
import com.onboarding.nowfloats.model.CityDataModel
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import com.onboarding.nowfloats.rest.response.ResponseDataCity
import com.onboarding.nowfloats.viewmodel.CityViewModel
import java.util.*
import kotlin.collections.ArrayList

class CitySearchDialog : BaseDialogFragment<DialogCitySearchBinding, CityViewModel>(), RecyclerItemClickListener {

  private var cityList = ArrayList<CityDataModel>()
  private var cityListFilter = ArrayList<CityDataModel>()
  private var baseAdapter: AppBaseRecyclerViewAdapter<CityDataModel>? = null
  var onClicked: (city: CityDataModel) -> Unit = { }

  override fun getLayout(): Int {
    return R.layout.dialog_city_search
  }

  override fun getViewModelClass(): Class<CityViewModel> {
    return CityViewModel::class.java
  }

  override fun getTheme(): Int {
    return R.style.CityDialogThemeFull
  }

  override fun onCreateView() {
    viewModel?.getCities(baseActivity)
      ?.observeOnce(viewLifecycleOwner, Observer { onGetCities(it) })
    binding?.edtSearchText?.afterTextChanged { filterCity(it) }
    binding?.ivClearText?.setOnClickListener { binding?.edtSearchText?.setText("") }
    binding?.close?.setOnClickListener { dismiss() }
  }

  private fun filterCity(str: String) {
    if (str.isEmpty()) {
      binding?.ivClearText?.gone()
      baseAdapter?.notify(cityList)
    } else {
      val query = str.trim().toLowerCase(Locale.ROOT)
      binding?.ivClearText?.visible()
      cityListFilter.clear()
      cityListFilter.addAll(cityList)
      val list = cityListFilter.filter {
        it.getCityName().startsWith(query) || it.getCityName().contains(query) || it.getStateName()
          .startsWith(query) || it.getStateName().contains(query)
      } as ArrayList<CityDataModel>
      baseAdapter?.notify(list)
    }
  }

  private fun onGetCities(response: BaseResponse) {
    if (response.error != null) {
      response.error?.localizedMessage?.let { showLongToast(it) }; return
    }
    val apiResponse = response as? ResponseDataCity ?: return
    cityList.clear()
    apiResponse.data?.let { cityList.addAll(it) }
    setAdapter(cityList)
    baseActivity.showKeyBoard(binding?.edtSearchText)
  }

  private fun setAdapter(cityList: ArrayList<CityDataModel>) {
    baseAdapter = AppBaseRecyclerViewAdapter(baseActivity, cityList, this)
    binding?.recyclerView?.layoutManager = LinearLayoutManager(baseActivity)
    binding?.recyclerView?.adapter = baseAdapter
    binding?.recyclerView?.let { baseAdapter?.runLayoutAnimation(it) }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    (item as? CityDataModel)?.let { onClicked(it) }
    dismiss()
  }
}