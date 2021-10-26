package com.appservice.model.aptsetting

import java.io.Serializable

data class AccountDetailsModel(
        var accountHolderName: String? = null,
        var accountNumber: Int? = null,
        var bankName: String? = null,
        var ifcsCode: String? = null,
        var accountAlias: String? = null,
):Serializable{

}
