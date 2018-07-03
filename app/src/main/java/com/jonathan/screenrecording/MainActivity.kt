package com.jonathan.screenrecording

import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val instance by lazy { this }
    private val Tag: String = "MainActivity"
    private val REQUEST_CODE = 0x01

    private var isHD: Boolean = false
    private var watchAndSaveStatus: Int = 1

    private val REQUESTCODE_STORAGE = 1

    private var width: Int = 0
    private var height: Int = 0
    private var mScreenDensity: Int = 0
    private var fPath: String = ""

    private var mProjectionManager: MediaProjectionManager? = null
    private var mediaProjection: MediaProjection? = null
    private var captureIntent: Intent? = null

    private val callback = object:SurfaceHolder.Callback{
        //销毁时
        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            stopRecording()
        }

        //被创建时
        override fun surfaceCreated(holder: SurfaceHolder?) {

        }

        //大小发生改变时
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mInit()

        //mainfun()
    }

    fun mainfun() {


    }

    fun mInit() {
        getScreenBaseInfo()

        fPath = Tools().getDiskCacheDir(instance)
        mProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager?
        captureIntent = mProjectionManager!!.createScreenCaptureIntent()

        surfaceView.holder.addCallback(callback)

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
            tv_hit.setText("录制中...")
            Log.d(Tag, "file path:" + fPath)
        }

        btn_stop.setOnClickListener {
            stopRecording()
            tv_hit.setText("录制已停止。")
        }

    }

    private fun startRecording() {

        startActivityForResult(captureIntent, REQUEST_CODE)
    }

    private fun stopRecording() {
        //val service: Intent = Intent(instance, MediaRecordService::class.java)
        val service: Intent = Intent(instance, ScreenRecordService::class.java)

        stopService(service)
    }


    /**
     * 获取屏幕相关数据
     */
    private fun getScreenBaseInfo() {

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        width = metrics.widthPixels
        height = metrics.heightPixels
        mScreenDensity = metrics.densityDpi
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {

            mediaProjection = mProjectionManager!!.getMediaProjection(resultCode,data)
            if (mediaProjection == null) {
                Log.e(Tag, "media projection is null")
                return
            }

            GlobalVariables().setmMediaProjection(mediaProjection)
            GlobalVariables().surface = surfaceView.holder.surface

            //val service: Intent = Intent(instance, MediaRecordService::class.java)//只有存
            val service: Intent = Intent(instance, ScreenRecordService::class.java)//全有

            service.putExtra("width", width)
            service.putExtra("height", height)
            service.putExtra("fPath", fPath)
            //service.putExtra("intent",data)
            //service.putExtra("resultCode",resultCode)
            service.putExtra("mScreenDensity",mScreenDensity)
            service.putExtra("isHD", isHD)
            service.putExtra("isSound", cb_sound.isChecked)
            service.putExtra("watchAndSaveStatus", watchAndSaveStatus)

            startService(service)

        }

    }


}
