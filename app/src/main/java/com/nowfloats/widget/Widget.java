package com.nowfloats.widget;

import com.nowfloats.Store.Model.ActivePackage;
import com.nowfloats.Store.Model.WidgetPacks;

import java.util.List;

public class Widget {

    private static Widget singleton = new Widget();

    public List<ActivePackage> activePackages;
    private ActivePackage activePackage;

    private Widget() {

    }

    public static Widget getInstance() {
        return singleton;
    }

    /*public List<ActivePackage> getActivePackages()
    {
        return activePackages;
    }

    public void setActivePackages(List<ActivePackage> activePackages)
    {
        this.activePackages = activePackages;
    }*/

    public ActivePackage getActivePackage() {
        return activePackage;
    }

    public void setActivePackage(ActivePackage activePackage) {
        this.activePackage = activePackage;
    }


    /*public void getValue()
    {
        for(WidgetPacks widgetPacks: getActivePackage().getWidgetPacks())
        {
            if(widgetPacks.Properties != null)
            {

            }
        }

    }*/
}