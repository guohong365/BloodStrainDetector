package com.uc.bloodstraindetector.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.uc.model.DataUpdatedState;

public class ImageItemState implements Parcelable {
    public DataUpdatedState state;
    public ImageItem imageItem;

    public ImageItemState(ImageItem imageItem, DataUpdatedState state){
        this.imageItem=imageItem;
        this.state=state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeParcelable(this.imageItem, flags);
    }

    public ImageItemState() {
    }

    protected ImageItemState(Parcel in) {
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : DataUpdatedState.values()[tmpState];
        this.imageItem = in.readParcelable(ImageItem.class.getClassLoader());
    }

    public static final Parcelable.Creator<ImageItemState> CREATOR = new Parcelable.Creator<ImageItemState>() {
        @Override
        public ImageItemState createFromParcel(Parcel source) {
            return new ImageItemState(source);
        }

        @Override
        public ImageItemState[] newArray(int size) {
            return new ImageItemState[size];
        }
    };
}
