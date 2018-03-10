package com.ford.openxc.webcam.webcam;

import android.graphics.Bitmap;

import com.socks.library.KLog;

import java.io.File;

public class NativeWebcam implements IWebcam {

    private static String TAG = "NativeWebcam";
    private static final int DEFAULT_IMAGE_WIDTH = 640;
    private static final int DEFAULT_IMAGE_HEIGHT = 480;

    private Bitmap mBitmap;
    private int mWidth;
    private int mHeight;


    private native int startCamera(String deviceName, int width, int height);
    private native void processCamera();
    private native boolean cameraAttached();
    private native void stopCamera();
    private native void loadNextFrame(Bitmap bitmap);

    static {
        System.loadLibrary("webcam");
    }

    public NativeWebcam(String deviceName, int width, int height) {
        mWidth = width;
        mHeight = height;
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        connect(deviceName, mWidth, mHeight);
    }

    public NativeWebcam(String deviceName) {
        this(deviceName, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
    }

    private void connect(String deviceName, int width, int height) {
        boolean deviceReady = true;

        File deviceFile = new File(deviceName);
        if(deviceFile.exists()) {
            if(!deviceFile.canRead()) {
                KLog.w(TAG, "Insufficient permissions on " + deviceName +
                        " -- does the app have the CAMERA permission?");
                //需要有读权限。或通过命令行执行chmod命令。否则会出现预览黑屏
                try {
                    Process su = Runtime.getRuntime().exec("/system/xbin/su");
                    String cmd = "";
                    for (int i = 0; i < 2; ++i)
                        cmd += String.format("chmod 777 dev/video%d\n", i);
                    cmd += "exit\n";
                    su.getOutputStream().write(cmd.getBytes(), 0, cmd.getBytes().length);
                    su.getOutputStream().flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(!deviceFile.canRead()) {
                KLog.w(TAG, "Insufficient permissions on " + deviceName +
                        " -- does the app have the CAMERA permission?");
                deviceReady = false;
            }else{
                deviceReady = true;
            }
        } else {
            KLog.w(TAG, deviceName + " does not exist");
            deviceReady = false;
        }

        if(deviceReady) {
            KLog.w(TAG, "Preparing camera with device name " + deviceName);
            startCamera(deviceName, width, height);
        }
    }

    public Bitmap getFrame() {
        loadNextFrame(mBitmap);
        return mBitmap;
    }

    public void stop() {
        stopCamera();
    }

    public boolean isAttached() {
        return cameraAttached();
    }
}
