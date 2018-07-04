package com.jonathan.screenrecording

import android.app.Service
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.projection.MediaProjection
import android.os.IBinder
import android.util.Log
import android.view.Surface
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


/**
 * 可以只存，可以边看边存，可以只看不存
 */
class ScreenRecordService : Service() {
    val Tag: String = "ScreenRecordService"

    // parameters for the encoder
    private val MIME_TYPE = "video/avc" // H.264 Advanced

    // Video Coding
    private val FRAME_RATE_HD = 60 // 60 fps
    private val FRAME_RATE_SD = 30 // 30 fps
    private val IFRAME_INTERVAL = 1 // 1 seconds between

    // I-frames
    private val TIMEOUT_US = 10

    private var width: Int = 0
    private var height: Int = 0
    private var watchAndSaveStatus: Int = 0
    private var mScreenDensity: Int = 0
    private var mVideoTrackIndex: Int = -1

    private var fPath: String = ""

    private var isHD: Boolean = false
    private var isSound: Boolean = false
    private var mMuxerStarted: Boolean = false


    private var mMediaProjection: MediaProjection? = null
    private var mMC: MediaCodec? = null
    private var mSurface: Surface? = null
    private var mMuxer: MediaMuxer? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mThread: Thread? = null
    private var mQuit: AtomicBoolean = AtomicBoolean(false)
    private var mBufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


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

        mMediaProjection = GlobalVariables().getmMediaProjection()
        //mSurface = GlobalVariables().surface

        mainfun()
        return super.onStartCommand(intent, flags, startId)
    }

    fun mainfun() {

        try {
            prepareEncoder()
            mMuxer = MediaMuxer(fPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

            mVirtualDisplay = mMediaProjection!!.createVirtualDisplay(Tag + "-display", width, height, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, mSurface, null, null)

            recordVirtualDisplay()
            //release()

        } catch (e: Exception) {
            Log.e(Tag, e.message)
        }
    }

    /**
     * stop task
     */
    fun quit() {
        mQuit.set(true)
    }

    fun recordVirtualDisplay() {

        mThread = Thread(Runnable {
            kotlin.run {

                while (!mQuit.get()) {
                    val index = mMC!!.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US.toLong())

                    if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        // 后续输出格式变化
                        resetOutputFormat()
                    } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        //超时
                    } else if (index >= 0) {
                        // 有效输出
                        if (!mMuxerStarted) {
                            throw IllegalStateException("MediaMuxer dose not call addTrack(format) ")
                        }

                        encodeToVideoTrack(index)

                        //如果缓冲区里的可展示时间>当前视频播放的进度，就休眠一下
                        //sleepRender(mBufferInfo, System.currentTimeMillis())

                        mMC!!.releaseOutputBuffer(index, true)

                    }
                }
            }
        })
        mThread!!.start()
    }

    //延迟渲染
    private fun sleepRender(audioBufferInfo: MediaCodec.BufferInfo, startMs: Long) {
        while (audioBufferInfo.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                break
            }

        }
    }

    /**
     * 硬解码获取实时帧数据并写入mp4文件
     */
    fun encodeToVideoTrack(index: Int) {
        // 获取到的实时帧视频数据
        var encodedData = mMC!!.getOutputBuffer(index)

        if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
            // This indicated that the buffer marked as such contains codec
            // initialization / codec specific data instead of media data.
            // Ignore it.
            return
        }

        if (mBufferInfo.size == 0) {
            encodedData = null
        }

        if (encodedData != null) {
            //将编码的样本写入复用器
            mMuxer!!.writeSampleData(mVideoTrackIndex, encodedData, mBufferInfo)
        }

    }

    fun resetOutputFormat() {
        // should happen before receiving buffers, and should only happen
        // once

        if (mMuxerStarted) {
            throw IllegalStateException("output format already changed!")
        }

        val mMF = mMC!!.getOutputFormat()

        mVideoTrackIndex = mMuxer!!.addTrack(mMF)
        mMuxer!!.start()
        mMuxerStarted = true

    }


    fun prepareEncoder() {
        var mMF: MediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, width, height)
        mMF.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        mMF.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL)

        // sd hd
        if (isHD) {
            mMF.setInteger(MediaFormat.KEY_BIT_RATE, FRAME_RATE_HD)
            mMF.setInteger(MediaFormat.KEY_FRAME_RATE, width * height * 5)
        } else {
            mMF.setInteger(MediaFormat.KEY_BIT_RATE, FRAME_RATE_SD)
            mMF.setInteger(MediaFormat.KEY_FRAME_RATE, width * height)
        }

        mMC = MediaCodec.createEncoderByType(MIME_TYPE)
        mMC!!.configure(mMF, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mSurface = mMC!!.createInputSurface()

        mMC!!.start()
    }

    private fun release() {
        if(!mQuit.get()){
            quit()
        }
        when(mThread!!.isAlive){ }

        if (mMC != null) {
            mMC!!.stop()
            mMC!!.release()
            mMC = null
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay!!.release()
        }
        if (mMediaProjection != null) {
            mMediaProjection!!.stop()
        }
        if (mMuxer != null) {
            mMuxer!!.stop()
            mMuxer!!.release()
            mMuxer = null
        }
    }

    override fun onDestroy() {
        //quit()
        release()
        super.onDestroy()
    }

}
