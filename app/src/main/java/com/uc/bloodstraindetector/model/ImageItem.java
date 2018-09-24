package com.uc.bloodstraindetector.model;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.uc.bloodstraindetector.BloodStrainDetectorApp;
import com.uc.model.AbstractSelectable;
import com.uc.utils.SysUtils;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.File;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity(nameInDb = "T_IMAGE")
public class ImageItem extends AbstractSelectable implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private Long caseId;
    @NotNull
    @Unique
    private String uriString;
    private int width;
    private int height;
    private Double latitude;
    private Double longitude;
    @NotNull
    private Date takenTime;
    private long size;

    @Transient
    private boolean dirty;
    @Transient
    private String takenDate;
    @Transient
    private Uri uri;

    public Uri getUri() {
        if(uri==null && uriString!=null){
           uri = Uri.parse(uriString);
        }
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
        this.uriString=uri.toString();
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }
    public String getTakenDate(){
        if(takenDate==null)
            takenDate=SysUtils.dateFormat.format(takenTime);
        return takenDate;
    }

    @Override
    public String toString() {
        return "id=" + getId() + ", " +
                "uri=" + getUriString() + ", " +
                "width=" + getWidth() + ", " +
                "height=" + getHeight() + ", " +
                "size=" + getSize() + ", " +
                "longitude=" + getLongitude() + ", " +
                "latitude=" + getLatitude() + ", " +
                "takenTime=" +(getTakenTime()==null ? "NULL" : SysUtils.dataTimeFormat.format(getTakenTime())) + ", " +
                "takenDate=" + (getTakenTime()==null ? "NULL" : getTakenDate());
    }

  
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.caseId);
        dest.writeString(this.uriString);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
        dest.writeLong(this.takenTime != null ? this.takenTime.getTime() : -1);
        dest.writeLong(this.size);
        dest.writeByte(this.dirty ? (byte) 1 : (byte) 0);
        dest.writeString(this.takenDate);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.selectable ? (byte) 1 : (byte) 0);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCaseId() {
        return this.caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getUriString() {
        return this.uriString;
    }

    public void setUriString(String uriString) {
        this.uri=null;
        this.uriString = uriString;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Date getTakenTime() {
        return this.takenTime;
    }

    public void setTakenTime(Date takenTime) {
        this.takenTime = takenTime;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    protected ImageItem(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.caseId = (Long) in.readValue(Long.class.getClassLoader());
        this.uriString = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        long tmpTakenTime = in.readLong();
        this.takenTime = tmpTakenTime == -1 ? null : new Date(tmpTakenTime);
        this.size = in.readLong();
        this.dirty = in.readByte() != 0;
        this.takenDate = in.readString();
        this.selected = in.readByte() != 0;
        this.selectable = in.readByte() != 0;
    }

    @Generated(hash = 305843251)
    public ImageItem(Long id, @NotNull Long caseId, @NotNull String uriString, int width,
            int height, Double latitude, Double longitude, @NotNull Date takenTime,
            long size) {
        this.id = id;
        this.caseId = caseId;
        this.uriString = uriString;
        this.width = width;
        this.height = height;
        this.latitude = latitude;
        this.longitude = longitude;
        this.takenTime = takenTime;
        this.size = size;
    }

    @Generated(hash = 1053804574)
    public ImageItem() {
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
}
