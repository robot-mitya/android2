package ru.robotmitya.robohead;

import android.content.Context;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

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
    private final Context mContext;
    private Publisher<std_msgs.String> mBoardPublisher;

    public HeadStateNode(Context context) {
        mContext = context;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(AppConst.RoboHead.HEAD_STATE_NODE);
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        RoboState.setSelectedCamIndex((short)SettingsActivity.getCameraIndex());

        mBoardPublisher = connectedNode.newPublisher(AppConst.RoboBoard.BOARD_TOPIC, std_msgs.String._TYPE);

        Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber(AppConst.RoboHead.HEAD_STATE_TOPIC, std_msgs.String._TYPE);
        subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(std_msgs.String message) {
                String messageBody = message.getData();
                String identifier = MessageHelper.getMessageIdentifier(messageBody);
                short value = (short)MessageHelper.getMessageIntegerValue(messageBody);

                Log.messageReceived(HeadStateNode.this, messageBody);

                if (identifier.contentEquals(Rs.Instruction.ID)) {
                    switch (value) {
                        case Rs.Instruction.HEADLIGHTS_OFF:
                        case Rs.Instruction.HEADLIGHTS_ON:
                            RoboState.setHeadlights(value);
                            publishToBoard(messageBody);
                            break;
                        case Rs.Instruction.CAMERA_OFF:
                            SettingsActivity.setCameraIndex(mContext, -1);
                            RoboState.setSelectedCamIndex((short) SettingsActivity.getCameraIndex());
                            publishToBoard(messageBody);
                            break;
                        case Rs.Instruction.CAMERA_BACK_ON:
                            SettingsActivity.setCameraIndex(mContext, SettingsActivity.getBackCameraIndex());
                            RoboState.setSelectedCamIndex((short) SettingsActivity.getCameraIndex());
                            publishToBoard(messageBody);
                            break;
                        case Rs.Instruction.CAMERA_FRONT_ON:
                            SettingsActivity.setCameraIndex(mContext, SettingsActivity.getFrontCameraIndex());
                            RoboState.setSelectedCamIndex((short) SettingsActivity.getCameraIndex());
                            publishToBoard(messageBody);
                            break;
                        case Rs.Instruction.ACCUMULATOR_MAIN_CHARGING_STOP:
                        case Rs.Instruction.ACCUMULATOR_MAIN_CHARGING_START:
                            RoboState.setMainAccumulatorCharging(value);
                            publishToBoard(messageBody);
                            break;
                        case Rs.Instruction.ACCUMULATOR_PHONE_CHARGING_STOP:
                        case Rs.Instruction.ACCUMULATOR_PHONE_CHARGING_START:
                            RoboState.setPhoneAccumulatorCharging(value);
                            publishToBoard(messageBody);
                            break;
                    }
                } else if (identifier.equals(Rs.Mood.ID)) {
                    RoboState.setMood(value);
                    publishToBoard(messageBody);
                }
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

    private void publishToBoard(final String command) {
        std_msgs.String message = mBoardPublisher.newMessage();
        message.setData(command);
        mBoardPublisher.publish(message);

        Log.messagePublished(this, mBoardPublisher.getTopicName().toString(), command);
    }
}
