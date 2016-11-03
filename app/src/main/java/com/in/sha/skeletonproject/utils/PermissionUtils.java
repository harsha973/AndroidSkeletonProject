package com.in.sha.skeletonproject.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by sreepolavarapu on 13/06/16.
 */
public class PermissionUtils {

    public static boolean isPersmissionGranted(Context context, String[] permissions)
    {
        for(String permission : permissions)
        {
            if(ActivityCompat.checkSelfPermission(context, permission) !=
                    PackageManager.PERMISSION_GRANTED)
            {
                return false;
            }
        }

        return true;
    }
}
