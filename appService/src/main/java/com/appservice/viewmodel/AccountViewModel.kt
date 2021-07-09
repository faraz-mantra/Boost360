package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.model.account.AccountCreateRequest
import com.appservice.model.account.BankAccountDetailsN
import com.appservice.rest.repository.RazorRepository
import com.appservice.rest.repository.WithFloatRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class AccountViewModel : BaseViewModel() {

  fun userAccountDetails(fpId: String?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatRepository.userAccountDetail(fpId, clientId).toLiveData()
  }

  fun createAccount(request: AccountCreateRequest?): LiveData<BaseResponse> {
    return WithFloatRepository.createAccount(request).toLiveData()
  }

  fun updateAccount(fpId: String?, clientId: String?, request: BankAccountDetailsN?): LiveData<BaseResponse> {
    return WithFloatRepository.updateAccount(fpId, clientId, request).toLiveData()
  }

  fun ifscDetail(ifsc: String?): LiveData<BaseResponse> {
    return RazorRepository.ifscDetail(ifsc).toLiveData()
  }
}
