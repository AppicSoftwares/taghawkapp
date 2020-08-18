package com.dnitinverma.amazons3library.model;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;

/**
 * Created by appinventiv on 7/9/17.
 */

public class ImageBean {
    public static final String IMAGE_UPLOAD_FAILED="failed";
    public static final String IMAGE_UPLOAD_LOADING="loading";

    int type;
    int position;
    boolean isMultiple;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isMultiple() {
        return isMultiple;
    }

    public void setMultiple(boolean multiple) {
        isMultiple = multiple;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public TransferObserver getmObserver() {
        return mObserver;
    }

    public void setmObserver(TransferObserver mObserver) {
        this.mObserver = mObserver;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getIsSucess() {
        return isSucess;
    }

    public void setIsSucess(String isSucess) {
        this.isSucess = isSucess;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    private String name;
    private int progress = 0;
    private TransferObserver mObserver;
    private String serverUrl = "";
    private String isSucess = "0";
    private String imagePath;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

}
