package com.ynot.androidengine.layout;

import com.ynot.androidengine.layout.Graphics.ImageFormat;

public interface Image {
    public int getWidth();
    public int getHeight();
    public ImageFormat getFormat();
    public void dispose();
}
