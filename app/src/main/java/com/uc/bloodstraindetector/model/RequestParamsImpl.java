package com.uc.bloodstraindetector.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.uc.activity.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class RequestParamsImpl implements RequestParams {
    private int action;
    private int position;
    private CaseItem caseItem;
    private String message;
    private Integer requestCount;
    private List<ImageItem> imageItems;
    public RequestParamsImpl(int action){
        this(action, -1, null);
    }

    public RequestParamsImpl(int action, int position, CaseItem caseItem){
        this(action, position, caseItem, null);
    }
    public RequestParamsImpl(int action, int position, CaseItem caseItem, String message){
        this(action, position, caseItem, message,  null);
    }
    public RequestParamsImpl(int action, int position, CaseItem caseItem, String message,
                             Integer requestCount){
        this(action, position, caseItem, message, requestCount, new ArrayList<ImageItem>());

    }
    public RequestParamsImpl(int action, int position, CaseItem caseItem, String message,
                             Integer requestCount,
                             @NonNull List<ImageItem> imageItems) {
        this.action = action;
        this.position = position;
        this.caseItem = caseItem;
        this.message = message;
        this.requestCount = requestCount;
        this.imageItems=imageItems;
    }

    @Override
    public String toString() {
        StringBuilder builder=new StringBuilder();
        builder.append("-------").append(getClass().getSimpleName()).append("--------\n")
                .append("action=[").append(action).append("][").append(getAction()).append(']').append('\n')
                .append("case=[").append(caseItem).append(']').append('\n')
                .append("position=[").append(position).append(']').append('\n')
                .append("request count=[").append(requestCount).append(']').append('\n')
                .append("message=[").append(message).append(']').append('\n')
                .append("image items:\n");
        if(imageItems.size()>0) {
            for (ImageItem item : imageItems) {
                builder.append('\t').append(item).append('\n');
            }
        } else {
            builder.append("\timage items are empty.");
        }
        builder.append("=====================");
        return builder.toString();
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public CaseItem getCaseItem() {
        return caseItem;
    }

    public void setCaseItem(CaseItem caseItem) {
        this.caseItem = caseItem;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }

    public List<ImageItem> getImageItems() {
        return imageItems;
    }

    public void setImageItems(List<ImageItem> imageItems) {
        this.imageItems = imageItems;
    }

    public  List<ImageItem> getSelectedItems(){
        List<ImageItem> selectedItems=new ArrayList<>();
        for(ImageItem item : imageItems){
            if(item.isSelected()) selectedItems.add(item);
        }
        return selectedItems;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.action);
        dest.writeInt(this.position);
        dest.writeParcelable(this.caseItem, flags);
        dest.writeString(this.message);
        dest.writeValue(this.requestCount);
        dest.writeTypedList(this.imageItems);
    }

    protected RequestParamsImpl(Parcel in) {
        this.action = in.readInt();
        this.position = in.readInt();
        this.caseItem = in.readParcelable(CaseItem.class.getClassLoader());
        this.message = in.readString();
        this.requestCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.imageItems = in.createTypedArrayList(ImageItem.CREATOR);
    }

    public static final Creator<RequestParamsImpl> CREATOR = new Creator<RequestParamsImpl>() {
        @Override
        public RequestParamsImpl createFromParcel(Parcel source) {
            return new RequestParamsImpl(source);
        }

        @Override
        public RequestParamsImpl[] newArray(int size) {
            return new RequestParamsImpl[size];
        }
    };
}
