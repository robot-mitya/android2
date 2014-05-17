package ru.robotmitya.robohead;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;

import org.ros.android.view.camera.RosCameraPreviewView;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import java.util.List;

import ru.robotmitya.robocommonlib.AppConst;
import ru.robotmitya.robocommonlib.Log;
import ru.robotmitya.robocommonlib.MessageHelper;
import ru.robotmitya.robocommonlib.RoboState;
import ru.robotmitya.robocommonlib.Rs;

/**
 * Created by dmitrydzz on 1/30/14.
 *
 */
public class EyePreviewView extends RosCameraPreviewView {
    public static final String BROADCAST_CAMERA_SETTINGS_NAME = "ru.robotmitya.robohead.CAMERA_SETTINGS";

    public static int VIDEO_STARTED = 1;
    public static int VIDEO_STOPPED = 2;

    private Handler mHandler;

    private Publisher<std_msgs.String> mBoardPublisher;
    private Publisher<std_msgs.String> mHeadStatePublisher;

    private BroadcastReceiver mCameraSettingsBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final short selectedCamera = (short)SettingsFragment.getCameraIndex();
            final short frontCamera = (short)SettingsFragment.getFrontCameraIndex();
            final short backCamera = (short)SettingsFragment.getBackCameraIndex();
            RoboState.setSelectedCamIndex(selectedCamera);
            RoboState.setFrontCamIndex(frontCamera);
            RoboState.setBackCamIndex(backCamera);

            short value;
            if (selectedCamera == frontCamera) {
                value = Rs.Instruction.CAMERA_FRONT_ON;
                startVideoStreaming(selectedCamera);
            } else if (selectedCamera == backCamera) {
                value = Rs.Instruction.CAMERA_BACK_ON;
                startVideoStreaming(selectedCamera);
            } else {
                value = Rs.Instruction.CAMERA_OFF;
                stopVideoStreaming();
            }
            publishToBoard(MessageHelper.makeMessage(Rs.Instruction.ID, value));
        }
    };

    public EyePreviewView(Context context) {
        super(context);
    }

    public EyePreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EyePreviewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setHandler(final Handler handler) {
        mHandler = handler;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(AppConst.RoboHead.EYE_NODE);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        super.onStart(connectedNode);

        mBoardPublisher = connectedNode.newPublisher(AppConst.RoboBoard.BOARD_TOPIC, std_msgs.String._TYPE);
        mHeadStatePublisher = connectedNode.newPublisher(AppConst.RoboHead.HEAD_STATE_TOPIC, std_msgs.String._TYPE);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                mCameraSettingsBroadcastReceiver, new IntentFilter(BROADCAST_CAMERA_SETTINGS_NAME));

        Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber(AppConst.RoboHead.EYE_TOPIC, std_msgs.String._TYPE);
        subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(final std_msgs.String message) {
                String messageBody = message.getData();
                Log.messageReceived(EyePreviewView.this, messageBody);
                String command = MessageHelper.getMessageIdentifier(messageBody);
                short value = (short)MessageHelper.getMessageIntegerValue(messageBody);
                if (command.contentEquals(Rs.Instruction.ID)) {
                    if (value == Rs.Instruction.CAMERA_OFF) {
                        Log.d(EyePreviewView.this, "video off");
                        stopVideoStreaming();
                        publishToHeadState(messageBody);
                    } else if (value == Rs.Instruction.CAMERA_BACK_ON) {
                        Log.d(EyePreviewView.this, "back camera is turned on");
                        startVideoStreaming(SettingsFragment.getBackCameraIndex());
                        publishToHeadState(messageBody);
                    } else if (value == Rs.Instruction.CAMERA_FRONT_ON) {
                        Log.d(EyePreviewView.this, "front camera is turned on");
                        startVideoStreaming(SettingsFragment.getFrontCameraIndex());
                        publishToHeadState(messageBody);
                    } else if (value == Rs.Instruction.STATE_REQUEST) {
                        String messageToBoard;
                        if (RoboState.getSelectedCamIndex() == SettingsFragment.getFrontCameraIndex()) {
                            messageToBoard = MessageHelper.makeMessage(Rs.Instruction.ID, Rs.Instruction.CAMERA_FRONT_ON);
                        } else if (RoboState.getSelectedCamIndex() == SettingsFragment.getBackCameraIndex()) {
                            messageToBoard = MessageHelper.makeMessage(Rs.Instruction.ID, Rs.Instruction.CAMERA_BACK_ON);
                        } else {
                            messageToBoard = MessageHelper.makeMessage(Rs.Instruction.ID, Rs.Instruction.CAMERA_OFF);
                        }
                        publishToBoard(messageToBoard);
                    }
                }
            }
        });
    }

    @Override
    public void onShutdown(Node node) {
        super.onShutdown(node);
        stopVideoStreaming();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mCameraSettingsBroadcastReceiver);
    }

    public void startVideoStreaming(final int cameraIndex) {
        final int numberOfCameras = Camera.getNumberOfCameras();
        if ((numberOfCameras == 0) || (cameraIndex < 0) || (cameraIndex >= numberOfCameras)) {
            return;
        }

        // Start of video streaming is delayed.
        final Handler h = new Handler(Looper.getMainLooper());
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                if ((cameraIndex >= 0) && (cameraIndex < numberOfCameras)) {
                    releaseCamera();
                    setCamera(Camera.open(cameraIndex));
                }
            }
        };
        h.postDelayed(r, 1000);

        Message message = new Message();
        message.arg1 = VIDEO_STARTED;
        message.arg2 = cameraIndex;
        mHandler.sendMessage(message);
    }

    public void stopVideoStreaming() {
        releaseCamera();

        Message message = new Message();
        message.arg1 = VIDEO_STOPPED;
        message.arg2 = -1;
        mHandler.sendMessage(message);
    }

    private void publishToBoard(final String command) {
        std_msgs.String message = mBoardPublisher.newMessage();
        message.setData(command);
        mBoardPublisher.publish(message);

        Log.messagePublished(this, mBoardPublisher.getTopicName().toString(), command);
    }

    private void publishToHeadState(final String command) {
        std_msgs.String message = mHeadStatePublisher.newMessage();
        message.setData(command);
        mHeadStatePublisher.publish(message);

        Log.messagePublished(this, mHeadStatePublisher.getTopicName().toString(), command);
    }

    protected Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        for (int i = 0; i < sizes.size(); i++) {
            Camera.Size size = sizes.get(i);
            Log.d(this, "++++++ " + i + ": " + size.width + "x" + size.height);
        }

        Camera.Size result;
        result = super.getOptimalPreviewSize(sizes, width, height);
        Log.d(this, "++++++ optimal: " + result.width + "x" + result.height);
//        result = sizes.get(9);
        result = sizes.get(5);
        return result;
    }
}
