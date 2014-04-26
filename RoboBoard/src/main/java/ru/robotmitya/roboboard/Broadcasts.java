package ru.robotmitya.roboboard;

import android.content.Intent;

/**
 * Created by dmitrydzz on 4/26/14.
 *
 */
public class Broadcasts {
    // From BoardNode to BoardFragment:
    public static final String BROADCAST_MESSAGE_TO_GUI_NAME = "ru.robotmitya.roboboard.MESSAGE-TO-GUI";
    public static final String BROADCAST_MESSAGE_TO_GUI_EXTRA_NAME = "message";

    // From BoardFragment to BoardNode:
    public static final String BROADCAST_MESSAGE_TO_BODY_NAME = "ru.robotmitya.roboboard.MESSAGE-TO-BODY";
    public static final String BROADCAST_MESSAGE_TO_BODY_EXTRA_NAME = "message";

    public static final String BROADCAST_MESSAGE_TO_EYE_NAME = "ru.robotmitya.roboboard.MESSAGE-TO-EYE";
    public static final String BROADCAST_MESSAGE_TO_EYE_EXTRA_NAME = "message";

    public static final String BROADCAST_MESSAGE_TO_FACE_NAME = "ru.robotmitya.roboboard.MESSAGE-TO-FACE";
    public static final String BROADCAST_MESSAGE_TO_FACE_EXTRA_NAME = "message";

    public static final String BROADCAST_MESSAGE_TO_REFLEX_NAME = "ru.robotmitya.roboboard.MESSAGE-TO-REFLEX";
    public static final String BROADCAST_MESSAGE_TO_REFLEX_EXTRA_NAME = "message";
}
