package ru.robotmitya.roboboard;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity {

    private VideoFragment mVideoFragment;
    private BoardFragment mBoardFragment;

    public MainActivity() {
        super("Robot Mitya\'s ticker", "Robot Mitya");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        mVideoFragment = new VideoFragment();
        fragmentTransaction.add(R.id.video_fragment, mVideoFragment);

        mBoardFragment = new BoardFragment();
        fragmentTransaction.add(R.id.board_fragment, mBoardFragment);

        fragmentTransaction.commit();

    }

    @Override
    public void startMasterChooser() {
        Intent data = new Intent();
        data.putExtra("ROS_MASTER_URI", "http://192.168.100.10:11311");//SettingsActivity.getMasterUri());
        data.putExtra("NEW_MASTER", false);
        data.putExtra("ROS_MASTER_PRIVATE", false);
        onActivityResult(0, RESULT_OK, data);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeConfiguration nodeConfiguration =
                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress(), getMasterUri());

        nodeMainExecutor.execute(mVideoFragment.getImageView(), nodeConfiguration.setNodeName("robot_mitya/video_node"));
    }

}
