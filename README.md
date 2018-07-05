# ScreenRecording
工程：Android studio 工程

语言：kotlin 和 java

功能：录屏 存mp4

大致流程如下：
(MediaRecordService)

MediaProjectionManager -> getMediaProjection() -> MediaProjection -> VirtualDisplay -> MediaRecorder -> getSurface() -> surface


其中MediaProjectionManager用于向用户显示一个弹窗，请求获取屏幕镜像的权限。

此弹窗的操作结果会通过Activity的onActivityResult返回，RESULT_OK表示用户已经给了权限。

得到权限后，可以调用MediaProjectionManager的getMediaProjection方法获取MediaProjection实例，并用此实例创建一个VirtualDisplay，这就是我们的屏幕镜像。

创建VirtualDisplay时需要一个surface做出输出缓存，即存放即将显示在屏幕上的数据。

自安卓5.1以后，系统为MediaRecorder提供多了一种新的图形输入方式，

我们可以通过其实例方法getSurface得到一个surface作为输入缓存。如此结合起来，

在录屏的场景中，我们可以先从MediaRecorder中得到一个输入缓存，并将这个缓存当做VirtualDisplay的输出缓存，形成I/O流通、内存共享。

（以上是在一个博主的博客中看到，可惜链接弄丢了）

(ScreenRecordService)

MediaCodec -> configure(MediaFormat) -> Surface

Surface

MediaMuxer

MediaProjection -> createVirtualDisplay -> VirtualDisplay

VirtualDisplay

其中首先创建MediaCodec在创建过程中需要MediaFormat来设置编码过程中的一些属性，然后通过MediaCodec.createInputSurface函数

创建Surface，通过start启动解码器

创建MediaMuxer

使用MediaProjection.createVirtualDisplay创建VirtualDisplay

通过MediaCodec.dequeueOutputBuffer获取可用缓冲区，获取时需要事先已经声明了BufferInfo缓冲区，会返回int数值

当获取值为MediaCodec.INFO_OUTPUT_FORMAT_CHANGED时

通过MediaCodec.getOutputFormat()创建MediaFormat为MediaMuxer添加（使用addTrack函数添加）MediaFormat，启动MediaMuxer

当换缓冲区的值为MediaCodec.INFO_TRY_AGAIN_LATER时表示超时

当换缓冲区的值大于等于0时，表示有效输出

使用MediaCodec.getOutputBuffer获取帧数据，

但是在使用之前需要判断BufferInfo.flags，当flags为MediaCodec.BUFFER_FLAG_CODEC_CONFIG则缓冲区里的内容为MediaCodec初始化数据

而不是视频数据，如果不是才可以使用MediaMuxer.writeSampleData合成视频文件

使用完缓冲区的数据之后需要MediaCodec.releaseOutputBuffer释放缓冲区

最后使用完成之后别忘了释放和关闭

MediaCodec.stop()
MediaCodec.release()
MediaCodec = null

VirtualDisplay.release()

MediaProjection!!.stop()

MediaMuxer.stop()
MediaMuxer.release()
MediaMuxer = null

（提示：只是实时存视频帧数据，没有在SurfaceView显示）