package ru.robotmitya.robohead;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import java.lang.String;

import ru.robotmitya.robocommonlib.Log;
import ru.robotmitya.robocommonlib.MessageHelper;
import ru.robotmitya.robocommonlib.RoboState;
import ru.robotmitya.robocommonlib.Rs;

/**
 * Created by dmitrydzz on 3/26/14.
 *
 */
public class HeadStateNode implements NodeMain {

    private Publisher<std_msgs.String> mBoardPublisher;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("robot_mitya/head_state_node");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        mBoardPublisher = connectedNode.newPublisher("robot_mitya/board", std_msgs.String._TYPE);
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

    public void changeState(final String message) {
        String identifier = MessageHelper.getMessageIdentifier(message);
        short value = (short)MessageHelper.getMessageIntegerValue(message);
        if (identifier.equals(Rs.Instruction.ID)) {
            switch (value) {
                case Rs.Instruction.HEADLIGHTS_OFF:
                case Rs.Instruction.HEADLIGHTS_ON:
                    if (RoboState.setHeadlights(value)) {
                        publish(message);
                    }
                    break;
            }
        }
    }

    private void publish(final String command) {
        std_msgs.String message = mBoardPublisher.newMessage();
        message.setData(command);
        mBoardPublisher.publish(message);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.
                append("Message sent from ").
                append(getClass().getName()).
                append(" to ").
                append(mBoardPublisher.getTopicName().toString()).
                append(": ").
                append(command);
        Log.d(stringBuilder.toString());
    }
}
