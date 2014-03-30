package ru.robotmitya.robocommonlib;

/**
 * RoboScript language constants.
 * Created by dmitrydzz on 3/26/14.
 */
public class Rs {
    public class Instruction {
        public static final String ID = "I";

        public static final short HEADLIGHTS_OFF = 0x0000;
        public static final short HEADLIGHTS_ON = 0x0001;

        public static final short CAMERA_OFF = 0x0010;
        public static final short CAMERA_FRONT_ON = 0x0011;
        public static final short CAMERA_BACK_ON = 0x0012;

        public static final short MIC_OFF = 0x0020;
        public static final short MIC_ON = 0x0021;

        public static final short ACCUMULATOR_MAIN_CHARGING_STOP = 0x0030;
        public static final short ACCUMULATOR_MAIN_CHARGING_START = 0x0031;

        public static final short ACCUMULATOR_PHONE_CHARGING_STOP = 0x0040;
        public static final short ACCUMULATOR_PHONE_CHARGING_START = 0x0041;
    }

    public class Mood {
        public static final String ID = "M";

        public static final short FACE_OK = 0x0001;
        public static final short FACE_HAPPY = 0x0002;
        public static final short FACE_BLUE = 0x0003;
        public static final short FACE_ANGRY = 0x0004;
        public static final short FACE_ILL = 0x0005;
        public static final short FACE_READY_TO_PLAY = 0x0006;

        public static final short ACTION_HAPPY = 0x0101;
        public static final short ACTION_PLAY = 0x0102;
        public static final short ACTION_SAD = 0x0103;
        public static final short ACTION_ANGRY = 0x0104;
        public static final short ACTION_DANCE = 0x0105;
        public static final short ACTION_NOSE = 0x0106;
    }

    public class Wait {
        public static final String ID = "W";
    }
}
