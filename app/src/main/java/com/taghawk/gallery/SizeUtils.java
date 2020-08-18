package com.taghawk.gallery;

import android.graphics.BitmapFactory;
import android.net.Uri;

import com.taghawk.TagHawkApplication;
import com.yalantis.ucrop.util.BitmapLoadUtils;
import com.yalantis.ucrop.util.FileUtils;

public class SizeUtils {

    private Uri fileUri;
    private float height;
    private float width;
    private int exifInformation;

    public SizeUtils(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    private void init() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(FileUtils.getPath(TagHawkApplication.getContext(), fileUri), options);
        exifInformation = BitmapLoadUtils.exifToDegrees(BitmapLoadUtils
                .getExifOrientation(TagHawkApplication.getContext(), fileUri));
        width = options.outWidth;
        height = options.outHeight;
    }

    public float getAspectRatio() {
        float ratio = 1f;
        if (height == 0 || width == 0)
            init();

        switch (exifInformation) {
            case 90:
            case 270:
                ratio = height / width;
                break;
            case 0:
            case 180:
                ratio = width / height;
                break;
        }

        return ratio;
    }
}
