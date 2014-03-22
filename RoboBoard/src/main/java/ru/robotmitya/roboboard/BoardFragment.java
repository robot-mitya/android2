package ru.robotmitya.roboboard;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class BoardFragment extends Fragment {
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
                Toast.makeText(BoardFragment.this.getActivity(), "!", Toast.LENGTH_SHORT).show();
            }
        });

        return result;
    }
}
