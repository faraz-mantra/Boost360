package com.nowfloats.instagram.graph

import com.nowfloats.facebook.models.BaseFacebookGraphResponse

class IGGraphManager {

    interface GraphRequestIGAccountCallback {
        fun onCompleted(
            facebookGraphResponse: BaseFacebookGraphResponse?
        )
    }
}