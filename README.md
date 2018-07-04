# ScreenRecording
工程：Android studio 工程

语言：kotlin 和 java

功能：录屏

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

MediaProjection

Surface

MediaMuxer

MediaCodec

VirtualDisplay