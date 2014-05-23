package ru.robotmitya.robocommonlib;

/**
 * Names of nodes and topics.
 * Created by dmitrydzz on 3/30/14.
 */
public final class AppConst {
    public class RoboBoard {
        public static final String BOARD_NODE = "robot_mitya/board_node";
        public static final String VIDEO_NODE = "robot_mitya/video_node";
        public static final String DRIVE_JOYSTICK_NODE = "robot_mitya/drive_joystick_node";
        public static final String HEAD_JOYSTICK_NODE = "robot_mitya/head_joystick_node";

        public static final String BOARD_TOPIC = "robot_mitya/board";
        public static final String CAMERA_TOPIC = "/camera/image/compressed";

        public final class Broadcast {
            // Will be used to send signal to BoardNode to send command that will change remote control mode in RoboHead.
            //todo Send command to RoboHead to change remote control mode
            public static final String BROADCAST_REMOTE_CONTROL_MODE_SETTINGS_NAME = "ru.robotmitya.robohead.REMOTE_CONTROL_MODE_SETTINGS";
        }
    }

    public final class RoboHead {
        public static final String BLUETOOTH_BODY_NODE = "robot_mitya/bluetooth_body_node";
        public static final String EYE_NODE = "robot_mitya/eye_node";
        public static final String FACE_NODE = "robot_mitya/face_node";
        public static final String HEAD_STATE_NODE = "robot_mitya/head_state_node";
        public static final String REFLEX_NODE = "robot_mitya/reflex_node";
        public static final String DRIVE_JOYSTICK_ANALYZER_NODE = "robot_mitya/drive_joystick_analyzer_node";
        public static final String HEAD_JOYSTICK_ANALYZER_NODE = "robot_mitya/head_joystick_analyzer_node";

        public static final String EYE_TOPIC = "robot_mitya/eye";
        public static final String FACE_TOPIC = "robot_mitya/face";
        public static final String BODY_TOPIC = "robot_mitya/body";
        public static final String REFLEX_TOPIC = "robot_mitya/reflex";
        public static final String DRIVE_JOYSTICK_TOPIC = "robot_mitya/drive_joystick";
        public static final String HEAD_JOYSTICK_TOPIC = "robot_mitya/head_joystick";
        public static final String HEAD_STATE_TOPIC = "robot_mitya/head_state";
    }

    public final class Camera {
        public static final int DISABLED = 0;
        public static final int FRONT = 1;
        public static final int BACK = 2;
    }
}
