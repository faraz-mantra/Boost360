package com.appservice.appointment.model

import java.io.Serializable

data class AccountDetailsModel(
        var accountHolderName: String? = null,
        var accountNumber: Int? = null,
        var bankName: String? = null,
        var ifcsCode: String? = null,
        var accountAlias: String? = null,
):Serializable{

}
