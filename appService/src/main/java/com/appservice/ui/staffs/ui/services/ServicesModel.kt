package com.newfloats.staffs.ui.services;

import java.util.ArrayList;

public class ServicesModel {
    public static ArrayList<String> getAllServices() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Hair Removal and Waxing");
        list.add("Hair Color for all ages");
        list.add("Facial Makover");
        list.add("Bridal Makover");
        list.add("Anti-aging therapy");
        list.add("Anti-pimple");
        list.add("Skin Toning");
        return list;
    }
}
