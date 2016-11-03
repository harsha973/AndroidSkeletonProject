package com.in.sha.skeletonproject.base;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.in.sha.skeletonproject.utils.LocationUtils;

/**
 * Created by sreepolavarapu on 13/06/16.
 */
public class BaseLocationActivity extends BaseActivity {

    private LocationUtils mLocationUtils;
    protected boolean shouldEnableLocationServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationUtils = new LocationUtils(this);
    }

    @Override
    protected void onStart() {

        if(shouldEnableLocationServices)
        {
            mLocationUtils.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(shouldEnableLocationServices)
        {
            mLocationUtils.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        switch (requestCode) {
            //  Device Location settings
            case LocationUtils.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        mLocationUtils.startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to

                        break;
                    default:
                        break;
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, intent);
        }
    }


    /**
     * Helper to return the current location
     * @return  Current location, if location is not enabled returns null.
     */
    public @Nullable
    Location getCurrentLocation()
    {
        return mLocationUtils.getCurrentLocation();
    }
}
