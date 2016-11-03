package com.in.sha.skeletonproject.retrofit.error;

import android.content.Context;
import android.text.TextUtils;

import com.in.sha.skeletonproject.R;
import com.in.sha.skeletonproject.base.BaseActivity;
import com.in.sha.skeletonproject.base.BaseFragment;
import com.in.sha.skeletonproject.models.ErrorResponseModel;
import com.in.sha.skeletonproject.retrofit.RetroFitServiceFactory;
import com.in.sha.skeletonproject.utils.GeneralUtils;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by sreepolavarapu on 25/07/16.
 *
 * Helper to handle errors.
 */
public class ErrorHandler {

    /**
     * Shows error dialog attached to base fragment. Constructs error message base on the throwable
     * parameter.
     * @param baseFragment  Base fragment's instance.
     * @param throwable Exception thrown when connecting to server.
     * @param dialogTag Dialog tad, used to handle callbacks. Refer BaseFragment's dialog handling.
     */
    public static void showErrorDialog(BaseFragment baseFragment,
                                       Throwable throwable,
                                       String dialogTag)
    {
        BaseActivity context = (BaseActivity)baseFragment.getActivity();

        RetrofitException retrofitException = asRetrofitException(throwable);
        ErrorResponseModel errorResponseModel = null;
        try {
            errorResponseModel = retrofitException.getErrorBodyAs(ErrorResponseModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //  Initialise default error response, if it is not yet.
            if(errorResponseModel == null ||
                    TextUtils.isEmpty(errorResponseModel.getMessage()))
            {
                errorResponseModel = getGenericErrorModel(context);
            }
        }

        baseFragment.showErrorDialog(errorResponseModel, dialogTag);
    }

    /**
     * Helper to convert Throwable into RetrofitException.
     */
    public static RetrofitException asRetrofitException(Throwable throwable) {

        // We had non-200 http error
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            Response response = httpException.response();
            return RetrofitException.httpError(response.raw().request().url().toString(), response, RetroFitServiceFactory.getRetrofit());
        }
        // A network error happened
        if (throwable instanceof IOException) {
            return RetrofitException.networkError((IOException) throwable);
        }

        // We don't know what happened. We need to simply convert to an unknown error
        return RetrofitException.unexpectedError(throwable);
    }

    /**
     * Creates a returns a generic error message. If there is no internet connectivity, return message
     * related to that.
     * @param context   Context
     * @return  Error response model
     */
    private static ErrorResponseModel getGenericErrorModel(Context context)
    {
        ErrorResponseModel errorResponseModel = new ErrorResponseModel();
        errorResponseModel.setError(context.getString(R.string.error));

        //  If not connected to internet, then set internet connectivity message
        if(!GeneralUtils.isConnectedToInternet(context))
        {
            errorResponseModel.setMessage(context.getString(R.string.error_please_check_your_internet));
        }else
        {
            //  Generic message.
            errorResponseModel.setMessage(context.getString(R.string.error_some_thing_went_wrong));
        }

        return errorResponseModel;
    }
}
