package ru.robotmitya.robohead;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import java.lang.String;

import ru.robotmitya.robocommonlib.AppConst;
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
        return GraphName.of(AppConst.RoboHead.HEAD_STATE_NODE);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        mBoardPublisher = connectedNode.newPublisher(AppConst.RoboBoard.BOARD_TOPIC, std_msgs.String._TYPE);
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
                        publishToBoard(message);
                    }
                    break;
                case Rs.Instruction.ACCUMULATOR_MAIN_CHARGING_STOP:
                case Rs.Instruction.ACCUMULATOR_MAIN_CHARGING_START:
                    if (RoboState.setMainAccumulatorCharging(value)) {
                        publishToBoard(message);
                    }
                    break;
                case Rs.Instruction.ACCUMULATOR_PHONE_CHARGING_STOP:
                case Rs.Instruction.ACCUMULATOR_PHONE_CHARGING_START:
                    if (RoboState.setPhoneAccumulatorCharging(value)) {
                        publishToBoard(message);
                    }
                    break;
            }
        }
    }

    private void publishToBoard(final String command) {
        std_msgs.String message = mBoardPublisher.newMessage();
        message.setData(command);
        mBoardPublisher.publish(message);

        Log.messagePublished(this, mBoardPublisher.getTopicName().toString(), command);
    }
}
