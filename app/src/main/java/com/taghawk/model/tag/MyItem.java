package com.taghawk.model.tag;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;

public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private Boolean isMember;
    private String imageUrl;
    private String tagName;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getMember() {
        return isMember;
    }

    public void setMember(Boolean member) {
        isMember = member;
    }

    private ArrayList<String> productId;

    public MyItem() {
        mPosition = null;
    }

    public MyItem(double lat, double lng, String eventIDS, boolean isMember, String imageUrl, String tagName) {
        mPosition = new LatLng(lat, lng);
        productId = new ArrayList<>();
        this.isMember = isMember;
        this.imageUrl = imageUrl;
        this.tagName = tagName;
        productId.add(eventIDS);
    }

    public MyItem(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getEventId(int i) {
        return productId.get(i);
    }


    public String getSnippet() {
        return mSnippet;
    }
}
