package me.iwf.PhotoPickerDemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.utils.MyPhotoUtil;
import me.iwf.photopicker.widget.MultiPickResultView;

public class PhotoPickerActivity extends AppCompatActivity {

    MultiPickResultView recyclerView;

//    MultiPickResultView recyclerViewShowOnly;

    protected ArrayList<String> pathslook;
    protected ArrayList<String> tempPathslook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);
        Button btn = (Button) findViewById(R.id.btn_test);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(MainActivity.RequestCode.ButtonMultiplePicked);
            }
        });

        pathslook = new ArrayList<>();
        tempPathslook = new ArrayList<>();

        recyclerView = (MultiPickResultView) findViewById(R.id.recycler_view);
        recyclerView.init(this, MultiPickResultView.ACTION_SELECT, null);

//        recyclerViewShowOnly = (MultiPickResultView) findViewById(R.id.recycler_onlylook);
//        recyclerViewShowOnly.init(this, MultiPickResultView.ACTION_ONLY_SHOW, pathslook);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // permission was granted, yay!
            onClick(MainActivity.RequestCode.values()[requestCode].mViewId);

        } else {
            // permission denied, boo! Disable the
            // functionality that depends on this permission.
            Toast.makeText(this, "No read storage permission! Cannot perform the action.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.CAMERA:
                // No need to explain to user as it is obvious
                return false;
            default:
                return true;
        }
    }

    private void checkPermission(@NonNull MainActivity.RequestCode requestCode) {

        int readStoragePermissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraPermissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        boolean readStoragePermissionGranted = readStoragePermissionState != PackageManager.PERMISSION_GRANTED;
        boolean cameraPermissionGranted = cameraPermissionState != PackageManager.PERMISSION_GRANTED;

        if (readStoragePermissionGranted || cameraPermissionGranted) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                String[] permissions;
                if (readStoragePermissionGranted && cameraPermissionGranted) {
                    permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                } else {
                    permissions = new String[]{
                            readStoragePermissionGranted ? Manifest.permission.READ_EXTERNAL_STORAGE
                                    : Manifest.permission.CAMERA
                    };
                }
                ActivityCompat.requestPermissions(this,
                        permissions,
                        requestCode.ordinal());
            }

        } else {
            // Permission granted
            onClick(R.id.btn_test);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            pathslook = (ArrayList<String>) MyPhotoUtil.getPhotoMap();
        } catch (Exception ex) {
//                    Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
        if(pathslook!=null){
            recyclerView.init(this, MultiPickResultView.ACTION_SELECT, pathslook);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent datad = data;
        //这里捕捉异常
        try{
            tempPathslook = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
        }catch (Exception ex){
//                Toast.makeText(PhotoPickerActivity.this,ex.toString(),Toast.LENGTH_SHORT).show();
        }
        //判断当前的集合和返回集合的差异，如果返回的更多，则添加进去，否则，则将其替换为返回的集合
        if(tempPathslook.size()>pathslook.size()){
            for (String temp : tempPathslook) {
                if (!pathslook.contains(temp)) {
                    pathslook.add(temp);
                }
            }
        }else{
            pathslook = tempPathslook;
        }
//        data.putStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS, pathslook);
//        recyclerView.onActivityResult(requestCode,resultCode,data);
        MyPhotoUtil.putPhotoMap(pathslook);
//        ArrayList<String> temp = pathslook;
//        recyclerView.showPics(temp);
        recyclerView.init(this, MultiPickResultView.ACTION_SELECT, pathslook);
}

    private void onClick(@IdRes int viewId) {

        switch (viewId) {
            case R.id.btn_test: {
                try {
                    pathslook = (ArrayList<String>) MyPhotoUtil.getPhotoMap();
                } catch (Exception ex) {
//                    Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
                }
                PhotoPicker.builder()
                        .setPhotoCount(2)
                        .setShowCamera(true)
                        .setSelected(pathslook)
                        .start(this);
                break;
            }
        }
    }
}
