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
    private CheckableImageView mButtonFaceHappy;
    private CheckableImageView mButtonFaceBlue;
    private CheckableImageView mButtonFaceAngry;
    private CheckableImageView mButtonFaceIll;
    private CheckableImageView mButtonFaceReadyToPlay;

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
                    mButtonFaceHappy.setChecked(false);
                    mButtonFaceBlue.setChecked(false);
                    mButtonFaceAngry.setChecked(false);
                    mButtonFaceIll.setChecked(false);
                    mButtonFaceReadyToPlay.setChecked(false);

                    switch (value) {
                        case Rs.Mood.ACTION_NOSE: //todo need more buttons
                        case Rs.Mood.FACE_OK:
                            mButtonFaceOk.setChecked(true);
                            break;
                        case Rs.Mood.ACTION_HAPPY: //todo need more buttons
                        case Rs.Mood.FACE_HAPPY:
                            mButtonFaceHappy.setChecked(true);
                            break;
                        case Rs.Mood.FACE_BLUE:
                            mButtonFaceBlue.setChecked(true);
                            break;
                        case Rs.Mood.ACTION_ANGRY: //todo need more buttons
                        case Rs.Mood.FACE_ANGRY:
                            mButtonFaceAngry.setChecked(true);
                            break;
                        case Rs.Mood.FACE_ILL:
                            mButtonFaceIll.setChecked(true);
                            break;
                        case Rs.Mood.FACE_READY_TO_PLAY:
                            mButtonFaceReadyToPlay.setChecked(true);
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

        ImageView buttonSettings = (ImageView) result.findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
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

        // Face buttons:
        mButtonFaceOk = (CheckableImageView) result.findViewById(R.id.buttonFaceOk);
        mButtonFaceOk.setTag(1);
        mButtonFaceOk.setOnClickListener(moodListener);

        mButtonFaceHappy = (CheckableImageView) result.findViewById(R.id.buttonFaceHappy);
        mButtonFaceHappy.setTag(2);
        mButtonFaceHappy.setOnClickListener(moodListener);

        mButtonFaceBlue = (CheckableImageView) result.findViewById(R.id.buttonFaceBlue);
        mButtonFaceBlue.setTag(3);
        mButtonFaceBlue.setOnClickListener(moodListener);

        mButtonFaceAngry = (CheckableImageView) result.findViewById(R.id.buttonFaceAngry);
        mButtonFaceAngry.setTag(4);
        mButtonFaceAngry.setOnClickListener(moodListener);

        mButtonFaceIll = (CheckableImageView) result.findViewById(R.id.buttonFaceIll);
        mButtonFaceIll.setTag(5);
        mButtonFaceIll.setOnClickListener(moodListener);

        mButtonFaceReadyToPlay = (CheckableImageView) result.findViewById(R.id.buttonFaceReadyToPlay);
        mButtonFaceReadyToPlay.setTag(6);
        mButtonFaceReadyToPlay.setOnClickListener(moodListener);

        return result;
    }

    private View.OnClickListener moodListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBoardNode != null) {
                int value = (Integer)v.getTag();
                String command = MessageHelper.makeMessage(Rs.Mood.ID, (short)value);
                mBoardNode.publishToFaceTopic(command);
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
