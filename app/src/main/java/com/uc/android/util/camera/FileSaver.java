package com.uc.android.util.camera;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by guohog on 2017/12/28.
 */

public class FileSaver implements PictureSaver {
    private NameGenerator nameGenerator;
    public FileSaver(NameGenerator nameGenerator){
        this.nameGenerator=nameGenerator;
    }
    @Override
    public void save(byte[] data) {
        File file=new File(nameGenerator.getNewFullPathName());
        try{
            FileOutputStream stream=new FileOutputStream(file);
            stream.write(data);
            stream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
