package ru.robotmitya.roboboard;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import ru.robotmitya.robocommonlib.AppConst;
import ru.robotmitya.robocommonlib.Log;
import ru.robotmitya.robocommonlib.RoboState;

/**
 * Created by dmitrydzz on 3/23/14.
 *
 */
public class BoardNode implements NodeMain {
    private Publisher<std_msgs.String> mEyePublisher;
    private Publisher<std_msgs.String> mFacePublisher;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(AppConst.RoboBoard.BOARD_NODE);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        mEyePublisher = connectedNode.newPublisher(AppConst.RoboHead.EYE_TOPIC, std_msgs.String._TYPE);
        mFacePublisher = connectedNode.newPublisher(AppConst.RoboHead.FACE_TOPIC, std_msgs.String._TYPE);

        Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber(AppConst.RoboBoard.BOARD_TOPIC, std_msgs.String._TYPE);
        subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(std_msgs.String message) {
                String messageBody = message.getData();

                Log.messageReceived(BoardNode.this, messageBody);
                //...
            }
        });

        initializeRoboState();
    }

    private void initializeRoboState() {
        //todo: Change this after implementing command "IFFFF".
        RoboState.setNumberOfCams((short) 2);
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
        Log.messagePublished(this, publisher.getTopicName().toString(), command);
    }

    public void publishToEyeTopic(final String message) {
        publishCommand(mEyePublisher, message);
    }

    public void publishToFaceTopic(final String message) {
        publishCommand(mFacePublisher, message);
    }
}
