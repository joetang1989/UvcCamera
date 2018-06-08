package cn.cloudwalk.dualcamera2;

import android.hardware.usb.UsbDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import com.serenegiant.usbcamera.AbstractUVCCameraHandler;
import com.serenegiant.usbcamera.UVCCameraHelper;
import com.serenegiant.widget.CameraViewInterface;

public class MainActivity extends AppCompatActivity implements  CameraViewInterface.Callback {
    private boolean isPreview1;
    private boolean isRequest1;
    private boolean isPreview2;
    private boolean isRequest2;


    private UVCCameraHelper mCameraHelper1;
    private UVCCameraHelper mCameraHelper2;
    private CameraViewInterface mUVCCameraView1;
    private CameraViewInterface mUVCCameraView2;


    private UVCCameraHelper.OnMyDevConnectListener listener1 = new UVCCameraHelper.OnMyDevConnectListener() {

        @Override
        public void onAttachDev(UsbDevice device) {
            if (mCameraHelper1 == null || mCameraHelper1.getUsbDeviceCount() == 0) {
                showShortMsg("check no usb camera");
                return;
            }
            // request open permission
            if (!isRequest1) {
                isRequest1 = true;
                if (mCameraHelper1 != null) {
//                    mCameraHelper1.requestPermission(11391,50081);
                    mCameraHelper1.requestPermission(3141,51457);//A2
                }
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            if (isRequest1) {
                isRequest1= false;
                mCameraHelper1.closeCamera();
                showShortMsg(device.getDeviceName() + " is out");
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            if (!isConnected) {
                showShortMsg("fail to connect,please check resolution params");
                isPreview1 = false;
            } else {
                isPreview1 = true;
                showShortMsg("connecting device"+device);
            }
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
            showShortMsg("disconnecting device"+device);
        }
    };

    private UVCCameraHelper.OnMyDevConnectListener listener2 = new UVCCameraHelper.OnMyDevConnectListener() {

        @Override
        public void onAttachDev(UsbDevice device) {
            if (mCameraHelper2 == null || mCameraHelper2.getUsbDeviceCount() == 0) {
                showShortMsg("check no usb camera");
                return;
            }
            // request open permission
            if (!isRequest2) {
                isRequest2 = true;
                if (mCameraHelper2 != null) {
//                    mCameraHelper2.requestPermission(11391,50097);
                    mCameraHelper2.requestPermission(3141,51458);//A2
                }

            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            // close camera
            if (isRequest2) {
                isRequest2= false;
                mCameraHelper2.closeCamera();
                showShortMsg(device.getDeviceName() + " is out");
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            if (!isConnected) {
                showShortMsg("fail to connect,please check resolution params");
                isPreview2 = false;
            } else {
                isPreview2 = true;
                showShortMsg("connecting device "+device);
            }
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
            showShortMsg("disconnecting device"+device.toString());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCameraHelper1 = new UVCCameraHelper();
        mCameraHelper2 = new UVCCameraHelper();
        mUVCCameraView1 = (CameraViewInterface) findViewById(R.id.camera_view1);
        mUVCCameraView2 = (CameraViewInterface) findViewById(R.id.camera_view2);

        mUVCCameraView1.setCallback(this);


        mCameraHelper1.initUSBMonitor(this, mUVCCameraView1, listener1);
        mCameraHelper2.initUSBMonitor(this, mUVCCameraView2, listener2);

        mCameraHelper1.setOnPreviewFrameListener(new AbstractUVCCameraHandler.OnPreViewResultListener() {
            @Override
            public void onPreviewResult(byte[] nv21Yuv) {
                Log.e("DDDD","----onPreviewResult1111-----");
            }
        });

        mCameraHelper2.setOnPreviewFrameListener(new AbstractUVCCameraHandler.OnPreViewResultListener() {
            @Override
            public void onPreviewResult(byte[] nv21Yuv) {
                Log.e("DDDD","----onPreviewResult22-----");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // step.2 register USB event broadcast
        if (mCameraHelper1 != null) {
            mCameraHelper1.registerUSB();
        }

        if (mCameraHelper2 != null) {
            mCameraHelper2.registerUSB();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // step.3 unregister USB event broadcast
        if (mCameraHelper1 != null) {
            mCameraHelper1.unregisterUSB();
        }
        if (mCameraHelper2 != null) {
            mCameraHelper2.unregisterUSB();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // step.4 release uvc camera resources
        if (mCameraHelper1 != null) {
            mCameraHelper1.release();
        }
        if (mCameraHelper2 != null) {
            mCameraHelper2.release();
        }
    }

    private void showShortMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }




    public boolean isCameraOpened() {
        return mCameraHelper1.isCameraOpened();
    }

    @Override
    public void onSurfaceCreated(CameraViewInterface view, Surface surface) {

    }

    @Override
    public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isPreview1 && mCameraHelper1.isCameraOpened()) {
            mCameraHelper1.startPreview(mUVCCameraView1);
            isPreview1 = true;
        }
        if (!isPreview2 && mCameraHelper2.isCameraOpened()) {
            mCameraHelper2.startPreview(mUVCCameraView2);
            isPreview2 = true;
        }
    }

    @Override
    public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
        if (isPreview1 && mCameraHelper1.isCameraOpened()) {
            mCameraHelper1.stopPreview();
            mCameraHelper2.stopPreview();
            isPreview1 = false;
        }
    }

}
