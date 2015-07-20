package com.nowfloats.signup.UI.Validation;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Signup_Validation {

    //Email Validation
    // Phone Validation


    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber)
    {
        if(phoneNumber.length() > 6 && phoneNumber.length() <= 12)
        {
            return true ;
        }
        else
            return false ;
    }


}
