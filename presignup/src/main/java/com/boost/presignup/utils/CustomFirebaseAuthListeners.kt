package com.boost.presignup.utils

import com.boost.presignup.datamodel.userprofile.ConnectUserProfileResponse
import com.boost.presignup.datamodel.userprofile.UserProfileResponse
import com.boost.presignup.datamodel.userprofile.VerificationRequestResult

interface CustomFirebaseAuthListeners {
    fun onSuccess(response: UserProfileResponse?, uniqueId: String)
    fun onSuccess(response: ConnectUserProfileResponse?)
    fun onFailure()
    fun onSuccess(response: VerificationRequestResult?)
}