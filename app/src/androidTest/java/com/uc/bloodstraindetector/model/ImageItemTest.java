package com.uc.bloodstraindetector.model;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import com.uc.bloodstraindetector.model.ImageItem;
import com.uc.bloodstraindetector.model.ImageItemDao;

import java.util.Date;
import java.util.UUID;

public class ImageItemTest extends AbstractDaoTestLongPk<ImageItemDao, ImageItem> {

    public ImageItemTest() {
        super(ImageItemDao.class);
    }

    @Override
    protected ImageItem createEntity(Long key) {
        ImageItem entity = new ImageItem();
        entity.setId(key);
        entity.setCaseId(0L);
        entity.setUriString(UUID.randomUUID().toString());
        entity.setWidth(random.nextInt());
        entity.setHeight(random.nextInt());
        entity.setTakenTime(new Date());
        entity.setSize(random.nextInt());
        return entity;
    }

}
