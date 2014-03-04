package ru.robotmitya.robohead;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ImageView;
import android.view.WindowManager.LayoutParams;

public class FaceActivity extends Activity {
    private FaceHelper mFaceHelper;
    private BroadcastReceiver mMessageReceiver;

    public FaceActivity() {
        super();

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(FaceNode.BROADCAST_EXTRA_NAME);
                Log.d("Message received in FaceActivity: " + message);
                FaceType faceType = FaceHelper.messageToFaceType(message);
                if ((mFaceHelper != null) && (faceType != FaceType.ftUnknown)) {
                    mFaceHelper.setFace(faceType);
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        ImageView faceImageView = (ImageView) this.findViewById(R.id.imageViewFace);
        mFaceHelper = new FaceHelper(faceImageView);
        mFaceHelper.setFace(FaceType.ftOk);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter(FaceNode.BROADCAST_NAME));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }
}
