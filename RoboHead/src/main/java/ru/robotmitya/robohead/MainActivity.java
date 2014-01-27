package ru.robotmitya.robohead;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.android.view.camera.RosCameraPreviewView;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Subscriber;

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
        NodeConfiguration nodeConfiguration =
                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(rosCameraPreviewView, nodeConfiguration);

        NodeMain myNode = new NodeMain() {
            @Override
            public GraphName getDefaultNodeName() {
                return GraphName.of("robot_mitya/my_node");
            }

            @Override
            public void onStart(ConnectedNode connectedNode) {
                Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber("topicMitya", std_msgs.String._TYPE);
                subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
                    @Override
                    public void onNewMessage(final std_msgs.String message) {
                        Log.i("Mitya", message.getData());
                    }
                });
            }

            @Override
            public void onShutdown(Node node) {
            }

            @Override
            public void onShutdownComplete(Node node) {
            }

            @Override
            public void onError(Node node, Throwable throwable) {
            }
        };
        nodeMainExecutor.execute(myNode, nodeConfiguration);
    }

}
