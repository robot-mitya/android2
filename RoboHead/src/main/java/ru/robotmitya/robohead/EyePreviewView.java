package ru.robotmitya.robohead;

import android.content.Context;
import android.util.AttributeSet;

import org.ros.android.view.camera.RosCameraPreviewView;
import org.ros.namespace.GraphName;

/**
 * Created by dmitrydzz on 1/30/14.
 */
public class EyePreviewView extends RosCameraPreviewView {
    public EyePreviewView(Context context) {
        super(context);
    }

    public EyePreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EyePreviewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("robot_mitya/eye_node");
    }
}
