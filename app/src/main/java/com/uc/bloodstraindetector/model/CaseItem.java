package com.uc.bloodstraindetector.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.uc.model.AbstractSelectable;
import com.uc.utils.SysUtils;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(nameInDb = "T_CASE")
public class CaseItem extends AbstractSelectable implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String operator;
    @NotNull
    @Unique
    private String title;
    private String description;
    private int level;
    @NotNull
    private Date createTime;
    private Double longitude;
    private Double latitude;
    @ToMany(referencedJoinProperty = "caseId")
    @OrderBy(value = "takenTime desc")
    private List<ImageItem> images;
    @Transient
    private boolean dirty;
    @Transient
    private String createDate;

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }

    public int getImageCount(){
        return daoSession==null ? 0 : getImages().size();
    }

    public void insertImage(ImageItem imageItem){
        imageItem.setCaseId(getId());
        imageItem.setDirty(true);
        if(images==null){
            getImages().add(imageItem);
        } else {
            images.add(imageItem);
        }
        images.sort(DataManager.getInstance().getImageComparator());
        DataManager.getInstance().insertImageItem(imageItem);
    }

    public Uri getPreviewUri(){
        return getImageCount() >0 ? getImages().get(0).getUri():null;

    }

    public String getCreateDate(){
        if(createDate==null)
            createDate=SysUtils.dateFormat.format(getCreateTime());
        return createDate;
    }

    @Override
    public String toString() {
        String ret= "id=" + getId() + ", " +
                "title=" + getTitle() + ", " +
                "operator=" + getOperator() + ", " +
                "description=" + getDescription() + ", " +
                "level=" + getLevel() + ", " +
                "longitude=" + getLongitude() + ", " +
                "latitude=" + getLatitude() + ", " +
                "imageCount=" + getImageCount() + ", " +
                "preview=" + getPreviewUri() + "," +
                "myDao=" + (myDao==null ? "NULL" : "OK");
        if(getImageCount() > 0){
            ret += ", images=[";
            for(ImageItem item:getImages()){
                ret +="[" + item.toString() + "], ";
            }
            ret += "]";
        }
        return ret;
    }

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 819375847)
    private transient CaseItemDao myDao;
    @Generated(hash = 1261053578)
    public CaseItem(Long id, @NotNull String operator, @NotNull String title, String description,
            int level, @NotNull Date createTime, Double longitude, Double latitude) {
        this.id = id;
        this.operator = operator;
        this.title = title;
        this.description = description;
        this.level = level;
        this.createTime = createTime;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Generated(hash = 1958375695)
    public CaseItem() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getOperator() {
        return this.operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getLevel() {
        return this.level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public Date getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Double getLongitude() {
        return this.longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public Double getLatitude() {
        return this.latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1242436693)
    public List<ImageItem> getImages() {
        if (images == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ImageItemDao targetDao = daoSession.getImageItemDao();
            List<ImageItem> imagesNew = targetDao._queryCaseItem_Images(id);
            synchronized (this) {
                if (images == null) {
                    images = imagesNew;
                }
            }
        }
        return images;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 604059028)
    public synchronized void resetImages() {
        images = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.operator);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeInt(this.level);
        dest.writeLong(this.createTime != null ? this.createTime.getTime() : -1);
        dest.writeValue(this.longitude);
        dest.writeValue(this.latitude);
        dest.writeList(this.images);
        dest.writeByte(this.dirty ? (byte) 1 : (byte) 0);
        dest.writeString(this.createDate);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.selectable ? (byte) 1 : (byte) 0);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1125044818)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCaseItemDao() : null;
    }

    protected CaseItem(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.operator = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.level = in.readInt();
        long tmpCreateTime = in.readLong();
        this.createTime = tmpCreateTime == -1 ? null : new Date(tmpCreateTime);
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.images = new ArrayList<ImageItem>();
        in.readList(this.images, ImageItem.class.getClassLoader());
        this.dirty = in.readByte() != 0;
        this.createDate = in.readString();
        this.selected = in.readByte() != 0;
        this.selectable = in.readByte() != 0;
    }

    public static final Parcelable.Creator<CaseItem> CREATOR = new Parcelable.Creator<CaseItem>() {
        @Override
        public CaseItem createFromParcel(Parcel source) {
            return new CaseItem(source);
        }

        @Override
        public CaseItem[] newArray(int size) {
            return new CaseItem[size];
        }
    };
}
