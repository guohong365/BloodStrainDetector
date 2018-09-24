package com.uc.bloodstraindetector.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.NotNull;

public class CompareParams implements Parcelable {
    public static final String KEY_REQUEST_COMPARE="REQUEST_COMPARE";
    public static final int COMPARE_MODE_PARALLEL=0;
    public static final int COMPARE_MODE_OVERLAP=1;

    public int compareMode;
    public Long caseId;
    public String [] images;  //input-- tow images to compared,
                                // output --return saved images when comparing
    public CompareParams(int compareMode, @NonNull Long caseId, @NotNull String[] images) {
        this.compareMode = compareMode;
        this.caseId=caseId;
        this.images=images;
    }

    @Override
    public String toString() {
        return "mode=[" + compareMode + "]" +
                "case id=[" + caseId + "]\n" +
                "image 1=[" + images[0] + "]\n" +
                "image 2=[" + images[1] + "]\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.compareMode);
        dest.writeValue(this.caseId);
        dest.writeStringArray(this.images);
    }

    protected CompareParams(Parcel in) {
        this.compareMode = in.readInt();
        this.caseId = (Long) in.readValue(Long.class.getClassLoader());
        this.images = in.createStringArray();
    }

    public static final Creator<CompareParams> CREATOR = new Creator<CompareParams>() {
        @Override
        public CompareParams createFromParcel(Parcel source) {
            return new CompareParams(source);
        }

        @Override
        public CompareParams[] newArray(int size) {
            return new CompareParams[size];
        }
    };
}
