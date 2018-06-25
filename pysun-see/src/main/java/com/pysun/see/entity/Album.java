package com.pysun.see.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Album implements Parcelable {

    public final static String ALL_ID = "all_id";
    public final static String ALL_NAME = "ALL";
    private String id;
    private String displayName;
    private long size;
    private String coverPath;
    private int count;


    public Album(String id, String displayName, long size, int count, String coverPath) {
        this.id = id;
        this.displayName = displayName;
        this.size = size;
        this.count = count;
        this.coverPath = coverPath;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getCount() {
        return count;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public boolean isAll() {
        return ALL_ID.equalsIgnoreCase(this.id);
    }

    public long getSize() {
        return size;
    }

    @Override
    public String toString() {
        return " id = " + id + " ,displayName = " + displayName + " ,count = " + count + " ,cover = " + coverPath + " ,size = " + size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.displayName);
        dest.writeLong(this.size);
        dest.writeString(this.coverPath);
        dest.writeInt(this.count);
    }

    protected Album(Parcel in) {
        this.id = in.readString();
        this.displayName = in.readString();
        this.size = in.readLong();
        this.coverPath = in.readString();
        this.count = in.readInt();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
