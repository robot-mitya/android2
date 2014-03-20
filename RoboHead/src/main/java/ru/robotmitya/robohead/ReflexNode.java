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
import java.util.ArrayList;

/**
 * Created by dmitrydzz on 3/6/14.
 *
 */
public final class ReflexNode implements NodeMain {
    private Context mContext;

    private Publisher<std_msgs.String> mBodyPublisher;
    private Publisher<std_msgs.String> mFacePublisher;

    private ArrayList<String> mHappyReflex; // M0101
    private ArrayList<String> mPlayReflex;  // M0102
    private ArrayList<String> mSadReflex;   // M0103
    private ArrayList<String> mAngryReflex; // M0104
    private ArrayList<String> mDanceReflex; // M0105 Nina Simone "Ain't Got No – I Got Life"
    private ArrayList<String> mNoseReflex;  // M0106

    public ReflexNode(final Context context) {
        mContext = context;
        initHappyReflex();
        initPlayReflex();
        initSadReflex();
        initAngryReflex();
        initDanceReflex();
        initNoseReflex();
    }

    private void initHappyReflex() {
        mHappyReflex = new ArrayList<String>();
        mHappyReflex.add("M0101"); // smile, wag the tail
        mHappyReflex.add(MessageHelper.makeMessage("W", (short) 4000)); // 4 seconds delay
        mHappyReflex.add("M0001"); // normal face
    }

    private void initPlayReflex() {
        mPlayReflex = new ArrayList<String>();
        mPlayReflex.add("M0102"); // want to play face, jump
        mPlayReflex.add(MessageHelper.makeMessage("W", (short) 4000)); // 4 seconds delay
        mPlayReflex.add("M0001"); // normal face
    }

    private void initSadReflex() {
        mSadReflex = new ArrayList<String>();
        mSadReflex.add("M0103"); // sad face, hang down his head
        mSadReflex.add(MessageHelper.makeMessage("W", (short)5000)); // 5 seconds delay
        mSadReflex.add("M0001"); // normal face
    }

    private void initAngryReflex() {
        mAngryReflex = new ArrayList<String>();
        mAngryReflex.add("M0104");
        mAngryReflex.add(MessageHelper.makeMessage("W", (short)4000)); // 4 seconds delay
        mAngryReflex.add("M0001"); // normal face
    }

    private void initDanceReflex() {
        mDanceReflex = new ArrayList<String>();
        mDanceReflex.add("M0105");
        mDanceReflex.add(MessageHelper.makeMessage("W", (short)6500)); // 6.5 seconds delay
        mDanceReflex.add("M0001"); // normal face
    }

    private void initNoseReflex() {
        mNoseReflex = new ArrayList<String>();
        mNoseReflex.add("M0106");
        mNoseReflex.add(MessageHelper.makeMessage("W", (short) 3000)); // 3 seconds delay
        mNoseReflex.add("M0001"); // normal face
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
                            Log.e(String.format(mContext.getResources().getString(R.string.error_command_failed),
                                    ReflexNode.this.getClass().getName(), command));
                        }
                    } else {
                        if (messageIdentifier.equals("M")) {
                            publishCommand(mFacePublisher, command);
                        }
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
        Log.d("Message sent from ReflexNode to " + publisher.getTopicName().toString() + ": " + command);
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
                } else if (messageBody.equals("M0102")) {
                    executeReflex(mPlayReflex);
                } else if (messageBody.equals("M0103")) {
                    executeReflex(mSadReflex);
                } else if (messageBody.equals("M0104")) {
                    executeReflex(mAngryReflex);
                } else if (messageBody.equals("M0105")) {
                    executeReflex(mDanceReflex);
                } else if (messageBody.equals("M0106")) {
                    executeReflex(mNoseReflex);
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
}
