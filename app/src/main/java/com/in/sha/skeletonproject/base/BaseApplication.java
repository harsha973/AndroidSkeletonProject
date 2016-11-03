package com.in.sha.skeletonproject.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.in.sha.skeletonproject.BuildConfig;
import com.in.sha.skeletonproject.R;
import com.in.sha.skeletonproject.retrofit.RetroFitServiceFactory;
import com.in.sha.skeletonproject.utils.AnalyticsTrackers;
import com.in.sha.skeletonproject.utils.PreferencesUtils;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


/**
 * Created by sreepolavarapu on 3/03/16.
 */
public class BaseApplication extends Application {

    private static final String TAG = BaseApplication.class.getSimpleName();

    private static BaseApplication sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sAppContext = this;

        //  Google Analytics stuff
//        AnalyticsTrackers.initialize(this);

        //  Fabric initialisation - Disable when debug or Localhost
        boolean disableCrashLytics = !BuildConfig.USE_CRASHLYTICS || BuildConfig.DEBUG_MODE;
        if(!disableCrashLytics)
        {
            Fabric.with(this, new Crashlytics());
        }

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath(BuildConfig.DEFAULT_FONT).setFontAttrId(R.attr.fontPath).build());
    }

    /**
     * Helper to get the App context.
     * @return  The App context.
     *
     * <p>NOTE : </p> Do not use this method which may cause null pointers.
     * Safe to use {@link BaseApplication#getAppContext(Context)}
     */
    public static BaseApplication getAppContext()
    {
        return sAppContext;
    }

    /**
     * Helper to get the App context.
     * @return  The App context.
     */
    public static BaseApplication getAppContext(Context context)
    {
        if (sAppContext == null)
        {
            sAppContext = (BaseApplication)context.getApplicationContext();
        }

        return sAppContext;
    }

//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d(TAG, "On trim memory level : " + level);

//        Glide.with(this).onTrimMemory(level);
        if(level >= TRIM_MEMORY_COMPLETE)
        {
            //  Try to free some memory from application.
            PreferencesUtils.onTrimMemory();
            RetroFitServiceFactory.onTrimMemory();
            Glide.get(getApplicationContext()).trimMemory(level);
        }

    }
}
