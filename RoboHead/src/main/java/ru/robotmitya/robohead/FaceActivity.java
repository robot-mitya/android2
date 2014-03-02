package ru.robotmitya.robohead;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.view.WindowManager.LayoutParams;

public class FaceActivity extends Activity {

    public FaceActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        ImageView faceImageView = (ImageView) this.findViewById(R.id.imageViewFace);
        FaceHelper faceHelper = new FaceHelper(faceImageView);
        faceHelper.setFace(FaceType.ftOk);
    }
}
