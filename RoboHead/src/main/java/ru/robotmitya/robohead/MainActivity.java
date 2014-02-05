package ru.robotmitya.robohead;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity {
    private EyePreviewView mEyePreviewView;
    private BluetoothAdapter mBluetoothAdapter;

    private Handler mEyeNodeHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mEyePreviewView == null) {
                return true;
            }
            if (msg.arg1 == EyePreviewView.VIDEO_STARTED) {
                mEyePreviewView.setVisibility(View.VISIBLE);
            } else if (msg.arg1 == EyePreviewView.VIDEO_STOPPED) {
                mEyePreviewView.setVisibility(View.GONE);
            }

            return true;
        }
    });

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

        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(MainActivity.this, Settings.class));
                return true;
            }
        };

        mEyePreviewView = (EyePreviewView) findViewById(R.id.eye_preview_view);
        mEyePreviewView.setHandler(mEyeNodeHandler);
        mEyePreviewView.setOnLongClickListener(onLongClickListener);

        LinearLayout rootLinearLayout = (LinearLayout) findViewById(R.id.root_linear_layout);
        rootLinearLayout.setOnLongClickListener(onLongClickListener);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mEyePreviewView.startVideoStreaming(Settings.getCameraIndex());
    }

    @Override
    protected void onStop() {
        mEyePreviewView.stopVideoStreaming();
        super.onStop();
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

        initBluetoothBodyNode(nodeMainExecutor, nodeConfiguration);
    }

    private void initBluetoothBodyNode(final NodeMainExecutor nodeMainExecutor,
                                       final NodeConfiguration nodeConfiguration) {
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
            BluetoothBodyNode bluetoothBodyNode = new BluetoothBodyNode(this, mBluetoothAdapter);
            nodeMainExecutor.execute(bluetoothBodyNode, nodeConfiguration);
        }
    }
}
