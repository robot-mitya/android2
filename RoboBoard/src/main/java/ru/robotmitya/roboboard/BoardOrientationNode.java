package ru.robotmitya.roboboard;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import geometry_msgs.Twist;
import ru.robotmitya.robocommonlib.AppConst;
import ru.robotmitya.robocommonlib.Log;
import ru.robotmitya.robocommonlib.RoboState;

/**
 * Created by dmitrydzz on 5/25/14.
 *
 */
public class BoardOrientationNode implements NodeMain, SensorEventListener {
    private Context mContext;
    private Publisher<Twist> mPublisher;
    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private Sensor mMagneticFieldSensor;
    private final float[] mAccelerometerData = new float[3];
    private final float[] mMagneticData = new float[3];
    private final float[] mRotationMatrix = new float[16];
    private final float[] mOrientationData = new float[3];

    public BoardOrientationNode(Context context) {
        mContext = context;

        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(AppConst.RoboBoard.ORIENTATION_NODE);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        mPublisher = connectedNode.newPublisher(AppConst.RoboHead.HEAD_JOYSTICK_TOPIC, Twist._TYPE);

        final int rate = SensorManager.SENSOR_DELAY_GAME;
        mSensorManager.registerListener(this, mAccelerometerSensor, rate);
        mSensorManager.registerListener(this, mMagneticFieldSensor, rate);
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerData, 0, 3);
        } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagneticData, 0, 3);
        } else {
            return;
        }

        SensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerData, mMagneticData);
        SensorManager.getOrientation(mRotationMatrix, mOrientationData);

        double horizontalOrientation = mOrientationData[0]; // value in interval [-pi, +pi]
        horizontalOrientation += RoboState.getHorizontalZeroOrientation();
        horizontalOrientation = 2 * Math.sin(horizontalOrientation); // value in interval [-2, +2]
        if (horizontalOrientation < -1) horizontalOrientation = -1;
        if (horizontalOrientation > 1) horizontalOrientation = 1;

        double verticalOrientation = mOrientationData[2]; // value in interval [-pi, +pi]
        verticalOrientation += RoboState.getVerticalZeroOrientation();
        verticalOrientation = 4 * Math.sin(verticalOrientation); // value in interval [-4, +4]
        if (verticalOrientation < -1) verticalOrientation = -1;
        if (verticalOrientation > 1) verticalOrientation = 1;

        Log.d(this, "Horizontal: " + horizontalOrientation);
        Log.d(this, "Vertical: " + verticalOrientation);

        try {
            Twist message = mPublisher.newMessage();
            message.getAngular().setX(0);
            message.getAngular().setY(0);
            message.getAngular().setZ(-horizontalOrientation);
            message.getLinear().setX(verticalOrientation);
            message.getLinear().setY(0);
            message.getLinear().setZ(0);
//            mPublisher.publish(message);
            Log.messagePublished(this, mPublisher.getTopicName().toString(), message.toString());
        } catch (NullPointerException e) {
            Log.e(this, e.getMessage());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
