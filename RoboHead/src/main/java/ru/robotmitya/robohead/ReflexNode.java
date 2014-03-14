package ru.robotmitya.robohead;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import java.lang.String;
import java.util.ArrayList;

/**
 * Created by dmitrydzz on 3/6/14.
 *
 */
public final class ReflexNode implements NodeMain {
    private Publisher<std_msgs.String> mBodyPublisher;
    private Publisher<std_msgs.String> mFacePublisher;

    private ArrayList<String> mHappyReflex;

    public ReflexNode() {
        initHappyReflex();
        initAngryReflex();
    }

    private void initHappyReflex() {
        mHappyReflex = new ArrayList<String>();
        mHappyReflex.add("M0002");
        mHappyReflex.add("W0000");
        mHappyReflex.add("t0001");
        mHappyReflex.add("W0FA0");
        mHappyReflex.add("M0001");
    }

    private void initAngryReflex() {
        Log.e("++++++++++++++++++++++++ 0000" + MessageHelper.makeMessage("=", (short)0));
        Log.e("++++++++++++++++++++++++ 0100" + MessageHelper.makeMessage("=", (short)256));
        Log.e("++++++++++++++++++++++++ FF00" + MessageHelper.makeMessage("=", (short)-256));
    }

    private boolean mIsExecutingReflex = false;

    private void executeReflex(final ArrayList<String> reflex) {
        if (mIsExecutingReflex) {
            return;
        }

        mIsExecutingReflex = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String command : reflex) {
                    String messageIdentifier = MessageHelper.getMessageIdentifier(command);
                    int messageValue = MessageHelper.getMessageIntegerValue(command);

                    if (messageIdentifier.equals("W")) {
                        try {
                            Thread.sleep(messageValue);
                        } catch (InterruptedException e) {
                        }
                    } else if (messageIdentifier.equals("M")) {
                        publishCommand(mFacePublisher, command);
                    } else {
                        publishCommand(mBodyPublisher, command);
                    }
                }

                mIsExecutingReflex = false;
            }
        }).start();
    }

    private void publishCommand(final Publisher<std_msgs.String> publisher, final String command) {
        std_msgs.String message = publisher.newMessage();
        message.setData(command);
        publisher.publish(message);
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("robot_mitya/reflex_node");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        mBodyPublisher = connectedNode.newPublisher("robot_mitya/body", std_msgs.String._TYPE);
        mFacePublisher = connectedNode.newPublisher("robot_mitya/face", std_msgs.String._TYPE);

        Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber("robot_mitya/reflex", std_msgs.String._TYPE);
        subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(std_msgs.String message) {
                String messageBody = message.getData();

                Log.d("Message received in ReflexNode: " + messageBody);
                if (messageBody.equals("M0101")) {
                    executeReflex(mHappyReflex);
                } //else if () ...........................
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
