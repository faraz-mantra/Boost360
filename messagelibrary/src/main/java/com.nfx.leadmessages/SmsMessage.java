package com.nfx.leadmessages;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NowFloats on 16-02-2017.
 */

public class SmsMessage implements Parcelable{
    public SmsMessage(){

    }

    public String body,subject,seen;
    public long date;

    protected SmsMessage(Parcel in) {
        body = in.readString();
        subject = in.readString();
        seen = in.readString();
        date = in.readLong();
    }

    public static final Creator<SmsMessage> CREATOR = new Creator<SmsMessage>() {
        @Override
        public SmsMessage createFromParcel(Parcel in) {
            return new SmsMessage(in);
        }

        @Override
        public SmsMessage[] newArray(int size) {
            return new SmsMessage[size];
        }
    };

    public SmsMessage setBody(String body){
        this.body=body;
        return this;
    }
    public SmsMessage setDate(long date){
        this.date=date;
        return this;
    }
    public SmsMessage setSubject(String subject){
        this.subject=subject;
        return this;
    }
    public SmsMessage setSeen(String seen){
        this.seen=seen;
        return this;
    }
    public String getBody(){
        return body;
    }

    public long getDate() {
        return date;
    }

    public String getSeen() {
        return seen;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return date+", "+seen+", "+subject+", "+body;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(body);
        dest.writeString(subject);
        dest.writeString(seen);
        dest.writeLong(date);
    }
}
