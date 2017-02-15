package com.nowfloats.Store.Model.OPCModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by NowFloats on 15-02-2017.
 */

public class UpdateDraftInvoiceModel {
    @SerializedName("fpUserProfileId")
    @Expose
    private String fPUserProfileId;
    @SerializedName("OPC")
    @Expose
    private String opc;
    @SerializedName("invoiceId")
    @Expose
    private String invoiceId;

    public UpdateDraftInvoiceModel(String fPUserProfileId, String opc, String invoiceId) {
        this.fPUserProfileId = fPUserProfileId;
        this.opc = opc;
        this.invoiceId = invoiceId;
    }


}
