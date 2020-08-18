package com.taghawk.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.taghawk.R;
import com.taghawk.constants.AppConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by admin1 on 6/1/17.
 * keeps all file related util methods
 */

public class FileUtils {
    private static final String PREFIX = "goodlife";
    private static final String VIDEO_EXT = "Videos";
    private static final String IMAGE_EXT = "Images";
    private static FileUtils sFileUtils;

    public static FileUtils getInstance() {
        if (sFileUtils == null)
            sFileUtils = new FileUtils();
        return sFileUtils;
    }

    public static File getNextVideoFile() {
        File videoDir = getVideoDir();
        String fileExt = ".mp4";
        String fileName = String.valueOf(System.currentTimeMillis());
        return new File(videoDir, fileName + fileExt);
    }

    public static String getImageDir() {
//        File filesDir = context.getFilesDir();
        File filesDir = new File(AppConstants.APP_IMAGE_FOLDER);
        if (!filesDir.exists()) {
            filesDir.mkdirs();
        }
//        File imageDir = new File(filesDir, IMAGE_EXT);
//        if (!imageDir.exists()) {
//            imageDir.mkdirs();
//        }
        return (filesDir.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
    }

    public static File getNextImageFile() {
        return new File(getImageDir());
    }

    public static File getVideoDir() {
        File filesDir = new File(AppConstants.APP_IMAGE_FOLDER);
        File audioDir = new File(filesDir, VIDEO_EXT);
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }
        return audioDir;
    }

    public static File saveImageToGallery(Bitmap bmp) {
        if (bmp == null) {
            throw new IllegalArgumentException("bmp should not be null");
        }
        File file = getNextImageFile();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * method to create PDF form byte array of a pdf file
     */
    public static String createImageFileFromByteArray(byte[] bytes, String fileName) {
        try {
            String path = getImageDir();
            OutputStream out = new FileOutputStream(path);
            out.write(bytes);
            out.close();
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private File initializeDir(Context context) {
        // Find the dir to save cached images
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            file = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name));
        else
            file = context.getCacheDir();
        if (!file.exists())
            file.mkdirs();
        return file;
    }

    private File initializeTempDir(Context context) {
        // Find the dir to save cached images
        File file;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
//            file = new File(Environment.getExternalStorageDirectory(), "." + context.getString(R.string.app_name));
//        else
        file = context.getCacheDir();
        if (!file.exists())
            file.mkdirs();
        return file;
    }

    public File getCameraTempFile(Context context, boolean deleteOldFile) {
        File file = getFile(context, "pickImageResult" + System.currentTimeMillis() + ".jpeg");
        if (deleteOldFile && file.exists())
            file.delete();
        return file;
    }

    public File getFile(Context context, String name) {
        return new File(initializeDir(context), name);
    }

    public File getDir(Context context, String name) {
        return new File(initializeDir(context), name);
    }

    public File getTempFile(Context context, String name) {
        return new File(initializeTempDir(context), name);
    }

    public File saveBitmapAsFile(Context context, Bitmap bitmap) {
        File imageFile = getFile(context, "filtered_image" + System.currentTimeMillis() + ".png");
        saveBitmapAsFile(bitmap, imageFile, 100, Bitmap.CompressFormat.PNG);
        return imageFile;
    }

    public File saveBitmapAsTempFile(Context context, Bitmap bitmap) {
        File imageFile = getTempFile(context, "temp" + System.currentTimeMillis() + ".jpeg");
        saveBitmapAsFile(bitmap, imageFile, 100, Bitmap.CompressFormat.JPEG);
        return imageFile;
    }

    public void saveBitmapAsFile(Bitmap bitmap, File file, int quality, Bitmap.CompressFormat format) {
        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(format, quality, bos);
        byte[] bitmapdata = bos.toByteArray();

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getCacheFile(Context context, String name) {
        return new File(context.getCacheDir(), name);
    }

    public boolean deleteContents(File dir) {
        File[] files = dir.listFiles();
        boolean success = true;
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    success &= deleteContents(file);
                }
                if (!file.delete()) {
                    success = false;
                }
            }
        }
        return success;
    }

    public File getGameDir(Context context) {
        return getDir(context, "game");
    }

}
