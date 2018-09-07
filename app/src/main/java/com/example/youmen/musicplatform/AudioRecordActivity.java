package com.example.youmen.musicplatform;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;

/**
 * Created by youmen on 2018/5/11.
 */

public class AudioRecordActivity extends Activity {
    boolean recording=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiorecord);

        final Chronometer timecount=(Chronometer)findViewById(R.id.timecount);
        final ImageView recordimg=(ImageView)findViewById(R.id.Recordimg);
        timecount.setFormat("time:%s");
        recordimg.setVisibility(View.VISIBLE);
        recordimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordimg.setImageResource(R.drawable.ic_microphone2);

                /*if(recording=false){
                    recordimg.setImageResource(R.drawable.ic_microphone2);
                    timecount.start();
                    recording=true;
                }else{
                    recordimg.setImageResource(R.drawable.ic_microphone1);
                    timecount.stop();
                    recording=false;
                }*/
            }
        });


    }
}
