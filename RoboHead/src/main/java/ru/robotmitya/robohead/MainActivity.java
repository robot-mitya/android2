package ru.robotmitya.robohead;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity {

    private EyePreviewView mEyePreviewView;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mBluetoothHandler;

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

        //todo Избавиться от mBluetoothHandler. Надо сразу публиковать пришедшие от Мити сообщения.
        mBluetoothHandler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                String message = (String) msg.obj;
                String command = MessageHelper.getMessageIdentifier(message);
                String value = MessageHelper.getMessageValue(message);
                Log.i("Message: " + message);
                Log.i("Command: " + command + ", value: " + value);
            }
        };

        mEyePreviewView = (EyePreviewView) findViewById(R.id.eye_preview_view);
        mEyePreviewView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(MainActivity.this, Settings.class));
                return true;
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void startMasterChooser() {
        Intent data = new Intent();
        data.putExtra("ROS_MASTER_URI", Settings.getMasterUri());
        data.putExtra("NEW_MASTER", true);
        data.putExtra("ROS_MASTER_PRIVATE", false);
        onActivityResult(0, RESULT_OK, data);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeConfiguration nodeConfiguration =
                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(mEyePreviewView, nodeConfiguration);

        if (mBluetoothAdapter == null) {
            Log.e(getString(R.string.error_no_bluetooth_adapter));
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(
                            MainActivity.this,
                            MainActivity.this.getString(R.string.error_no_bluetooth_adapter),
                            Toast.LENGTH_LONG)
                    .show();
                }
            });
        } else if (!mBluetoothAdapter.isEnabled()) {
            Log.e(getString(R.string.error_bluetooth_adapter_is_not_activated));
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(
                            MainActivity.this,
                            MainActivity.this.getString(R.string.error_bluetooth_adapter_is_not_activated),
                            Toast.LENGTH_LONG)
                    .show();
                }
            });
        } else {
            BodyNode bodyNode = new BodyNode(this, mBluetoothAdapter, mBluetoothHandler);
            nodeMainExecutor.execute(bodyNode, nodeConfiguration);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startVideoStreaming();
    }

    @Override
    protected void onStop() {
        stopVideoStreaming();
        super.onStop();
    }

    private void startVideoStreaming() {
        // Start of video streaming is delayed.
        Handler h = new Handler(Looper.getMainLooper());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                int cameraId = Camera.getNumberOfCameras() > 1 ? 1 : 0;
                mEyePreviewView.setCamera(Camera.open(cameraId));
            }
        };
        h.postDelayed(r, 1000);
    }

    private void stopVideoStreaming() {
        mEyePreviewView.releaseCamera();
    }
}
