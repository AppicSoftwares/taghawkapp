package com.taghawk.model.tag;

import java.io.Serializable;

/**
 * Created by appinveniv on 10/4/18.
 */

public class ClusterBean implements Serializable {

    private String mTagName;
    private String mTagTotalMember;
    private String mTagId;
    private int mTagType;
    private String mTagImage;
    private boolean isMember;
    private String mFounder;

    public String getmFounder() {
        return mFounder;
    }

    public void setmFounder(String mFounder) {
        this.mFounder = mFounder;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    public int getmTagType() {
        return mTagType;
    }

    public void setmTagType(int mTagType) {
        this.mTagType = mTagType;
    }

    public String getmTagImage() {
        return mTagImage;
    }

    public void setmTagImage(String mTagImage) {
        this.mTagImage = mTagImage;
    }

    public String getmTagName() {
        return mTagName;
    }

    public void setmTagName(String mTagName) {
        this.mTagName = mTagName;
    }

    public String getmTagTotalMember() {
        return mTagTotalMember;
    }

    public void setmTagTotalMember(String mTagTotalMember) {
        this.mTagTotalMember = mTagTotalMember;
    }

    public String getmTagId() {
        return mTagId;
    }

    public void setmTagId(String mTagId) {
        this.mTagId = mTagId;
    }
}
