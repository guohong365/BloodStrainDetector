package com.uc.activity;

import android.net.Uri;
import android.os.Parcel;

public class RequestPhotoParams extends AbstractRequestParams {
    protected Uri output;

    public RequestPhotoParams(int action, Uri output){
        super(action);
        this.output=output;
    }

    public Uri getOutput() {
        return output;
    }

    public void setOutput(Uri output) {
        this.output = output;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.output, flags);
        dest.writeInt(this.action);
    }

    protected RequestPhotoParams(Parcel in) {
        this.output = in.readParcelable(Uri.class.getClassLoader());
        this.action = in.readInt();
    }

    public static final Creator<RequestPhotoParams> CREATOR = new Creator<RequestPhotoParams>() {
        @Override
        public RequestPhotoParams createFromParcel(Parcel source) {
            return new RequestPhotoParams(source);
        }

        @Override
        public RequestPhotoParams[] newArray(int size) {
            return new RequestPhotoParams[size];
        }
    };
}
