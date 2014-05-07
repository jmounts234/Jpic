package com.lucaslou.framework;

import android.graphics.Bitmap;

import com.lucaslou.framework.Graphics.ImageFormat;

public interface Image {
    public int getWidth();
    public Bitmap getBitmap();
    public int getHeight();
    public ImageFormat getFormat();
    public void dispose();
    
}
 