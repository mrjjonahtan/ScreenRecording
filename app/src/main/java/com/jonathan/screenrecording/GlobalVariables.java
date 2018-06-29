package com.jonathan.screenrecording;

import android.media.projection.MediaProjection;

public class GlobalVariables {
    private static MediaProjection mMediaProjection;

    public MediaProjection getmMediaProjection() {
        return mMediaProjection;
    }

    public void setmMediaProjection(MediaProjection mMediaProjection) {
        GlobalVariables.mMediaProjection = mMediaProjection;
    }

}
