package ru.robotmitya.roboboard;

/**
 * Created by dmitrydzz on 3/23/14.
 *
 */
public class RoboState {
    private static short mNumberOfCams;
    private static short mSelectedCamIndex;

    static {
        mNumberOfCams = 1;
        mSelectedCamIndex = 0;
    }

    public static void setNumberOfCams(final short numberOfCams) {
        mNumberOfCams = numberOfCams;
    }

    public static short getSelectedCamIndex() {
        return mSelectedCamIndex;
    }

    public static void setSelectedCamIndex(final short selectedCamIndex) {
        mSelectedCamIndex = selectedCamIndex;
    }

    public static void switchCam() {
        if (mSelectedCamIndex < mNumberOfCams - 1) {
            mSelectedCamIndex++;
        } else {
            mSelectedCamIndex = 0;
        }
    }
}
