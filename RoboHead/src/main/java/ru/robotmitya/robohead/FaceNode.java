package ru.robotmitya.robohead;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import ru.robotmitya.robocommonlib.Log;

/**
 * Created by dmitrydzz on 3/3/14.
 *
 */
public class FaceNode implements NodeMain {
    public static String BROADCAST_FACE_CHANGE_NAME = "ru.robot-mitya.FACE-CHANGE";
    public static String BROADCAST_FACE_CHANGE_EXTRA_NAME = "message";

    public static String BROADCAST_FACE_PATTING = "ru.robot-mitya.FACE-PAT";
    public static String BROADCAST_FACE_PUSH_EYE = "ru.robot-mitya.FACE-EYE-DIG";
    public static String BROADCAST_FACE_PUSH_NOSE = "ru.robot-mitya.FACE-NOSE-DIG";

    private Context mContext;
    private BroadcastReceiver mBroadcastReceiverPatting;
    private BroadcastReceiver mBroadcastReceiverPushEye;
    private BroadcastReceiver mBroadcastReceiverPushNose;

    private Publisher<std_msgs.String> mPublisher;

    public FaceNode(final Context context) {
        mContext = context;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("robot_mitya/face_node");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        mPublisher = connectedNode.newPublisher("robot_mitya/reflex", std_msgs.String._TYPE);

        Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber("robot_mitya/face", std_msgs.String._TYPE);
        subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(std_msgs.String message) {
                String messageBody = message.getData();

                Log.d("Message received in FaceNode: " + messageBody);

                Intent intent = new Intent(BROADCAST_FACE_CHANGE_NAME);
                intent.putExtra(BROADCAST_FACE_CHANGE_EXTRA_NAME, messageBody);
                LocalBroadcastManager.getInstance(FaceNode.this.mContext).sendBroadcast(intent);
            }
        });

        mBroadcastReceiverPatting = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Message received in FaceNode: hair patting");
                publish("M0101");
            }
        };
        LocalBroadcastManager.getInstance(mContext).registerReceiver(
                mBroadcastReceiverPatting, new IntentFilter(FaceNode.BROADCAST_FACE_PATTING));

        mBroadcastReceiverPushEye = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Message received in FaceNode: eye push");
                publish("M0104");
            }
        };
        LocalBroadcastManager.getInstance(mContext).registerReceiver(
                mBroadcastReceiverPushEye, new IntentFilter(FaceNode.BROADCAST_FACE_PUSH_EYE));

        mBroadcastReceiverPushNose = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Message received in FaceNode: nose push");
                publish("M0106");
            }
        };
        LocalBroadcastManager.getInstance(mContext).registerReceiver(
                mBroadcastReceiverPushNose, new IntentFilter(FaceNode.BROADCAST_FACE_PUSH_NOSE));
    }

    @Override
    public void onShutdown(Node node) {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiverPatting);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiverPushEye);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiverPushNose);
    }

    @Override
    public void onShutdownComplete(Node node) {
    }

    @Override
    public void onError(Node node, Throwable throwable) {
    }

    private void publish(String command) {
        if (mPublisher != null) {
            std_msgs.String message = mPublisher.newMessage();
            message.setData(command);
            mPublisher.publish(message);
        }
    }
}
