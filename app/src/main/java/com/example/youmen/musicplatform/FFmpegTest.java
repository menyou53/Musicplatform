package com.example.youmen.musicplatform;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;
import nl.bravobit.ffmpeg.FFtask;

/**
 * Created by youmen on 2018/4/22.
 */

public class FFmpegTest extends Activity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpegtest);
        Button FFbtn = (Button) findViewById(R.id.FFbtn);


        if (FFmpeg.getInstance(this).isSupported()) {
            // ffmpeg is supported
            versionFFmpeg();}
        /*FFbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/
    }

    //cut video

    private void versionFFmpeg() {

        FFmpeg.getInstance(this).execute(new String[]{"-version"}, new ExecuteBinaryResponseHandler() {
            @Override
            public void onSuccess(String message) {
                Log.e("",message);
            }

            @Override
            public void onProgress(String message) {
                Log.e("",message);
            }
        });

    }

    public void run(View run) {
        String base = Environment.getExternalStorageDirectory().getPath() + "/VideoEdit/";
        Log.e("PATH", base);
        String[] commands = new String[12];
        commands[0] = "ffmpeg";
        //commands[2] = base+"Brave Shine.mp3";
        commands[1] = "-ss";
        commands[2] = "00:00:05";
        commands[3] = "-t";
        commands[4] = "10";
        commands[5] = "-i";
        commands[6] = base + "video.mp4";
        commands[7] = "-acodec";
        commands[8] = "copy";
        commands[9] = "-vcodec";
        commands[10] = "copy";
        commands[11] = base + "cut video.mp4";
        //commands[9] = base+"Brave Shine_cut.mp3";
        int result = jni.FFmpegJni.run(commands);
        if (result == 0) {
            Toast.makeText(FFmpegTest.this, "finish!", Toast.LENGTH_SHORT).show();
        }
    }

/*
    public void runmux(View runmux) {
        String base = Environment.getExternalStorageDirectory().getPath() + "/FFmpeg/";
        Log.e("PATH", base);
        String[] commands = new String[14];
        commands[0] = "ffmpeg";
        commands[1] = "-i";
        commands[2] = base + "recordtest.mp4";
        commands[3] = "-i";
        commands[4] = base + "Lemon.wav";
        commands[5] = "-c:v";
        commands[6] = "copy";
        commands[7] = "-map";
        commands[8] = "0:v:0";
        commands[9] = "-c:a";
        commands[10] = "copy";
        commands[11] = "-map";
        commands[12] = "1:a:0";
        commands[13] = base + "muxvideo.mp4";
        int result = FFmpegJni.run(commands);
        if (result == 0) {
            Toast.makeText(FFmpegTest.this, "命令行执行完成", Toast.LENGTH_SHORT).show();
        }
    }

*/

   /*
   //video audio track cover
    public void runmux(View runmux){
        String base = Environment.getExternalStorageDirectory().getPath()+ "/VideoEdit/";
        Log.e("PATH", base);
        String[] commands = new String[12];
        commands[0] = "ffmpeg";
        commands[1] = "-i";
        commands[2] = base + "cut video.mp4";
        commands[3] = "-i";
        commands[4] = base + "audio.mp4";
        commands[5] = "-c";
        commands[6] = "copy";
        commands[7] = "-map";
        commands[8] = "0:v:0";
        commands[9] = "-map";
        commands[10] = "1:a:0";
        commands[11] = base + "audio in cut video.mp4";
        int result = FFmpegJni.run(commands);
        if(result == 0){
            Toast.makeText(FFmpegTest.this, "finish!", Toast.LENGTH_SHORT).show();
        }
    }
*/

    //commands[9] = "-vf";


    public void runsplit(View runsplit) {
        String base = Environment.getExternalStorageDirectory().getPath() + "/FFmpeg/";
        String input1=base+"recordtest2.mp4";
        String input2=base+"Lemon.mp3";
        String output=base+"muxvideo3.mp4";
        String[] command={"-i",input1,"-i",input2,"-c:v","copy","-map","0:v:0","-c:a","copy","-map","1:a:0",output};
        String[] cmd={"-version"};
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        // to execute "ffmpeg -version" command you just need to pass "-version"
        ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {


            @Override
            public void onProgress(String message) {
                Log.e("message",message);
            }


            @Override
            public void onSuccess(String message) {
                Log.e("message2",message);

            }

        });

    }




    public void runmux(View runmux){
        String base = Environment.getExternalStorageDirectory().getPath() + "/FFmpeg/";
        String input1=base+"recordtest2.mp4";
        String input2=base+"yurei.mp3";
        String output=base+"muxvideo2.mp4";
        String[] command={"-i",input1,"-i",input2,"-c:v","copy","-map","0:v:0","-c:a","copy","-map","1:a:0",output};
        String[] cmd={"-version"};
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        // to execute "ffmpeg -version" command you just need to pass "-version"
        ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {


            @Override
            public void onProgress(String message) {
                Log.e("message",message);
            }


            @Override
            public void onSuccess(String message) {
                Log.e("message2",message);

            }

        });

    }


    public void runconvert(View runconvert) {
        String base = Environment.getExternalStorageDirectory().getPath() + "/FFmpeg/";
        String input=base+"reach.mp4";
        String output=base+"cut_reach.mp4";
        String[] command={"-i",input,"-c","copy","-ss","00:00:22",output};
        final FFtask task = FFmpeg.getInstance(this).execute(command, new ExecuteBinaryResponseHandler() {


            @Override
            public void onFinish() {


                Log.e("message","on finish");

            }

            @Override
            public void onSuccess(String message) {
                Log.e("message2",message);

            }

            @Override
            public void onProgress(String message) {
                Log.e("message",message);
            }


        });

    }


    public void runmix(View runmix) {
        String base = Environment.getExternalStorageDirectory().getPath() + "/FFmpeg/";
        String input1=base+"recordtest.mp4";
        String input2=base+"recordtest2.mp4";
        String input3=base+"muxvideo2.mp4";
        String input4=base+"muxvideo.mp4";

        String output=base+"splipt.mp4";
        String[] command={"-i",input1,"-i",input2,"-i",input3,"-i",input4,"-filter_complex","nullsrc=size=640x480 [base]; [0:v] setpts=PTS-STARTPTS, scale=320x240 [upperleft]; [1:v] setpts=PTS-STARTPTS, scale=320x240 [upperright]; [2:v] setpts=PTS-STARTPTS, scale=320x240 [lowerleft]; [3:v] setpts=PTS-STARTPTS, scale=320x240 [lowerright]; [base][upperleft] overlay=shortest=1 [tmp1]; [tmp1][upperright] overlay=shortest=1:x=320 [tmp2]; [tmp2][lowerleft] overlay=shortest=1:y=240 [tmp3]; [tmp3][lowerright] overlay=shortest=1:x=320:y=240;[0:a][1:a][2:a][3:a]amix=inputs=4","-c:v","libx264",output};
        String[] cmd={"-version"};
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        // to execute "ffmpeg -version" command you just need to pass "-version"
        ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {


            @Override
            public void onProgress(String message) {
                Log.e("message",message);
            }


            @Override
            public void onSuccess(String message) {
                Log.e("message2",message);

            }

        });

}
}


//ffmpeg -i movie.mp4 -ss 00:00:03 -t 00:00:08 -async 1 -c copy cut.mp4
//"-i",input1,"-i",input2,"-c:v","copy","-map","0:v:0","-c:a","copy","-map","1:a:0",output