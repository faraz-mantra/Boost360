package com.nowfloats.Store.Model;

import java.util.ArrayList;

/**
 * Created by guru on 03/12/2015.
 */
public class MailModel {
    public String ClientId;
    public String Message;
    public String Subject;
    public ArrayList<String> EmailIds;

    public MailModel(String id, String msg, String subj, ArrayList<String> EmailIds) {
        this.ClientId = id;
        this.Message = msg;
        this.Subject = subj;
        this.EmailIds = EmailIds;
    }
}
