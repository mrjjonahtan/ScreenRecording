package com.jonathan.screenrecording

import android.app.Service
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.os.IBinder
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*


public class MediaRecordService : Service() {
    private val Tag = "MediaRecordService"

    private var width: Int = 0
    private var height: Int = 0
    private var watchAndSaveStatus: Int = 0
    private var mScreenDensity: Int = 0

    private var fPath: String = ""

    private var isHD: Boolean = false
    private var isSound: Boolean = false

    private var mMediaRecorder: MediaRecorder? = null
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null

    private val FRAME_RATE_HD = 60 // 60 fps
    private val FRAME_RATE_SD = 30 // 30 fps

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val simpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss")
        val date = Date(System.currentTimeMillis())

        width = intent!!.getIntExtra("width", 0)
        height = intent!!.getIntExtra("height", 0)
        watchAndSaveStatus = intent!!.getIntExtra("watchAndSaveStatus", 0)
        mScreenDensity = intent!!.getIntExtra("mScreenDensity", 1)
        fPath = intent!!.getStringExtra("fPath") + "/" + simpleDateFormat.format(date) + ".mp4"
        isHD = intent!!.getBooleanExtra("isHD", false)
        isSound = intent!!.getBooleanExtra("isSound", false)

        //val resultCode = intent!!.getIntExtra("resultCode",-1)
        //val data = intent!!.getParcelableExtra<Intent>("data")
        mMediaProjection = GlobalVariables().getmMediaProjection()

        try {

            initMediaRecorder()
            mainfun()
        } catch (err: Exception) {
            Log.e(Tag, err.message)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    fun mainfun() {

        mVirtualDisplay = mMediaProjection!!.createVirtualDisplay(Tag + "-display", width, height, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, mMediaRecorder!!.getSurface(), null, null)
        mMediaRecorder!!.start()
    }

    fun initMediaRecorder() {

        mMediaRecorder = MediaRecorder()
        mMediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mMediaRecorder!!.setOutputFile(fPath)
        mMediaRecorder!!.setVideoSize(width, height)
        mMediaRecorder!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264)

        // Sound
        if (isSound) {
            mMediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        }

        // sd hd
        if (isHD) {
            mMediaRecorder!!.setVideoFrameRate(FRAME_RATE_HD)
            mMediaRecorder!!.setVideoEncodingBitRate(width * height * 5)

        } else {
            mMediaRecorder!!.setVideoFrameRate(FRAME_RATE_SD)
            mMediaRecorder!!.setVideoEncodingBitRate(width * height)
        }

        try {

            mMediaRecorder!!.prepare()
        } catch (er: Exception) {
            Log.e(Tag, er.message)
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        if (mVirtualDisplay != null) {
            mVirtualDisplay!!.release()
            mVirtualDisplay = null
        }
        if (mMediaRecorder != null) {
            mMediaRecorder!!.setOnErrorListener(null)
            mMediaProjection!!.stop()
            mMediaRecorder!!.reset()
            mMediaRecorder!!.release()
        }
        if (mMediaProjection != null) {
            mMediaProjection!!.stop()
            mMediaProjection = null
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}
