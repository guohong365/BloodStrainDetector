package com.uc.bloodstraindetector;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.uc.bloodstraindetector.model.CaseItem;
import com.uc.bloodstraindetector.model.DataManager;
import com.uc.bloodstraindetector.model.ImageItem;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    static final String TAG="TEST_LOG";
    Random random=new Random();
    protected CaseItem createEntity(Long key) {
        CaseItem entity = new CaseItem();
        entity.setId(key);
        entity.setTitle(UUID.randomUUID().toString());
        entity.setOperator(UUID.randomUUID().toString());
        entity.setDescription(UUID.randomUUID().toString());
        entity.setLevel(random.nextInt(4));
        entity.setCreateTime(new Date());
        return entity;
    }

    protected ImageItem createImageItem(Long key, Long caseId) {
        ImageItem entity = new ImageItem();
        entity.setId(key);
        entity.setCaseId(caseId);
        entity.setUriString(UUID.randomUUID().toString());
        entity.setWidth(random.nextInt());
        entity.setHeight(random.nextInt());
        entity.setTakenTime(new Date());
        entity.setSize(random.nextInt());
        return entity;
    }
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.uc.bloodstraindetector", appContext.getPackageName());
    }

    @Test
    public void dataManagerTest(){
        List<CaseItem> caseItems= DataManager.getInstance().getCases();
        int count=caseItems.size();
        Log.d(TAG, "load case " + count);
        CaseItem caseItem=createEntity(null);
        Log.d(TAG, "------ before insert --------");
        Log.d(TAG, caseItem.toString());
        DataManager.getInstance().insertCase(caseItem);
        Log.d(TAG, "------ after insert --------");
        Log.d(TAG, caseItem.toString());
        assertEquals(count + 1, DataManager.getInstance().getCases().size());
        assertEquals(caseItems.size() , DataManager.getInstance().getCases().size());


        caseItem =createEntity(caseItems.get(0).getId());
        Log.d(TAG, "dataManagerTest: updated before ----------------------------------");
        Log.d(TAG, "dataManagerTest: case=" + caseItem.toString());
        DataManager.getInstance().updateCase(caseItem);
        Log.d(TAG, "dataManagerTest: updated after ----------------------------------");
        Log.d(TAG, "dataManagerTest: case=" + caseItem.toString());


        for(CaseItem item : caseItems){
            count = random.nextInt(5);
            for(int i=0; i< count; i++){
                ImageItem imageItem=createImageItem(null, item.getId());
                DataManager.getInstance().insertImageItem(imageItem);
            }
            Log.d(TAG, item.toString());
            Log.d(TAG, "===================");
        }




    }
}
