package com.ceylonapz.myfile.utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class AppPermission {

    public boolean canAccessWriteStorage(Context context) {
        return (hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    public boolean canAccessReadStorage(Context context) {
        return (hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE));
    }

    public boolean hasPermission(Context context, String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, perm));
    }
}
