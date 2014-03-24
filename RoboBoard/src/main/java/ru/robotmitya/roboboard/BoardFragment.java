package ru.robotmitya.roboboard;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.robotmitya.robocommonlib.MessageHelper;

public class BoardFragment extends Fragment {
    private BoardNode mBoardNode;

    public BoardFragment(final BoardNode boardNode) {
        super();
        this.mBoardNode = boardNode;
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
                    String command = MessageHelper.makeMessage("I", (short)(0x0011 + RoboState.getSelectedCamIndex()));
                    mBoardNode.publishToEyeTopic(command);
                }
            }
        });

        // Face buttons:
        ImageView buttonFaceOk = (ImageView) result.findViewById(R.id.buttonFaceOk);
        buttonFaceOk.setTag(1);
        buttonFaceOk.setOnClickListener(moodListener);

        ImageView buttonFaceHappy = (ImageView) result.findViewById(R.id.buttonFaceHappy);
        buttonFaceHappy.setTag(2);
        buttonFaceHappy.setOnClickListener(moodListener);

        ImageView buttonFaceBlue = (ImageView) result.findViewById(R.id.buttonFaceBlue);
        buttonFaceBlue.setTag(3);
        buttonFaceBlue.setOnClickListener(moodListener);

        ImageView buttonFaceAngry = (ImageView) result.findViewById(R.id.buttonFaceAngry);
        buttonFaceAngry.setTag(4);
        buttonFaceAngry.setOnClickListener(moodListener);

        ImageView buttonFaceIll = (ImageView) result.findViewById(R.id.buttonFaceIll);
        buttonFaceIll.setTag(5);
        buttonFaceIll.setOnClickListener(moodListener);

        ImageView buttonFaceReadyToPlay = (ImageView) result.findViewById(R.id.buttonFaceReadyToPlay);
        buttonFaceReadyToPlay.setTag(6);
        buttonFaceReadyToPlay.setOnClickListener(moodListener);

        return result;
    }

    private View.OnClickListener moodListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBoardNode != null) {
                int value = (Integer)v.getTag();
                String command = MessageHelper.makeMessage("M", (short)value);
                mBoardNode.publishToFaceTopic(command);
            }
        }
    };
}
