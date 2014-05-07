package com.lucaslou.framework.implementation;

import android.graphics.Bitmap;

import com.lucaslou.framework.Image;
import com.lucaslou.framework.Graphics.ImageFormat;

public class AndroidImage implements Image {
    public Bitmap bitmap;
    ImageFormat format;
    
    public AndroidImage(Bitmap bitmap, ImageFormat format) {
        this.bitmap = bitmap;
        this.format = format;
    }

    @Override
    public int getWidth() {
        return bitmap.getWidth();
    }
    
    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }

    @Override
    public ImageFormat getFormat() {
        return format;
    }

    @Override
    public void dispose() {
        bitmap.recycle();
    }      
}