package ru.robotmitya.robohead;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import java.lang.String;

import geometry_msgs.Twist;
import geometry_msgs.Vector3;
import ru.robotmitya.robocommonlib.AppConst;
import ru.robotmitya.robocommonlib.Log;
import ru.robotmitya.robocommonlib.MessageHelper;
import ru.robotmitya.robocommonlib.Rs;
import std_msgs.*;

/**
 * Created by dmitrydzz on 4/12/14.
 *
 */
public class DriveJoystickAnalyzerNode implements NodeMain {
    private final static double SIN_ALPHA_BOUND = 0.06;

    private Publisher<std_msgs.String> mBodyPublisher;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(AppConst.RoboHead.DRIVE_JOYSTICK_ANALYZER_NODE);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        mBodyPublisher = connectedNode.newPublisher(AppConst.RoboHead.BODY_TOPIC, std_msgs.String._TYPE);

        Subscriber<Twist> subscriber = connectedNode.newSubscriber(AppConst.RoboBoard.DRIVE_JOYSTICK_TOPIC, geometry_msgs.Twist._TYPE);
        subscriber.addMessageListener(new MessageListener<Twist>() {
            @Override
            public void onNewMessage(geometry_msgs.Twist message) {
                Vector3 linear = message.getLinear();
                Vector3 angular = message.getAngular();
                double x = -angular.getZ();
                double y = linear.getX();
                Log.messageReceived(DriveJoystickAnalyzerNode.this, String.format("x=%.3f, y=%.3f", x, y));

                MotorsSpeed motorsSpeed = new MotorsSpeed();
                calculateMotorsSpeed(x, y, motorsSpeed);
                publishCommand(MessageHelper.makeMessage(Rs.DriveLeft.ID, motorsSpeed.mLeft));
                publishCommand(MessageHelper.makeMessage(Rs.DriveRight.ID, motorsSpeed.mRight));
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

    private final class MotorsSpeed {
        public short mLeft;
        public short mRight;
    }

    private void calculateMotorsSpeed(final double x, final double y, final MotorsSpeed motorsSpeed) {
        double vectorLength = Math.sqrt(x * x + y * y);
        vectorLength = vectorLength < 0 ? 0 : vectorLength;
        vectorLength = vectorLength > 1 ? 1 : vectorLength;

        double sinAlpha = vectorLength == 0 ? 0 : (y >= 0 ? y / vectorLength : -y / vectorLength);

        // Аж два раза корректирую синус. Дошёл до этого эмпирически. Только так чувствуется эффект.
        sinAlpha = 1 - Math.sqrt(1 - (sinAlpha * sinAlpha)); // (нелинейная корректировка синуса) f(x) = 1 - sqrt(1 - x^2)
        sinAlpha = 1 - Math.sqrt(1 - (sinAlpha * sinAlpha)); // (нелинейная корректировка синуса) f(x) = 1 - sqrt(1 - x^2)
        sinAlpha = sinAlpha < 0 ? 0 : sinAlpha;
        sinAlpha = sinAlpha > 1 ? 1 : sinAlpha;

        boolean rotationModeOn = sinAlpha < SIN_ALPHA_BOUND;
        int leftSpeed;
        int rightSpeed;

        if ((x >= 0) && (y >= 0)) {
            leftSpeed = map(vectorLength, 0, 255);
            rightSpeed = rotationModeOn ? -leftSpeed : map(sinAlpha * vectorLength, 0, 255);
        } else if ((x < 0) && (y >= 0)) {
            rightSpeed = map(vectorLength, 0, 255);
            leftSpeed = rotationModeOn ? -rightSpeed : map(sinAlpha * vectorLength, 0, 255);
        } else if ((x < 0) && (y < 0)) {
            rightSpeed = map(vectorLength, 0, -255);
            leftSpeed = rotationModeOn ? -rightSpeed : map(sinAlpha * vectorLength, 0, -255);
        } else { // (x >= 0) && (y < 0)
            leftSpeed = map(vectorLength, 0, -255);
            rightSpeed = rotationModeOn ? -leftSpeed : map(sinAlpha * vectorLength, 0, -255);
        }

        // Делаю нелинейный прирост скорости. Функция - дуга окружности. f(x) = sqrt(2x - x^2)
        leftSpeed = nonlinearSpeedCorrection(leftSpeed);
        rightSpeed = nonlinearSpeedCorrection(rightSpeed);

        motorsSpeed.mLeft = (short) leftSpeed;
        motorsSpeed.mRight = (short) rightSpeed;
    }

    private int map(double value, int minResult, int maxResult) {
        value = value < 0 ? 0 : value;
        value = value > 1 ? 1 : value;
        double result = minResult + (value * (maxResult - minResult));
        return (int)Math.round(result);
    }

    public static int nonlinearSpeedCorrection(int speed) {
        double floatSpeed = Math.abs(speed);
        floatSpeed = floatSpeed / 255.0;
        double floatResult = Math.sqrt((2 * floatSpeed) - (floatSpeed * floatSpeed));
        floatResult = floatResult * 255.0;
        int result = (int)Math.round(floatResult);
        result = result > 255 ? 255 : result;
        result = result < 0 ? 0 : result;
        if (speed < 0) {
            result = -result;
        }
        return result;
    }

    private void publishCommand(final String command) {
        std_msgs.String message = mBodyPublisher.newMessage();
        message.setData(command);
        mBodyPublisher.publish(message);
        Log.messagePublished(this, mBodyPublisher.getTopicName().toString(), command);
    }
}
