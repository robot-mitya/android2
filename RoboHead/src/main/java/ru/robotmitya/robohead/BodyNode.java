/**
 * This node implements connection and communication functions with robo_body via Bluetooth.
 */
package ru.robotmitya.robohead;

import android.util.Log;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import std_msgs.String;

/**
 * Created by dmitrydzz on 1/27/14.
 *
 */
public class BodyNode implements NodeMain {
    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("robot_mitya/body_node");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber("robot_mitya/command", std_msgs.String._TYPE);
        subscriber.addMessageListener(new MessageListener<String>() {
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
}
