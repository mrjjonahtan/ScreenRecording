package com.jonathan.screenrecording;

import android.media.projection.MediaProjection;
import android.view.Surface;
import android.view.SurfaceHolder;

public class GlobalVariables {
    private static MediaProjection mMediaProjection;
    private static SurfaceHolder surfaceHolder;

    public SurfaceHolder getSurfaceHolder() {
        return surfaceHolder;
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        GlobalVariables.surfaceHolder = surfaceHolder;
    }

    public MediaProjection getmMediaProjection() {
        return mMediaProjection;
    }

    public void setmMediaProjection(MediaProjection mMediaProjection) {
        GlobalVariables.mMediaProjection = mMediaProjection;
    }

}
