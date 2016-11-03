package com.in.sha.skeletonproject.utils;;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by sreepolavarapu on 4/03/16.
 * All the left over util methods just dump them here.
 *
 * Sorry for naming convention. Rename if u don't like it :)
 */
public class GeneralUtils {

    public static final String UUID_PATTERN = "[a-fA-F0-9]{8}[a-fA-F0-9]{4}[a-fA-F0-9]{4}[a-fA-F0-9]{4}[a-fA-F0-9]{12}";

    /**
     *	Helper to check if activity is active.
     * @param activity	The context to check
     * @return	true if activity is active, else false.
     */
    public static boolean isContextActive(Activity activity){
        if(activity != null && !activity.isFinishing()){
            return true;
        }
        return false;
    }

    /**
     *	Helper to check if activity is active.
     * @param context	The context to check
     * @return	true if activity is active, else false.
     */
    public static boolean isContextActive(Context context){

        if(context != null)
        {
            if(context instanceof Activity)
            {
                return isContextActive((Activity)context);
            }

            return true;
        }

        return false;
    }

    /**
     * Helper to generate Random UUID without dashes
     * @return  32 digit UUID without dashes
     */
    public static String generateUUIDWithoutDashes()
    {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * Extracts the uuid from the name of file. If name of file does not match UUID pattern,
     * creates a new UUID.
     * @param mediaFilePath The path of the media file.
     * @return  generated/extracted UUID
     */
    public static String getUUIDFromMediaFilePath(String mediaFilePath)
    {
        String uuid = mediaFilePath.substring(mediaFilePath.lastIndexOf("/")+1);
        if(uuid.contains("."))
        {
            uuid = uuid.substring(0, uuid.indexOf("."));
        }

        //  Replace dashes with empty character to keep in sync with server format.
        uuid = uuid.replaceAll("-","");
        if(!Pattern.matches(UUID_PATTERN, uuid))
        {
            uuid = String.valueOf(GeneralUtils.generateUUIDWithoutDashes());
        }

        return uuid;
    }

    /**
     * Method to check if telephony manger is supported by device.
     * @param context
     * @return	true if supported, else false.
     */
    public static boolean isTelephonyEnabled(Context context){

        TelephonyManager tm = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
        if(tm != null)
        {
            if(tm.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM ||
                    context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY))
            {
                return tm.getSimState()== TelephonyManager.SIM_STATE_READY;
            }
        }

        return false;
    }

    public static boolean isLoggedIn(Context context)
    {
        String guiID = PreferencesUtils.getString(context, PreferencesUtils.KEY_USER_GUID);

        return guiID != null;
    }

    /**
     * Method to check if the there is a connection to internet. But I still doubt this method works
     * when connected to network(Wifi, data) and not connected to internet.
     * @param context   Context
     * @return  True if is connected, else false.
     */
    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }

    /**
     * Check if list is empty
     * @param list
     * @return  true if its empty
     */
    public static boolean isListEmpty(List list)
    {
        return list == null ||
                list.size() == 0;
    }
}
