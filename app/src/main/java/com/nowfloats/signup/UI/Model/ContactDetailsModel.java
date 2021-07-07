package com.nowfloats.signup.UI.Model;

import java.io.Serializable;

/**
 * Created by NowFloatsDev on 25/05/2015.
 */
public class ContactDetailsModel implements Serializable {

    public String ContactName;
    public String ContactNumber;

    public ContactDetailsModel(String ContactNumber, String ContactName) {
        this.ContactNumber = ContactNumber;
        this.ContactName = ContactName;
    }
}
