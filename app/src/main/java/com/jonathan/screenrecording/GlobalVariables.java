package com.jonathan.screenrecording;

import android.media.projection.MediaProjection;
import android.view.Surface;

public class GlobalVariables {
    private static MediaProjection mMediaProjection;
    private static Surface surface;


    public Surface getSurface() {
        return surface;
    }

    public void setSurface(Surface surface) {
        GlobalVariables.surface = surface;
    }


    public MediaProjection getmMediaProjection() {
        return mMediaProjection;
    }

    public void setmMediaProjection(MediaProjection mMediaProjection) {
        GlobalVariables.mMediaProjection = mMediaProjection;
    }

}
