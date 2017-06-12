package com.nowfloats.riachatsdk.interfaces;

/**
 * Created by NowFloats on 18-05-2017.
 */

public interface IConfirmationCallback {
    void onPositiveResponse(String confirmationType, String... confirmationText);
    void onNegativeResponse(String confirmationType, String... data);
}
