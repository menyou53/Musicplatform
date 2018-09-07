package jni;

/**
 * Created by youmen on 2018/4/22.
 */

public class FFmpegJni {
    static {
        System.loadLibrary("avutil-55");
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avformat-57");
        System.loadLibrary("avdevice-57");
        System.loadLibrary("swresample-2");
        System.loadLibrary("swscale-4");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("ffmpeg");
        System.loadLibrary("x264-155");


    }
    public native static int run(String[] commands);
    private native void initEncoder(int numChannels, int sampleRate, int bitRate, int mode, int quality);
    private native void destroyEncoder();
    private native int encodeFile(String sourcePath, String targetPath);

}




