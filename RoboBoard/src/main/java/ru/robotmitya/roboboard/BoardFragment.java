package ru.robotmitya.roboboard;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.robotmitya.robocommonlib.Log;
import ru.robotmitya.robocommonlib.MessageHelper;
import ru.robotmitya.robocommonlib.RoboState;
import ru.robotmitya.robocommonlib.Rs;

public class BoardFragment extends Fragment {
    private BoardNode mBoardNode;

    private BroadcastReceiver mMessageReceiver;

    private CheckableImageView mButtonFaceOk;
    private CheckableImageView mButtonFaceIll;
    private CheckableImageView mButtonFaceAngry;
    private CheckableImageView mButtonFaceBlue;
    private CheckableImageView mButtonFaceHappy;

    private CheckableImageView mButtonActionPlay;
    private CheckableImageView mButtonActionDance;
    private CheckableImageView mButtonActionAngry;
    private CheckableImageView mButtonActionBlue;
    private CheckableImageView mButtonActionHappy;

    private CheckableImageView mButtonHeadlights;

    public BoardFragment(final BoardNode boardNode) {
        super();
        this.mBoardNode = boardNode;

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(BoardNode.BROADCAST_MESSAGE_RECEIVED_EXTRA_NAME);
                Log.d(BoardFragment.this, "message received: " + message);

                String identifier = MessageHelper.getMessageIdentifier(message);
                int value = MessageHelper.getMessageIntegerValue(message);
                if (identifier.contentEquals(Rs.Mood.ID)) {
                    mButtonFaceOk.setChecked(false);
                    mButtonFaceIll.setChecked(false);
                    mButtonFaceAngry.setChecked(false);
                    mButtonFaceBlue.setChecked(false);
                    mButtonFaceHappy.setChecked(false);

                    mButtonActionPlay.setChecked(false);
                    mButtonActionDance.setChecked(false);
                    mButtonActionAngry.setChecked(false);
                    mButtonActionBlue.setChecked(false);
                    mButtonActionHappy.setChecked(false);

                    switch (value) {
                        case Rs.Mood.ACTION_NOSE:
                        case Rs.Mood.FACE_OK:
                            mButtonFaceOk.setChecked(true);
                            break;
                        case Rs.Mood.FACE_ILL:
                            mButtonFaceIll.setChecked(true);
                            break;
                        case Rs.Mood.FACE_ANGRY:
                            mButtonFaceAngry.setChecked(true);
                            break;
                        case Rs.Mood.FACE_BLUE:
                            mButtonFaceBlue.setChecked(true);
                            break;
                        case Rs.Mood.FACE_HAPPY:
                            mButtonFaceHappy.setChecked(true);
                            break;

                        case Rs.Mood.ACTION_PLAY:
                            mButtonActionPlay.setChecked(true);
                            break;
                        case Rs.Mood.ACTION_DANCE:
                            mButtonActionDance.setChecked(true);
                            break;
                        case Rs.Mood.ACTION_ANGRY:
                            mButtonActionAngry.setChecked(true);
                            break;
                        case Rs.Mood.ACTION_SAD:
                            mButtonActionBlue.setChecked(true);
                            break;
                        case Rs.Mood.ACTION_HAPPY:
                            mButtonActionHappy.setChecked(true);
                            break;

                    }
                } else if (identifier.contentEquals(Rs.Instruction.ID)) {
                    switch (value) {
                        case Rs.Instruction.HEADLIGHTS_OFF:
                            mButtonHeadlights.setChecked(false);
                            break;
                        case Rs.Instruction.HEADLIGHTS_ON:
                            mButtonHeadlights.setChecked(true);
                            break;
                    }
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.board_fragment, container, false);
        if (result == null) {
            return null;
        }

        // Face buttons:
        mButtonFaceOk = (CheckableImageView) result.findViewById(R.id.buttonFaceOk);
        mButtonFaceOk.setTag(Rs.Mood.FACE_OK);
        mButtonFaceOk.setOnClickListener(faceButtonListener);

        mButtonFaceIll = (CheckableImageView) result.findViewById(R.id.buttonFaceIll);
        mButtonFaceIll.setTag(Rs.Mood.FACE_ILL);
        mButtonFaceIll.setOnClickListener(faceButtonListener);

        mButtonFaceAngry = (CheckableImageView) result.findViewById(R.id.buttonFaceAngry);
        mButtonFaceAngry.setTag(Rs.Mood.FACE_ANGRY);
        mButtonFaceAngry.setOnClickListener(faceButtonListener);

        mButtonFaceBlue = (CheckableImageView) result.findViewById(R.id.buttonFaceBlue);
        mButtonFaceBlue.setTag(Rs.Mood.FACE_BLUE);
        mButtonFaceBlue.setOnClickListener(faceButtonListener);

        mButtonFaceHappy = (CheckableImageView) result.findViewById(R.id.buttonFaceHappy);
        mButtonFaceHappy.setTag(Rs.Mood.FACE_HAPPY);
        mButtonFaceHappy.setOnClickListener(faceButtonListener);

        // Action buttons:
        mButtonActionPlay = (CheckableImageView) result.findViewById(R.id.buttonActionPlay);
        mButtonActionPlay.setTag(Rs.Mood.ACTION_PLAY);
        mButtonActionPlay.setOnClickListener(reflexButtonListener);

        mButtonActionDance = (CheckableImageView) result.findViewById(R.id.buttonActionDance);
        mButtonActionDance.setTag(Rs.Mood.ACTION_DANCE);
        mButtonActionDance.setOnClickListener(reflexButtonListener);

        mButtonActionAngry = (CheckableImageView) result.findViewById(R.id.buttonActionAngry);
        mButtonActionAngry.setTag(Rs.Mood.ACTION_ANGRY);
        mButtonActionAngry.setOnClickListener(reflexButtonListener);

        mButtonActionBlue = (CheckableImageView) result.findViewById(R.id.buttonActionBlue);
        mButtonActionBlue.setTag(Rs.Mood.ACTION_SAD);
        mButtonActionBlue.setOnClickListener(reflexButtonListener);

        mButtonActionHappy = (CheckableImageView) result.findViewById(R.id.buttonActionHappy);
        mButtonActionHappy.setTag(Rs.Mood.ACTION_HAPPY);
        mButtonActionHappy.setOnClickListener(reflexButtonListener);

        // Other buttons:
        ImageView buttonSettings = (ImageView) result.findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

        ImageView buttonActionLike = (ImageView) result.findViewById(R.id.buttonActionLike);
        buttonActionLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBoardNode != null) {
                    String command = MessageHelper.makeMessage(Rs.Tail.ID, Rs.Tail.WAG_1);
                    mBoardNode.publishToBodyTopic(command);
                }
            }
        });

        mButtonHeadlights = (CheckableImageView) result.findViewById(R.id.buttonHeadlights);
        mButtonHeadlights.setOnClickListener(headlightsButtonListener);

        ImageView buttonActionNo = (ImageView) result.findViewById(R.id.buttonActionNo);
        buttonActionNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBoardNode != null) {
                    String command = MessageHelper.makeMessage(Rs.No.ID, Rs.No.SHAKE_1);
                    mBoardNode.publishToBodyTopic(command);
                }
            }
        });

        ImageView buttonActionYes = (ImageView) result.findViewById(R.id.buttonActionYes);
        buttonActionYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBoardNode != null) {
                    String command = MessageHelper.makeMessage(Rs.Yes.ID, Rs.Yes.SHAKE_1);
                    mBoardNode.publishToBodyTopic(command);
                }
            }
        });

        ImageView buttonSwitchCam = (ImageView) result.findViewById(R.id.buttonSwitchCam);
        buttonSwitchCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBoardNode != null) {
                    RoboState.switchCam();
                    String command = MessageHelper.makeMessage(
                            Rs.Instruction.ID,
                            (short)(Rs.Instruction.CAMERA_BACK_ON + RoboState.getSelectedCamIndex()));
                    mBoardNode.publishToEyeTopic(command);
                }
            }
        });

        return result;
    }

    private View.OnClickListener faceButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBoardNode != null) {
                CheckableImageView button = (CheckableImageView)v;
                short value = button.isChecked() ? Rs.Mood.FACE_OK : (Short)v.getTag();
                String command = MessageHelper.makeMessage(Rs.Mood.ID, value);
                mBoardNode.publishToFaceTopic(command);
            }
        }
    };

    private View.OnClickListener reflexButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBoardNode != null) {
                short value = (Short)v.getTag();
                String command = MessageHelper.makeMessage(Rs.Mood.ID, value);
                mBoardNode.publishToReflexTopic(command);
            }
        }
    };

    private View.OnClickListener headlightsButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBoardNode != null) {
                short value = mButtonHeadlights.isChecked() ? Rs.Instruction.HEADLIGHTS_OFF : Rs.Instruction.HEADLIGHTS_ON;
                String command = MessageHelper.makeMessage(Rs.Instruction.ID, value);
                mBoardNode.publishToBodyTopic(command);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mMessageReceiver, new IntentFilter(BoardNode.BROADCAST_MESSAGE_RECEIVED_NAME));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }
}
