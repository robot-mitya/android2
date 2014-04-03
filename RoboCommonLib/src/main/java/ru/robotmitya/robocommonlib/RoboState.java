package ru.robotmitya.robocommonlib;

/**
 * Created by dmitrydzz on 3/23/14.
 *
 */
public class RoboState {
    private static short mHeadlights;

    private static short mNumberOfCams;
    private static short mSelectedCamIndex;

    private static short mMainAccumulatorCharging;
    private static short mPhoneAccumulatorCharging;

    static {
        mHeadlights = Rs.Instruction.HEADLIGHTS_OFF;

        mNumberOfCams = 1;
        mSelectedCamIndex = 0;

        mMainAccumulatorCharging = Rs.Instruction.ACCUMULATOR_MAIN_CHARGING_STOP;
        mPhoneAccumulatorCharging = Rs.Instruction.ACCUMULATOR_PHONE_CHARGING_STOP;
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

    public static short getmNumberOfCams() {
        return mNumberOfCams;
    }


    public static boolean setSelectedCamIndex(final short value) {
        if (value != mSelectedCamIndex) {
            mSelectedCamIndex = value;
            return true;
        }
        return false;
    }

    public static short getSelectedCamIndex() {
        return mSelectedCamIndex;
    }

    public static void switchCam() {
        if (mSelectedCamIndex < mNumberOfCams - 1) {
            mSelectedCamIndex++;
        } else {
            mSelectedCamIndex = 0;
        }
    }


    public static boolean setMainAccumulatorCharging(final short value) {
        if (value != mMainAccumulatorCharging) {
            mMainAccumulatorCharging = value;
            return true;
        }
        return false;
    }

    public static short getMainAccumulatorCharging() {
        return mMainAccumulatorCharging;
    }


    public static boolean setPhoneAccumulatorCharging(final short value) {
        if (value != mPhoneAccumulatorCharging) {
            mPhoneAccumulatorCharging = value;
            return true;
        }
        return false;
    }

    public static short getPhoneAccumulatorCharging() {
        return mPhoneAccumulatorCharging;
    }

}
