package com.nowfloats.Business_Enquiries.Model;

import java.util.ArrayList;

/**
 * Created by guru on 06-07-2015.
 */
public class BzQueryEvent {
    public ArrayList<Business_Enquiry_Model> StorebizQueries = new ArrayList<Business_Enquiry_Model>();
    public ArrayList<Entity_model> StorebizEnterpriseQueries = new ArrayList<Entity_model>();

    public BzQueryEvent(ArrayList<Business_Enquiry_Model> StorebizQueries,
                        ArrayList<Entity_model> StorebizEnterpriseQueries) {
        this.StorebizQueries = StorebizQueries;
        this.StorebizEnterpriseQueries = StorebizEnterpriseQueries;
    }
}