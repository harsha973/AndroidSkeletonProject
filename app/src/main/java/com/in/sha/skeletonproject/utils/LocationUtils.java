package com.in.sha.skeletonproject.utils;

import android.Manifest;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.in.sha.skeletonproject.R;
import com.in.sha.skeletonproject.base.BaseLocationActivity;

import java.util.ArrayList;


/**
 * Created by sreepolavarapu on 13/06/16.
 * Helper for enabling the location on device, Enable permissions for the app to use location and
 * has listeners to listen the changes in location.
 *
 * Order of dialogs
 *  --  Asks to accept "App to use Location API"
 *  --  If user accepts above, Shows a dialog to "Enable device location"
 *
 *  Note : These dialogs only show up when the permissions are not still enabled.
 */
public class LocationUtils implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private BaseLocationActivity mContext;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;

    //  Not yet used in the App.
    private Location mLastKnownLocation;
    private LocationRequest mLocationRequest;
    private static String[] LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    public final static int REQUEST_CHECK_SETTINGS = 111;

    public LocationUtils(BaseLocationActivity context) {
        mContext = context;
        setupGoogleAPIClient();
    }

    /**
     * Connect the Google client API
     */
    public void connect() {
        mGoogleApiClient.connect();
    }

    /**
     * Disconnect Google client API
     */
    public void disconnect(){
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
    }

    /**
     * Configures Google client API.
     */
    private void setupGoogleAPIClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setSmallestDisplacement(10);
        }
    }

    /**
     * Uses Settings API to check the location settings are enabled on the device.
     * If Yes, Registers app for the location changes.
     * If No, Shows a dialog to enable location settings.
     */
    private void checkAndEnableLocationSettingsOnDevice()
    {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
//                final LocationSettingsStates states = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    mContext,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            //  Request location permissions if not enabled.
            requestLocationPermissions();

            //  Update last known location.
            updateLastKnownLocation();
        } catch (SecurityException ex) {
            //  We are handling permissions, so we donot encounter this exception
            //  But, as compiler is complaining, added try catch.
            ex.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressWarnings({"MissingPermission"})
    public void updateLastKnownLocation() {
        //  Check for permissions
        if (PermissionUtils.isPersmissionGranted(mContext, LOCATION_PERMISSIONS)) {

            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }


    /**
     * @return Current location of the device. Null, in case its not available.
     */
    public Location getCurrentLocation()
    {
        return mCurrentLocation;
    }

    /**
     * Unregister the updates for location changes.
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Register for Location updates.
     */
    @SuppressWarnings({"MissingPermission"})
    public void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    //  --- TED Permissions
    /**
     * Helper to request location permissions
     */
    private void requestLocationPermissions() {

        new TedPermission(mContext)
                .setPermissionListener(mLocationPermissionListener)
                .setPermissions(LOCATION_PERMISSIONS)
                .setDeniedMessage(R.string.permission_denied_message_location)
                .check();
    }

    /**
     * Permission listener
     */
    private PermissionListener mLocationPermissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            //  Update last known location.
            updateLastKnownLocation();

            // Enable location settings if not enabled.
            checkAndEnableLocationSettingsOnDevice();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> arrayList) {

        }
    };

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }
}
