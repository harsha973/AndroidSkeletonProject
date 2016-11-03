package com.in.sha.skeletonproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.in.sha.skeletonproject.base.BaseModel;

/**
 * Created by sreepolavarapu on 4/03/16.
 *
 */
public class ErrorResponseModel extends BaseModel implements Parcelable {

    /**
     * statusCode : 422
     * error : Unprocessable Entity
     * message : Error processing Report
     */

    @SerializedName("statusCode")
    private int mStatusCode;
    @SerializedName("error")
    private String mError;
    @SerializedName("message")
    private String mMessage;

    public int getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(int mStatusCode) {
        this.mStatusCode = mStatusCode;
    }

    public String getError() {
        return mError;
    }

    public void setError(String mError) {
        this.mError = mError;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mStatusCode);
        dest.writeString(this.mError);
        dest.writeString(this.mMessage);
    }

    public ErrorResponseModel() {
    }

    protected ErrorResponseModel(Parcel in) {
        this.mStatusCode = in.readInt();
        this.mError = in.readString();
        this.mMessage = in.readString();
    }

    public static final Creator<ErrorResponseModel> CREATOR = new Creator<ErrorResponseModel>() {
        @Override
        public ErrorResponseModel createFromParcel(Parcel source) {
            return new ErrorResponseModel(source);
        }

        @Override
        public ErrorResponseModel[] newArray(int size) {
            return new ErrorResponseModel[size];
        }
    };
}
