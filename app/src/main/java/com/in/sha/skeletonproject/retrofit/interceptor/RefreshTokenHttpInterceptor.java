package com.in.sha.skeletonproject.retrofit.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sreepolavarapu on 5/04/16.
 *
 * Interceptor to Add AuthHeader.
 *
 * Also checks the response code for 401(UnAuthorized) and calls login service silently for new AuthToken.
 * Once this is successful, full fills the previous request
 *
 * @Note - Donot use this interceptor which do not need Authtoken
 */
public class RefreshTokenHttpInterceptor implements Interceptor {

    private static final String TAG = RefreshTokenHttpInterceptor.class.getSimpleName();
    private static final int RESPONSE_CODE_UNAUTHORISED = 401;
    private static final int RESPONSE_CODE_FORBIDDEN = 403;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        return chain.proceed(original);

//        UserModel userModel = PreferencesUtils.getObject(BaseApplication.getAppContext(),
//                PreferencesUtils.KEY_USER_MODEL,
//                UserModel.class);
//
//        //  Not sure how to handle this case. Normally User should not be null
//        if(userModel == null)
//        {
//            return chain.proceed(original);
//        }
//
//        // Customize the request
//        Request request = getRequestWithAuthHeader(original, userModel.getAuthToken());
//        Response response = chain.proceed(request);
//
//        //  UnAuthorized or forbidden
//        if((response.code() == RESPONSE_CODE_UNAUTHORISED ||
//            response.code() == RESPONSE_CODE_FORBIDDEN))
//        {
//            Log.w(TAG, "Status code 401 : UNAuthorised - Retrying ");
//
//            //  Get the latest user model.
//            userModel = getUserModelFromServer();
//
//            if(userModel != null)
//            {
//                Log.w(TAG, "Got New Authtoken - Retrying prev request");
//                //  Save updated AuthToken
//                saveUserModelToPrefs(userModel);
//                request = getRequestWithAuthHeader(original, userModel.getAuthToken());
//                response = chain.proceed(request);
//            }
//        }

        // Customize or return the response
//        return response;
    }

//    /**
//     * Prepares the Request with the Auth header.
//     * @param original  The original request
//     * @param authToken Auth token to add
//     * @return  The newly constructed request.
//     */
//    private Request getRequestWithAuthHeader(Request original, String authToken)
//    {
//        return original.newBuilder()
//                .header("Accept", "application/json")
//                .header(Constants.HEADER_AUTHORIZATION, Constants.BEARER + authToken)
//                .method(original.method(), original.body())
//                .build();
//    }
//
//    /**
//     * Connects to the server and returns the user model. Synchronous network call.
//     * @return  UserModel if successful, else null.
//     */
//    private @Nullable
//    UserModel getUserModelFromServer()
//    {
//        UserModel userModel = null;
//        //  Retry 3 times
//        int retryCount = 0;
//        do{
//            //  Re-login for new Auth token
//            try {
//
//                UserRequestModel reqModel = getUserRequestModel();
//                if(reqModel != null) {
//
//                    Observable<retrofit2.Response<UserModel>> userModelCall = RetroFitServiceFactory.getRestService(false).userLoginRaw(reqModel);
//                    //  This is not proper implementation of observable.
//                    //  But couldn't find a better way to do synchronous call. This should not be called from
//                    //  main thread.
//
//                    retrofit2.Response<UserModel> userModelResponse = userModelCall.toBlocking().first();
//
//                    //  Failure case, Send null back and signout user
//                    if(userModelResponse.code() == RESPONSE_CODE_UNAUTHORISED)
//                    {
//                        signOutUserOnAuthFailure();
//                        return null;
//                    }
//
//                    userModel = userModelResponse.body();
//                }
//
//            }catch (Exception ex) {
//
//                Log.e(TAG, "Refreshing access token failed");
//
//                RetrofitException retrofitException = ErrorHandler.asRetrofitException(ex);
//                //  Failure case, Send null back and sign out user
//                if( retrofitException.getResponse() != null &&
//                        retrofitException.getResponse().code() == RESPONSE_CODE_UNAUTHORISED)
//                {
//                    signOutUserOnAuthFailure();
//                    return null;
//                }
//            }
//            retryCount++;
//        } while (userModel == null && retryCount < 3);
//
//        return userModel;
//    }
//
//    /**
//     * GEt the request model for the Login Call.
//     * @return  The constructed {@link UserRequestModel} from the prefs, null in failure
//     */
//    private UserRequestModel getUserRequestModel()
//    {
//        UserRequestModel requestModel = new UserRequestModel();
//
//        String username = PreferencesUtils.getString(BaseApplication.getAppContext(),
//                PreferencesUtils.KEY_USER_NAME);
//        String password = PreferencesUtils.getString(BaseApplication.getAppContext(),
//                PreferencesUtils.KEY_USER_PASSWORD);
//
//        requestModel.setEmail(username);
//        requestModel.setPassword(password);
//
//        return requestModel;
//
//    }
//
//    /**
//     * Check the context and Sing out the user while displaying a toast
//     */
//    private void signOutUserOnAuthFailure()
//    {
//        if(GeneralUtils.isContextActive(BaseApplication.getAppContext()))
//        {
//            //  Toast to display reg session expiry
//            LocalBroadcastManager
//                    .getInstance(BaseApplication.getAppContext())
//                    .sendBroadcast(new Intent(HomeActivity.ACTION_SIGN_OUT_TOAST));
//
//            //  Sign out broadcast which finishes all opened activities and opens StartupActivity
//            AppUtils.singOut(BaseApplication.getAppContext());
//        }
//    }
//    /**
//     * Handy method to Save the UserModel object to prefs. Only use this on AuthKey changes.
//     * @param userModel Usermodel to save to prefs
//     */
//    private void saveUserModelToPrefs(UserModel userModel)
//    {
//        if(BaseApplication.getAppContext() != null)
//        {
//            PreferencesUtils.saveObject(BaseApplication.getAppContext(), PreferencesUtils.KEY_USER_MODEL, userModel);
//        }
//    }
}
