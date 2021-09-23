package com.usrProject.taizhongoldtownguideapp.component.popupwin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.usrProject.taizhongoldtownguideapp.GlideApp;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.model.MapClickDTO;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class IntroductionCustomPopUpWin extends CustomPopUpWin {
    private Context mContext;
    private MapClickDTO mapClickDTO;
    private TextView titleTextView;
    private TextView descTextView;
    private ImageSwitcher imageSwitcher;
    private ProgressBar progressBarImgCount;

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
        imageSwitcher = getView().findViewById(R.id.contentImageSwitcher);
        imageSwitcher.setFactory(() -> {
            ImageView imageView = new ImageView(mContext);
//          這邊可已設定imageView的設定
            return imageView;
        });
        imageSwitcher.setAnimateFirstView(true);
//      設定動畫元件 時間
        Animation outAnimation,inAnimation;
        outAnimation = AnimationUtils.loadAnimation(mContext,R.anim.fade_out);
        outAnimation.setDuration(400);
        inAnimation = AnimationUtils.loadAnimation(mContext,R.anim.fade_in);
        inAnimation.setDuration(400);
        imageSwitcher.setOutAnimation(outAnimation);
        imageSwitcher.setInAnimation(inAnimation);
        progressBarImgCount = getView().findViewById(R.id.progressBar_imgCount);
        storageReference = FirebaseStorage.getInstance(mContext.getString(R.string.storage)).getReference();
        initImageView();
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
                    for(StorageReference temp : Objects.requireNonNull(task.getResult()).getItems()){
                        allImgNames.add(temp.getName());
                        imgMap.put(temp.getName(), null);
                    }
                    if(allImgNames.size() > 1){
                        progressBarImgCount.setVisibility(View.VISIBLE);
                        progressBarImgCount.setMax(allImgNames.size());
                        progressBarImgCount.setProgress(currentImg + 1);
                        imageSwitcher.setOnClickListener(view -> showImgResource());
                    }
                    loadImgResource();
                }
            });
        }
    }

    private void showImgResource(){
        currentImg = (currentImg + 1) % allImgNames.size();
        Log.d("CURRENT",String.format("%d",currentImg));
        progressBarImgCount.setProgress(currentImg + 1,true);
        imageSwitcher.showNext();
        GlideApp.with(mContext)
                .load(imgMap.get(allImgNames.get(currentImg)))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageSwitcher.setImageDrawable(resource);
                        return true;
                    }
                })
                .into((ImageView) imageSwitcher.getCurrentView());
    }

    private void loadImgResource(){
        if(imgMap.size() == 0){
            return;
        }
        for(String imgName : allImgNames){
            storageReference.child("MapClick").child(mapClickDTO.imgId).child(imgName).getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                imgMap.put(imgName, bytes);
                Log.d(FirebaseStorage.class.getSimpleName(),imgName);
                if(!initFlag){
                    currentImg = allImgNames.indexOf(imgName);
                    GlideApp.with(mContext)
                            .load(bytes)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    imageSwitcher.setImageDrawable(resource);
                                    return true;
                                }
                            })
                            .into((ImageView) imageSwitcher.getCurrentView());
                    progressBarImgCount.setProgress(currentImg + 1 ,true);
                    imageSwitcher.setVisibility(View.VISIBLE);
                    initFlag = true;
                }
            });
        }
    }
}
