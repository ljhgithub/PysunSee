package com.pysun.see.entity;

import android.os.Parcel;
import android.os.Parcelable;


public class Media implements Parcelable {
    public final static String ID_CAMERA = "id_camera";
    public final static String DISPLAY_CAMERA = "display_camera";
    public final static String PATH_CAMERA = "";
    private String id;
    private String displayName;
    private String path;
    private long size;

    public Media(String id, String displayName, String path, long size) {
        this.id = id;
        this.displayName = displayName;
        this.path = path;
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isCamera() {
        return ID_CAMERA.equalsIgnoreCase(this.id);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.displayName);
        dest.writeString(this.path);
        dest.writeLong(this.size);
    }

    protected Media(Parcel in) {
        this.id = in.readString();
        this.displayName = in.readString();
        this.path = in.readString();
        this.size = in.readLong();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    @Override
    public String toString() {
        return " id = " + id + " ,displayName = " + displayName + " ,pat = " + path + " ,size = " + size;
    }
}
