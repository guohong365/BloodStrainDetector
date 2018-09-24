package com.uc.bloodstraindetector.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.uc.bloodstraindetector.BloodStrainDetectorApp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class DataManager {

    public interface OnItemAddedListener<T>{
        void onItemAdded(T added);
    }
    public interface OnItemRemoveListener<T>{
        void onItemRemoved(T removed);
    }
    public interface OnItemUpdatedListener<T>{
        void onItemUpdated(T updated);
    }
    private List<? extends OnItemAddedListener<CaseItem>> caseAddListeners=new ArrayList<>();
    private List<? extends OnItemRemoveListener<CaseItem>> caseRemoveListeners=new ArrayList<>();
    private List<? extends OnItemUpdatedListener<CaseItem>> caseUpdatedListeners=new ArrayList<>();

    private List<? extends OnItemAddedListener<ImageItem>> imageAddedListeners=new ArrayList<>();
    private List<? extends OnItemRemoveListener<ImageItem>> imageRemovedListeners=new ArrayList<>();
    private List<? extends OnItemUpdatedListener<ImageItem>> imageUpdatedListeners=new ArrayList<>();

    private static final String TAG="DataManager";
    private List<CaseItem> cases;
    private List<ImageItem> images;
    private DaoSession session;
    private static DataManager instance;
    private static Comparator<CaseItem> caseItemComparator=new Comparator<CaseItem>() {
        @Override
        public int compare(CaseItem o1, CaseItem o2) {
            return o2.getCreateTime().compareTo(o1.getCreateTime());
        }
    };
    private static Comparator<ImageItem> imageItemComparator=new Comparator<ImageItem>() {
        @Override
        public int compare(ImageItem o1, ImageItem o2) {
            return o2.getTakenTime().compareTo(o1.getTakenTime());
        }
    };
    public static DataManager getInstance() {
        synchronized (DataManager.class){
            if(instance==null){
                instance=new DataManager();
                instance.session= BloodStrainDetectorApp.getInstance().getDaoSession();
                instance.load();
            }
        }
        return instance;
    }

    protected DataManager(){
        images=new ArrayList<>();
    }

    public List<CaseItem> getCases() {
        return cases;
    }

    public List<ImageItem> getImages() {
        return images;
    }

    private void load(){
        cases=  session.getCaseItemDao()
                .queryBuilder()
                .orderDesc(CaseItemDao.Properties.CreateTime)
                .build()
                .list();
        for(CaseItem item : cases){
            if(item.getImages().size()>0){
                images.addAll(item.getImages());
            }
        }
        images.sort(imageItemComparator);
        Log.d(TAG, "load: all data loaded. case count=" + cases.size());
        Log.d(TAG, "load: " + cases.toString());
    }

    public void reload(){
        session.clear();
        load();
    }

    public CaseItem findCaseById(@NonNull Long caseId){
        for(CaseItem item : cases){
            if(item.getId().equals(caseId)) return item;
        }
        return null;
    }

    public CaseItem findCaseByTitle(@NonNull String title){
        for(CaseItem item : cases){
            if(item.getTitle().equals(title)) return item;
        }
        return null;
    }

    public void insertCase(CaseItem caseItem){
        session.getCaseItemDao().insert(caseItem);
        cases.add(0,caseItem);
    }

    public void updateCase(CaseItem caseItem){
        session.getCaseItemDao().update(caseItem);
        caseItem.setDirty(false);
    }

    public void deleteCase(final CaseItem caseItem) throws Exception {
        synchronized (DataManager.class) {
            session.runInTx(new Runnable() {
                @Override
                public void run() {
                    List<ImageItem> imageItems = caseItem.getImages();
                    session.getImageItemDao().deleteInTx(imageItems);
                    session.getCaseItemDao().delete(caseItem);
                    session.getDatabase().endTransaction();
                    images.removeAll(imageItems);
                    cases.remove(caseItem);
                }
            });
        }
    }

    public ImageItem findImageById(Long id) {
        for(ImageItem item : images){
            if(item.getId().longValue()==id){
                return item;
            }
        }
        return null;
    }

    public boolean insertImageItem(ImageItem imageItem){
        CaseItem caseItem=findCaseById(imageItem.getCaseId());
        if(caseItem==null){
            Log.d(TAG, "insertImageItem: no CASE related.");
            return false;
        }
        Log.d(TAG, "insertImageItem: case found " + caseItem.toString());
        session.getImageItemDao().insert(imageItem);
        Log.d(TAG, "insertImageItem: insert into DB OK.");
        imageItem.setDirty(false);
        caseItem.getImages().add(0, imageItem);
        images.add(0, imageItem);
        return true;
    }
    public void updateImageItem(ImageItem imageItem){
        session.getImageItemDao().update(imageItem);
    }

    public void deleteImageItem(ImageItem imageItem){
        findCaseById(imageItem.getCaseId()).getImages().remove(imageItem);
        session.getImageItemDao().delete(imageItem);
        images.remove(imageItem);
    }

    public void deleteImageItems(Collection<ImageItem> imageItems){
        for(CaseItem caseItem : cases){
            caseItem.getImages().removeAll(imageItems);
        }
        session.getImageItemDao().deleteInTx(imageItems);
        imageItems.removeAll(imageItems);
    }

    public boolean isTitleDuplicated(CaseItem caseItem){
        CaseItem item=findCaseByTitle(caseItem.getTitle());
        if(item!=null && !item.getId().equals(caseItem.getId())) return true;
        return false;
    }

    public static Comparator<? super ImageItem> getImageComparator() {
        return imageItemComparator;
    }

    public static Comparator<? extends CaseItem> getCaseComparator() {
        return caseItemComparator;
    }

    public static List<ImageGroupItem> buildImageGroup(List<ImageItem> imageItems, CaseItem caseItem){
        Map<String, List<ImageItem>> map = new HashMap<>();
        for (ImageItem item : imageItems){
            if(map.containsKey(item.getTakenDate())){
                map.get(item.getTakenDate()).add(item);
            } else {
                List<ImageItem> list=new ArrayList<>();
                list.add(item);
                map.put(item.getTakenDate(), list);
            }
        }
        List<ImageGroupItem> groupItems=new ArrayList<>();
        for (String key : map.keySet()){
            List<ImageItem> list = map.get(key);
            list.sort(imageItemComparator);
            ImageGroupItem groupItem=new ImageGroupItem(key, list, caseItem);
            groupItems.add(groupItem);
        }
        groupItems.sort(new Comparator<ImageGroupItem>() {
            @Override
            public int compare(ImageGroupItem o1, ImageGroupItem o2) {
                return o2.getGroupName().compareTo(o1.getGroupName());
            }
        });
        Log.d(TAG, "buildImageGroup: group count=" + groupItems.size());
        for (ImageGroupItem group : groupItems){
            Log.d(TAG, "buildImageGroup: group["+group.getGroupName()+"] ["+group.getItems().toString()+"]");
        }
        return groupItems;
    }
}
