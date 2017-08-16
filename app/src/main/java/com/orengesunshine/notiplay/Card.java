package com.orengesunshine.notiplay;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Time;

/**
 * Created by hayatomoritani on 8/2/17.
 */

public class Card implements Parcelable{

    private String folderName;
    private String front;
    private String back;
    private Time createdAt;
    private boolean check;

    public Card(String folderName, String front, String back, Time createdAt, boolean check) {
        this.folderName = folderName;
        this.front = front;
        this.back = back;
        this.createdAt = createdAt;
        this.check = check;
    }

    protected Card(Parcel in) {
        folderName = in.readString();
        front = in.readString();
        back = in.readString();
        check = in.readByte() != 0;
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public Time getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Time createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(folderName);
        dest.writeString(front);
        dest.writeString(back);
        dest.writeByte((byte) (check ? 1 : 0));
    }
}
