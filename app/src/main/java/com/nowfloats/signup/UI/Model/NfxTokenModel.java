package com.nowfloats.signup.UI.Model;

import java.io.Serializable;

/**
 * Created by NowFloats on 01-08-2016.
 */
public class NfxTokenModel implements Serializable {
    public int Status;
    public int Type;
    public String UserAccessTokenKey;
    public String UserAccessTokenSecret;
    public String UserAccountId;
    public String UserAccountName;
}
