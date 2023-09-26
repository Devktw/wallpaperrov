package com.mstudio.android.mstory.manuel.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Tab {
    String id;
    String title;
    String type;

    public Tab(String id, String title, String type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }
    public Tab() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
