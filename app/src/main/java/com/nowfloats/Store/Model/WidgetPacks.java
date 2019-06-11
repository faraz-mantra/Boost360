package com.nowfloats.Store.Model;

import java.util.List;

/**
 * Created by guru on 29-04-2015.
 */
public class WidgetPacks {
    public String CreatedOn;
    public String Desc;
    public String ExpiryDate;
    public String ExpiryInMths;
    public String Name;
    public List<Property> Properties;
    public String WidgetKey;
    public String Group;
    public int Priority;


    public static class Property
    {
        public String Key, Value;
    }
}
