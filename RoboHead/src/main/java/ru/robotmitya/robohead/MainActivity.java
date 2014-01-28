package ru.robotmitya.robohead;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.android.view.camera.RosCameraPreviewView;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity {

    private RosCameraPreviewView rosCameraPreviewView;

    public MainActivity() {
        super("Robot Mitya\'s ticker", "Robot Mitya");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Settings.initialize(this);

        Handler handler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                String message = (String) msg.obj;
                String command = MessageHelper.getMessageIdentifier(message);
                String value = MessageHelper.getMessageValue(message);
                Log.i("Message: " + message);
                Log.i("Command: " + command + ", value: " + value);
            }
        };

        BluetoothHelper.initialize(this);
        if (!BluetoothHelper.getBluetoothAdapterIsEnabled()) {
            return;
        }
        BluetoothHelper.start(handler);

        rosCameraPreviewView = (RosCameraPreviewView) findViewById(R.id.ros_camera_preview_view);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        int cameraId;
        if (Camera.getNumberOfCameras() > 1) {
            cameraId = 1;
        } else {
            cameraId = 0;
        }
        rosCameraPreviewView.setCamera(Camera.open(cameraId));
        rosCameraPreviewView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(MainActivity.this, Settings.class));
                return true;
            }
        });
        NodeConfiguration nodeConfiguration =
                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(rosCameraPreviewView, nodeConfiguration);

        BodyNode bodyNode = new BodyNode();
        nodeMainExecutor.execute(bodyNode, nodeConfiguration);
    }

}
