package ru.robotmitya.robohead;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import geometry_msgs.Vector3;
import ru.robotmitya.robocommonlib.AppConst;
import ru.robotmitya.robocommonlib.Log;

/**
 * Created by dmitrydzz on 4/12/14.
 *
 */
public class HeadJoystickAnalyzerNode implements NodeMain {
    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(AppConst.RoboHead.HEAD_JOYSTICK_ANALYZER_NODE);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Subscriber<geometry_msgs.Twist> subscriber = connectedNode.newSubscriber(AppConst.RoboBoard.HEAD_JOYSTICK_TOPIC, geometry_msgs.Twist._TYPE);
        subscriber.addMessageListener(new MessageListener<geometry_msgs.Twist>() {
            @Override
            public void onNewMessage(geometry_msgs.Twist message) {
                Vector3 linear = message.getLinear();
                Vector3 angular = message.getAngular();
                double value = linear.getX();
                double sinus = angular.getZ();
                Log.messageReceived(HeadJoystickAnalyzerNode.this, String.format("linear=%.3f, angular=%.3f", value, sinus));
                //...
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
