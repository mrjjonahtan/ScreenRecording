package com.jonathan.screenrecording

import android.Manifest
import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val instance by lazy { this }
    private val Tag: String = "MainActivity"

    private var isHD: Boolean = false
    private var watchAndSaveStatus: Int = 1

    private val REQUESTCODE_STORAGE = 1

    private var width: Int = 0
    private var height: Int = 0
    private var fPath: String = ""

    private var mProjectionManager: MediaProjectionManager? = null
    private var mediaProjection: MediaProjection? = null
    private var captureIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mInit()

        mainfun()
    }

    fun mainfun() {



    }

    fun mInit() {
        getScreenBaseInfo()

        fPath = Tools().getDiskCacheDir(instance)

        rb_sh.setOnClickListener {
            isHD = false
        }

        rb_hd.setOnClickListener {
            isHD = true
        }

        rb_save.setOnClickListener {
            watchAndSaveStatus = 1
        }
        rb_watch.setOnClickListener {
            watchAndSaveStatus = 2
        }
        rb_was.setOnClickListener {
            watchAndSaveStatus = 3
        }

        btn_start.setOnClickListener {
            startRecording()
        }

        btn_stop.setOnClickListener {
            stopRecording()
        }

    }

    private fun startRecording() {


    }

    private fun stopRecording() {

    }


    /**
     * 获取屏幕相关数据
     */
    private fun getScreenBaseInfo() {

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        width = metrics.widthPixels
        height = metrics.heightPixels
    }


}
