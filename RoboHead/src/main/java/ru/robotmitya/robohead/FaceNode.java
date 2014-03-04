package ru.robotmitya.robohead;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

/**
 * Created by dmitrydzz on 3/3/14.
 *
 */
public class FaceNode implements NodeMain {
    public static String BROADCAST_NAME = "ru.robot-mitya.FACE-CHANGE";
    public static String BROADCAST_EXTRA_NAME = "message";

    private Context mContext;

    public FaceNode(final Context context) {
        mContext = context;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("robot_mitya/face_node");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber("robot_mitya/face", std_msgs.String._TYPE);
        subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(std_msgs.String message) {
                String messageBody = message.getData();

                Log.d("Message received in FaceNode: " + messageBody);

                Intent intent = new Intent(BROADCAST_NAME);
                intent.putExtra(BROADCAST_EXTRA_NAME, messageBody);
                LocalBroadcastManager.getInstance(FaceNode.this.mContext).sendBroadcast(intent);
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
}
