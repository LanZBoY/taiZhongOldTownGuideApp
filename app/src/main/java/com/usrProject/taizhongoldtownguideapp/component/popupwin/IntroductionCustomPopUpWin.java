package com.usrProject.taizhongoldtownguideapp.component.popupwin;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.usrProject.taizhongoldtownguideapp.GlideApp;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.model.MapClickDTO;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;


public class IntroductionCustomPopUpWin extends CustomPopUpWin {
    private Context mContext;
    private MapClickDTO mapClickDTO;
    private TextView titleTextView;
    private TextView descTextView;
    private ImageView imgImageView;
    private StorageReference storageReference;

    private boolean initFlag = false;
    private ArrayList<String> allImgNames;
    private HashMap<String, byte[]> imgMap;

    private int currentImg = 0;
//  目前限制傳輸量最多為3MB
    private final long ONE_MEGABYTE = 3 * 1024 * 1024;
    public IntroductionCustomPopUpWin(Context mContext, int xmlLayout, MapClickDTO mapClickDTO) {
        super(mContext, xmlLayout,true);
        this.mContext = mContext;
        this.mapClickDTO = mapClickDTO;
        titleTextView = getView().findViewById(R.id.title_TextView);
        descTextView =  getView().findViewById(R.id.contentTextView);
        imgImageView = getView().findViewById(R.id.contentImageView);
        storageReference = FirebaseStorage.getInstance(mContext.getString(R.string.storage)).getReference();
        initImageView();
        //這裡可能要寫成看資料有多少再去找
        titleTextView.setText(mapClickDTO.title);
        descTextView.setText(mapClickDTO.desc);

    }
    private void initImageView(){
        if(StringUtils.isNotBlank(mapClickDTO.imgId)){
            allImgNames = new ArrayList<>();
            imgMap = new HashMap<>();
            Log.d(FirebaseStorage.class.getSimpleName(),storageReference.child("MapClick").child(mapClickDTO.imgId).getPath());
            storageReference.child("MapClick").child(mapClickDTO.imgId).listAll().addOnCompleteListener(task -> {
                if(task.isComplete()){
                    for(StorageReference temp : task.getResult().getItems()){
                        Log.d(FirebaseStorage.class.getSimpleName(),temp.getName());
                        allImgNames.add(temp.getName());
                        imgMap.put(temp.getName(), null);
                    }
                    if(allImgNames.size() > 1){
                        imgImageView.setOnClickListener(view -> {
                            currentImg = (currentImg + 1) % allImgNames.size();
                            GlideApp.with(mContext)
                                    .load(imgMap.get(allImgNames.get(currentImg)))
                                    .into(imgImageView);
                        });
                    }
                    loadImgResource();
                }
            });
        }
    }

    private void loadImgResource(){
        if(imgMap.size() == 0){
            return;
        }
        for(String imgName : allImgNames){
            storageReference.child("MapClick").child(mapClickDTO.imgId).child(imgName).getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                imgMap.put(imgName, bytes);
                if(!initFlag){
                    GlideApp.with(mContext)
                            .load(bytes)
                            .into(imgImageView);
                    imgImageView.setVisibility(View.VISIBLE);
                    initFlag = true;
                }
            });
        }
    }
}
