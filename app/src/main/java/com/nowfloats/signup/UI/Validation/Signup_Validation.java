package com.nowfloats.signup.UI.Validation;


public class Signup_Validation {

    //Email Validation
    // Phone Validation


    public static boolean isValidEmail(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() > 6 && phoneNumber.length() <= 12) {
            return true;
        } else
            return false;
    }


}
