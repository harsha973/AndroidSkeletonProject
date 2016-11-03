package com.in.sha.skeletonproject.retrofit;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.in.sha.skeletonproject.retrofit.interceptor.RefreshTokenHttpInterceptor;
import com.in.sha.skeletonproject.retrofit.strategies.DeSerailisationExclusionStrategy;
import com.in.sha.skeletonproject.retrofit.strategies.AppExclusionStrategy;
import com.in.sha.skeletonproject.retrofit.strategies.AppSerializationExclusionStrategy;
import com.in.sha.skeletonproject.utils.FlavourUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sreepolavarapu on 4/03/16.
 */
public class RetroFitServiceFactory {

    private static RetrofitRestService sRestService;
    private static RetrofitRestService sRestServiceNoHttpnterceptor;
    private static Retrofit retrofit;
    private static final int TIME_OUT = 60;

    /**
     *
     * @return  {@link RetrofitRestService}, Creates one if it doesnot exist. Attached Httpinteceptor
     */
    public static RetrofitRestService getRestService()
    {
        return getRestService(true);
    }

    /**
     * Method to return instnace of rest service
     * @param isHttpInterceptorEnabled  Disables HttpInterceptor when the boolean is false.
     *                                  Useful in login scenarios
     * @return  Hazco rest service instance.
     */
    public static RetrofitRestService getRestService(boolean isHttpInterceptorEnabled)
    {
        //  If enabled, return sRestService Instance.
        if(isHttpInterceptorEnabled)
        {
            if(sRestService == null)
            {
                sRestService = getsRestServiceNewInstance(true);
            }
            return sRestService;
        }
        //  return sRestServiceNoHttpnterceptor Instance.
        else
        {
            if(sRestServiceNoHttpnterceptor == null)
            {
                sRestServiceNoHttpnterceptor = getsRestServiceNewInstance(false);
            }
            return sRestServiceNoHttpnterceptor;
        }
    }

    /**
     * Create a new instance of the rest service. Attached interceptor based on the value
     * @param isHttpInterceptorEnabled
     * @return  Instance of rest service.
     */
    private static RetrofitRestService getsRestServiceNewInstance(boolean isHttpInterceptorEnabled)
    {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
        // add your other interceptors …

        // add logging as last interceptor
        httpClientBuilder.addInterceptor(logging);
        httpClientBuilder.addNetworkInterceptor(new StethoInterceptor());

        //  Add interceptor if enabled
        if(isHttpInterceptorEnabled)
        {
            httpClientBuilder.addInterceptor(new RefreshTokenHttpInterceptor());
        }

        //  Customising GSON to exclude some properties.
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new AppExclusionStrategy())
                .excludeFieldsWithModifiers(Modifier.STATIC)
                .addSerializationExclusionStrategy(new AppSerializationExclusionStrategy())
                .addDeserializationExclusionStrategy(new DeSerailisationExclusionStrategy())
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(FlavourUtils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                    .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .client(httpClientBuilder.build())
                .build();

        return retrofit.create(RetrofitRestService.class);

    }
    /**
     *
     * @return  {@link RetrofitRestService}, Creates one if it doesnot exist.
     */
    public static Retrofit getRetrofit()
    {
        if(retrofit == null)
        {
            getRestService();

        }

        return retrofit;
    }

    /**
     * Any field which adds this annotation is skipped by Gson.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Ignore {
        // some implementation here
    }

    /**
     * Any field which adds this annotation is skipped by Gson.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface SerializationIgnore {
        // some implementation here
    }

    /**
     * Any field which adds this annotation is skipped by Gson.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface DeserializationIgnore {
        // some implementation here
    }

    /**
     * Clears all the unnecessary variables.
     * Use this on low memory situations.
     */
    public static  void onTrimMemory()
    {
        sRestService = null;
    }

}
