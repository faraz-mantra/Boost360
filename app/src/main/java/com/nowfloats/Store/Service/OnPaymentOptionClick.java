package com.nowfloats.Store.Service;

import com.nowfloats.Store.PaymentOptionsActivity;

/**
 * Created by Admin on 13-04-2018.
 */

public interface OnPaymentOptionClick {

    void onOptionClicked(PaymentOptionsActivity.PaymentType type);
    void onPickImage(PaymentOptionsActivity.PaymentType type, int requestCode);
}
