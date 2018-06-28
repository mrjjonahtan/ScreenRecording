package com.jonathan.screenrecording

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    val Tag: String = "MainActivity"

    var isHD: Boolean = false
    var watchAndSaveStatus: Int = 1

    var width: Int = 0
    var height: Int = 0
    var fPath:String = ""

    var mProjectionManager: MediaProjectionManager? = null
    var mediaProjection: MediaProjection? = null
    var captureIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mInit()
    }

    init {
        fPath = this.getFilesDir().getAbsolutePath().toString()

        getScreenBaseInfo()
    }

    fun mInit() {
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
