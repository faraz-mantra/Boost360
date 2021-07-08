package com.boost.upgrades.interfaces

import com.boost.upgrades.datamodule.SingleNetBankData

interface PaymentListener {

  fun walletSelected(data: String)
}