package com.nowfloats.BusinessProfile.UI.Model;

import java.util.ArrayList;

/**
 * Created by NowFloatsDev on 26/05/2015.
 */
public class Business_Update_Event {

    public ArrayList<String> updateList ;

    public Business_Update_Event(ArrayList<String> response)
    {
        updateList = response ;
    }
}
