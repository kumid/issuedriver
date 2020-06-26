package com.ru.test.issuedriver.helpers;

import android.app.Activity;
import android.content.pm.PackageManager;

import com.ru.test.issuedriver.ui.registration.RegistrationActivity;

import androidx.core.app.ActivityCompat;

public class permissionsHelper {

    public static final int RC_STORAGE_PERMS = 101, RC_STORAGE_PERMS_READ = 102;

    public static void requestPermissionRW_EXTERNAL_STORAGE(Activity activity) {
        int hasWriteExtStorePMS0 = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteExtStorePMS0 != PackageManager.PERMISSION_GRANTED) {

            Runnable run = new Runnable() {
                @Override
                public void run() {
                    ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_STORAGE_PERMS);
                }
            };
            Thread thread = new Thread(run);
            thread.start();

            return;
        }
        int hasWriteExtStorePMS = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExtStorePMS != PackageManager.PERMISSION_GRANTED) {

            Runnable run = new Runnable() {
                @Override
                public void run() {
                    ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_STORAGE_PERMS);                        }
            };
            Thread thread = new Thread(run);
            thread.start();
            return;
        }
    }



}
