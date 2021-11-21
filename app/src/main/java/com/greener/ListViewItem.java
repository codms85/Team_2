package com.greener;

import android.net.Uri;

public class ListViewItem {
    private String imageUri;
    private String nameStr ;
    private String addressStr ;
    private String callStr;

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getNameStr() {
        return nameStr;
    }

    public void setNameStr(String nameStr) {
        this.nameStr = nameStr;
    }

    public String getAddressStr() {
        return addressStr;
    }

    public void setAddressStr(String addressStr) {
        this.addressStr = addressStr;
    }

    public String getCallStr() {
        return callStr;
    }

    public void setCallStr(String callStr) {
        this.callStr = callStr;
    }

    public ListViewItem() {
    }
}