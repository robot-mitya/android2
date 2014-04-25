package ru.robotmitya.robocommonlib;

/**
 * Created by dmitrydzz on 3/23/14.
 *
 */
public class RoboState {
    private static short mHeadlights;

    private static short mFrontCamIndex;
    private static short mBackCamIndex;
    private static short mSelectedCamIndex;

    private static short mMainAccumulatorCharging;
    private static short mPhoneAccumulatorCharging;

    private final static short mHeadHorizontalServoMinDegree = 0;
    private final static short mHeadHorizontalServoMaxDegree = 180;
    private final static short mHeadVerticalServoMinDegree = 0;
    private final static short mHeadVerticalServoMaxDegree = 90;

    private static short mMood;

    static {
        mHeadlights = Rs.Instruction.HEADLIGHTS_OFF;

        mFrontCamIndex = -1;
        mBackCamIndex = -1;
        mSelectedCamIndex = -1;

        mMainAccumulatorCharging = Rs.Instruction.ACCUMULATOR_MAIN_CHARGING_STOP;
        mPhoneAccumulatorCharging = Rs.Instruction.ACCUMULATOR_PHONE_CHARGING_STOP;

        mMood = Rs.Mood.FACE_OK;
    }


    // Headlights

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


    // Front camera

    public static boolean setFrontCamIndex(final short value) {
        if  (value != mFrontCamIndex) {
            mFrontCamIndex = value;
            return true;
        }
        return false;
    }

    public static short getFrontCamIndex() {
        return mFrontCamIndex;
    }


    // Back camera

    public static boolean setBackCamIndex(final short value) {
        if  (value != mBackCamIndex) {
            mBackCamIndex = value;
            return true;
        }
        return false;
    }

    public static short getBackCamIndex() {
        return mBackCamIndex;
    }


    // Selected camera index

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

//    public static void switchCam() {
//        if (mSelectedCamIndex == mFrontCamIndex) {
//            mSelectedCamIndex = mBackCamIndex;
//        } else if (mSelectedCamIndex == mBackCamIndex) {
//            mSelectedCamIndex = mFrontCamIndex;
//        } else { // when (mSelectedCamIndex == -1)
//            mSelectedCamIndex = mFrontCamIndex;
//        }
//    }


    // Main accumulator charge

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


    // Phone accumulator charge

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


    // Mood

    public static boolean setMood(final short value) {
        if (value != mMood) {
            mMood = value;
            return true;
        }
        return false;
    }

    public static short getMood() {
        return mMood;
    }

    public static short getHeadHorizontalServoMinDegree() {
        return mHeadHorizontalServoMinDegree;
    }

    public static short getHeadHorizontalServoMaxDegree() {
        return mHeadHorizontalServoMaxDegree;
    }

    public static short getHeadVerticalServoMinDegree() {
        return mHeadVerticalServoMinDegree;
    }

    public static short getHeadVerticalServoMaxDegree() {
        return mHeadVerticalServoMaxDegree;
    }
}
