package ru.robotmitya.roboboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.ros.android.view.VirtualJoystickView;
import org.ros.node.ConnectedNode;

import ru.robotmitya.robocommonlib.Log;

/**
 * Created by dmitrydzz on 5/7/14.
 *
 */
public class BoardJoystickView extends VirtualJoystickView {
    private boolean mIsConnected = false;
    private Integer mPointerId = null;

    public BoardJoystickView(Context context) {
        super(context);
    }

    public BoardJoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoardJoystickView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        super.onStart(connectedNode);
        mIsConnected = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsConnected) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mPointerId = event.getPointerId(event.getActionIndex());
        }
        int pointerId = event.getPointerId(event.getActionIndex());
        if ((mPointerId == null) || (mPointerId != pointerId)) {
            return true;
        }

        String info = "+++++ ";
        info += "event: " + event.toString();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(this, info);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.e(this, info);
        }

        return super.onTouchEvent(event);
    }
}
