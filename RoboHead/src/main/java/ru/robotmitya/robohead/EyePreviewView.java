package ru.robotmitya.robohead;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;

import org.ros.android.view.camera.RosCameraPreviewView;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import ru.robotmitya.robocommonlib.AppConst;
import ru.robotmitya.robocommonlib.Log;
import ru.robotmitya.robocommonlib.MessageHelper;
import ru.robotmitya.robocommonlib.RoboState;
import ru.robotmitya.robocommonlib.Rs;

/**
 * Created by dmitrydzz on 1/30/14.
 */
public class EyePreviewView extends RosCameraPreviewView {
    public static int VIDEO_STARTED = 1;
    public static int VIDEO_STOPPED = 2;

    private Handler mHandler;

    private Publisher<std_msgs.String> mBoardPublisher;
    private Publisher<std_msgs.String> mHeadStatePublisher;

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
//                        int cameraIndex = -1;
//                        RoboState.setSelectedCamIndex((short)cameraIndex);
//                        SettingsActivity.setCameraIndex(EyePreviewView.this.getContext(), cameraIndex);
//                        publishToBoard(messageBody);
                    } else if (value == Rs.Instruction.CAMERA_BACK_ON) {
                        Log.d(EyePreviewView.this, "back camera is turned on");
                        startVideoStreaming(SettingsActivity.getBackCameraIndex());
                        publishToHeadState(messageBody);
//                        RoboState.setSelectedCamIndex((short) cameraIndex);
//                        SettingsActivity.setCameraIndex(EyePreviewView.this.getContext(), cameraIndex);
//                        publishToBoard(messageBody);
                    } else if (value == Rs.Instruction.CAMERA_FRONT_ON) {
                        Log.d(EyePreviewView.this, "front camera is turned on");
                        startVideoStreaming(SettingsActivity.getFrontCameraIndex());
                        publishToHeadState(messageBody);
//                        RoboState.setSelectedCamIndex((short) cameraIndex);
//                        SettingsActivity.setCameraIndex(EyePreviewView.this.getContext(), cameraIndex);
//                        publishToBoard(messageBody);
                    } else if (value == Rs.Instruction.STATE_REQUEST) {
                        String messageToBoard;
                        if (RoboState.getSelectedCamIndex() == SettingsActivity.getFrontCameraIndex()) {
                            messageToBoard = MessageHelper.makeMessage(Rs.Instruction.ID, Rs.Instruction.CAMERA_FRONT_ON);
                        } else if (RoboState.getSelectedCamIndex() == SettingsActivity.getBackCameraIndex()) {
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
    }

    public void startVideoStreaming(final int cameraIndex) {
        stopVideoStreaming();
        final int index = cameraIndex;
        final int numberOfCameras = Camera.getNumberOfCameras();
        if ((numberOfCameras == 0) || (index < 0) || (index >= numberOfCameras)) {
            return;
        }

        // Start of video streaming is delayed.
        final Handler h = new Handler(Looper.getMainLooper());
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                if ((index >= 0) && (index < numberOfCameras)) {
                    setCamera(Camera.open(index));
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
}
