package com.nowfloats.Store.Model.OPCModels;

import java.util.ArrayList;

/**
 * Created by NowFloats on 09-11-2016.
 */

public class OPCInvoice {
    public double TotalAmount;
    public double NetAmount;
    public Object Taxes;
    public ArrayList<OPCItems> Items;
}
