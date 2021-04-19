package com.boost.presignin.viewmodel

import androidx.lifecycle.LiveData
import com.boost.presignin.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class LoginSignUpViewModel : BaseViewModel() {
    fun checkMobileIsRegistered(number: Long?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.isMobileIsRegistered(number).toLiveData()
    }

}