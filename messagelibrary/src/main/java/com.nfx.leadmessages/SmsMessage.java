package com.nfx.leadmessages;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NowFloats on 16-02-2017.
 */

public class SmsMessage implements Parcelable {
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
    public String body, subject, seen;
    public long date;

    public SmsMessage() {

    }

    protected SmsMessage(Parcel in) {
        body = in.readString();
        subject = in.readString();
        seen = in.readString();
        date = in.readLong();
    }

    public String getBody() {
        return body;
    }

    public SmsMessage setBody(String body) {
        this.body = body;
        return this;
    }

    public long getDate() {
        return date;
    }

    public SmsMessage setDate(long date) {
        this.date = date;
        return this;
    }

    public String getSeen() {
        return seen;
    }

    public SmsMessage setSeen(String seen) {
        this.seen = seen;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public SmsMessage setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    @Override
    public String toString() {
        return date + ", " + seen + ", " + subject + ", " + body;
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
