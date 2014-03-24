package ru.robotmitya.roboboard;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import ru.robotmitya.robocommonlib.Log;

/**
 * Created by dmitrydzz on 3/23/14.
 *
 */
public class BoardNode implements NodeMain {
    private Publisher<std_msgs.String> mEyePublisher;
    private Publisher<std_msgs.String> mFacePublisher;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("robot_mitya/board_node");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        mEyePublisher = connectedNode.newPublisher("robot_mitya/eye", std_msgs.String._TYPE);
        mFacePublisher = connectedNode.newPublisher("robot_mitya/face", std_msgs.String._TYPE);

        Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber("robot_mitya/board", std_msgs.String._TYPE);
        subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(std_msgs.String message) {
                String messageBody = message.getData();

                Log.d("Message received in BoardNode: " + messageBody);
                //...
            }
        });

        initializeRoboState();
    }

    private void initializeRoboState() {
        //todo: Change this after implementing command "IFFFF".
        RoboState.setNumberOfCams((short)2);
        RoboState.setSelectedCamIndex((short)1);
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

    private void publishCommand(final Publisher<std_msgs.String> publisher, final String command) {
        std_msgs.String message = publisher.newMessage();
        message.setData(command);
        publisher.publish(message);
        Log.d("Message sent from BoardNode to " + publisher.getTopicName().toString() + ": " + command);
    }

    public void publishToEyeTopic(final String message) {
        publishCommand(mEyePublisher, message);
    }

    public void publishToFaceTopic(final String message) {
        publishCommand(mFacePublisher, message);
    }
}
