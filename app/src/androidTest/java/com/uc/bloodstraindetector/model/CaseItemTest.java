package com.uc.bloodstraindetector.model;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import com.uc.bloodstraindetector.model.CaseItem;
import com.uc.bloodstraindetector.model.CaseItemDao;

import java.util.Date;
import java.util.UUID;

public class CaseItemTest extends AbstractDaoTestLongPk<CaseItemDao, CaseItem> {

    public CaseItemTest() {
        super(CaseItemDao.class);
    }

    @Override
    protected CaseItem createEntity(Long key) {
        CaseItem entity = new CaseItem();
        entity.setId(key);
        entity.setOperator(UUID.randomUUID().toString());
        entity.setLevel(random.nextInt(4));
        entity.setCreateTime(new Date());
        return entity;
    }
}
