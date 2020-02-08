package com.boost.presignup.utils

import com.boost.presignup.datamodel.userprofile.UserProfileResponse

interface CustomFirebaseAuthListeners {
    fun onSuccess(response: UserProfileResponse?)
    fun onFailure()
}