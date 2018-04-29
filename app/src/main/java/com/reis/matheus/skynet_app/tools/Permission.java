package com.reis.matheus.skynet_app.tools;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matheus on 4/28/18.
 */

public class Permission {
    private static String[] necessaryPermissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO
    };

    public static boolean verifyPermissions(int requestCode, Activity activity) {

        List<String> permissionsList = new ArrayList<String>();

        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : necessaryPermissions) {

                boolean validPermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
                if (!validPermission) permissionsList.add(permission);
            }

            if(permissionsList.isEmpty()) return true;

            String[] newPermissions = new String[permissionsList.size()];
            permissionsList.toArray(newPermissions);

            ActivityCompat.requestPermissions(activity, newPermissions, requestCode);
        }

        return true;
    }
}
