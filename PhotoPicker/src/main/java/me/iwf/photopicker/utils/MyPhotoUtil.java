package me.iwf.photopicker.utils;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jiang on 2016/10/28.
 */

public class MyPhotoUtil extends Application{
    public static Map mPhotos;
    public static int FORRESULT = 100;

    public static void putPhotoMap(Object mSelectedphotos) {
        mPhotos = new HashMap<String,String>();
        mPhotos.put("photos",mSelectedphotos);
    }
    public static Object getPhotoMap(){
        if(mPhotos.get("photos") == null){
            return 0;
        }else{
            return mPhotos.get("photos");
        }
    }
}
