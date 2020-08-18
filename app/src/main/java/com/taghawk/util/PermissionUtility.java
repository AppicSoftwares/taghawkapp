package com.taghawk.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amar on 3/16/2016.
 */
public class PermissionUtility {


    public static boolean isPermissionGranted(Activity context, final String[] permission, int permissionReqCode) {

        final List<String> permissionsNeeded = new ArrayList<>();
        for (int i = 0; i < permission.length; i++) {
            final String perm = permission[i];
            if (permission != null && permission[i].startsWith("android.permission")) {
                if (ContextCompat.checkSelfPermission(context,
                        permission[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                            permission[i])) {

                        permissionsNeeded.add(perm);

                    } else {
                        permissionsNeeded.add(perm);

                    }
                }
            }
        }
        if (permissionsNeeded.size() > 0) {
            ActivityCompat.requestPermissions(context, permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    permissionReqCode);

            return false;
        } else {
            return true;
        }
    }

    public static boolean checkPermissionIsGrant(Context context, String permission) {
        //This methode retuns true when permission does not grant.
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


}
