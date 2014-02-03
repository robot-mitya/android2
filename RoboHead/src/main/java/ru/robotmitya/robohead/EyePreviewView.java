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
import org.ros.node.topic.Subscriber;

/**
 * Created by dmitrydzz on 1/30/14.
 */
public class EyePreviewView extends RosCameraPreviewView {
    public static int VIDEO_STARTED = 1;
    public static int VIDEO_STOPPED = 2;

    private Handler mHandler;
    private int mCameraIndex = 1;

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
        return GraphName.of("robot_mitya/eye_node");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        super.onStart(connectedNode);

        mCameraIndex = 1;

        Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber("robot_mitya/eye", std_msgs.String._TYPE);
        subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(final std_msgs.String message) {
                String messageBody = message.getData();
                String command = MessageHelper.getMessageIdentifier(messageBody);
                String value = MessageHelper.getMessageValue(messageBody);
                if (command.contentEquals("I")) {
                    if (value.contentEquals("0010")) {
                        Log.d("Video off");
                        stopVideoStreaming();
                    } else if (value.contentEquals("0011")) {
                        Log.d("Front camera on");
                        mCameraIndex = 1;
                        startVideoStreaming();
                    } else if (value.contentEquals("0012")) {
                        Log.d("Back camera on");
                        mCameraIndex = 0;
                        startVideoStreaming();
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

    public void startVideoStreaming() {
        stopVideoStreaming();
        final int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras == 0) {
            return;
        }

        // Start of video streaming is delayed.
        final Handler h = new Handler(Looper.getMainLooper());
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                if ((mCameraIndex >= 0) && (mCameraIndex < numberOfCameras)) {
                    setCamera(Camera.open(mCameraIndex));
                }
            }
        };
        h.postDelayed(r, 1000);

        Message message = new Message();
        message.arg1 = VIDEO_STARTED;
        mHandler.sendMessage(message);
    }

    public void stopVideoStreaming() {
        releaseCamera();

        Message message = new Message();
        message.arg1 = VIDEO_STOPPED;
        mHandler.sendMessage(message);
    }
}
