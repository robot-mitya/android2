package ru.robotmitya.robocommonlib;

/**
 * Created by dmitrydzz on 3/23/14.
 *
 */
public class RoboState {
    private static short mHeadlights;

    private static short mNumberOfCams;
    private static short mSelectedCamIndex;

    static {
        mHeadlights = Rs.Instruction.HEADLIGHTS_OFF;

        mNumberOfCams = 1;
        mSelectedCamIndex = 0;
    }

    public static boolean setHeadlights(final short value) {
        if (value != mHeadlights) {
            mHeadlights = value;
            return true;
        }
        return false;
    }

    public static short getHeadlights() {
        return mHeadlights;
    }

    public static boolean setNumberOfCams(final short value) {
        if  (value != mNumberOfCams) {
            mNumberOfCams = value;
            return true;
        }
        return false;
    }

    public static short getSelectedCamIndex() {
        return mSelectedCamIndex;
    }

    public static boolean setSelectedCamIndex(final short value) {
        if (value != mSelectedCamIndex) {
            mSelectedCamIndex = value;
            return true;
        }
        return false;
    }

    public static void switchCam() {
        if (mSelectedCamIndex < mNumberOfCams - 1) {
            mSelectedCamIndex++;
        } else {
            mSelectedCamIndex = 0;
        }
    }
}
