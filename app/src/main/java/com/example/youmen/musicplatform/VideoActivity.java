package com.example.youmen.musicplatform;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;
import nl.bravobit.ffmpeg.FFtask;

/**
 * Created by youmen on 2018/3/20.
 */

public class VideoActivity extends Activity {

    private static final int PICK_FILE_REQUEST = 123;
    private static final int PICK_FILE_REQUEST_Mux = 234;
    private String fileStringUrl,selectedFileName,muxFileStringUrl,muxSelectedFileName,videoss,videoduration;
    private VideoView videoView;
    private MediaController mController;
    private Thread cutthread=null;
    private Thread muxthread=null;
    private ProgressDialog pd;
    private ImageView btnRight,btnLeft;
    private LinearLayout ln,videolinear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        File sdFile = android.os.Environment.getExternalStorageDirectory();
        String path = sdFile.getPath() + File.separator + "Video";
        ln=(LinearLayout)findViewById(R.id.videotool);
        ln.bringToFront();

        btnRight=(ImageView)findViewById(R.id.btn_right);
        btnLeft=(ImageView)findViewById(R.id.btn_left);

        btnRight.bringToFront();
        btnLeft.bringToFront();

        File dirFile = new File(path);

        if (!dirFile.exists()) {//如果資料夾不存在

            dirFile.mkdir();//建立資料夾
        }

        if (FFmpeg.getInstance(this).isSupported()) {
            // ffmpeg is supported
            versionFFmpeg();
        }else{
            Log.e("message","can't edit");
        }


        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ln.setVisibility(View.VISIBLE);
                btnLeft.setVisibility(View.INVISIBLE);
                btnRight.setVisibility(View.VISIBLE);
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ln.setVisibility(View.INVISIBLE);
                btnLeft.setVisibility(View.VISIBLE);
                btnRight.setVisibility(View.INVISIBLE);

            }
        });




        final ImageView btntoRecordVideo=(ImageView) findViewById(R.id.btn_recordVideo);
        btntoRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(VideoActivity.this,Camera2VideoImageActivity.class);
                startActivity(intent);
            }
        });

        final ImageView btnSplitVideo=(ImageView)findViewById(R.id.btn_split);
        btnSplitVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(VideoActivity.this,SplitScreenVideo.class);
                startActivity(intent);
            }
        });

        final ImageView btnChooseVideo=(ImageView) findViewById(R.id.btn_chooseVideo);
        btnChooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        mController = new MediaController(this);
        videoView=(VideoView)findViewById(R.id.videoView);
        videoView.setMediaController(mController);

        final ImageView btnMux=(ImageView) findViewById(R.id.btn_mux);
        btnMux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooserMux();
                videoView.stopPlayback();
            }
        });

        final ImageView btnCut=(ImageView) findViewById(R.id.btn_cut);
        btnCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileStringUrl != null) {
                    videoView.pause();
                    String videotime = String.valueOf(videoView.getCurrentPosition());
                    String videodu=String.valueOf(videoView.getDuration());
                    videoss = videotime.substring(0, videotime.length() - 3);
                    videoduration=videodu.substring(0, videodu.length() - 3);
                    Log.e("current", videoss);
                    Log.e("du", videoduration);
                    pd = ProgressDialog.show(VideoActivity.this, "wait...", "剪輯中...");
                    cutthread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            cutvideo();
                        }
                    });
                    cutthread.start();
                }
            }
        });
    }



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


    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
       /* Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "Video");
        intent.setDataAndType(uri,"video/*");*/
       intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "result"), PICK_FILE_REQUEST);
    }
    private void showFileChooserMux() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        /*Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "MixFile");
        intent.setDataAndType(uri,"audio/*");*/
        videoView.pause();
        intent.setType("audio/*");

        startActivityForResult(Intent.createChooser(intent, "result"), PICK_FILE_REQUEST_Mux);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        // 有選擇檔案
        if ( resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_FILE_REQUEST) {
                // 取得檔案的 Uri
                Uri selectedFileUri = data.getData();
                if (selectedFileUri != null) {
                    fileStringUrl = FilePath.getPath(this, selectedFileUri);

                  //  fileStringUrl=selectedFileUri.getPath().replace("/document/","");
                    videoView.setVideoURI(selectedFileUri);
                    //videoView.setVideoPath(fileStringUrl);
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            videoView.start();
                        }
                    });
                    videoView.setVisibility(View.VISIBLE);
                    if (fileStringUrl != null && !fileStringUrl.equals("")) {
                        selectedFileName = fileStringUrl.substring(fileStringUrl.lastIndexOf("/") + 1);
                    }
                } else {
                    Toast.makeText(this, "無效的檔案路徑 !!", Toast.LENGTH_SHORT).show();
                }
            }
        }if (requestCode == PICK_FILE_REQUEST_Mux && data != null && data.getData() != null) {
            // 取得檔案的 Uri
            Uri selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                muxFileStringUrl = FilePath.getPath(this, selectedFileUri);
                //muxFileStringUrl=selectedFileUri.getPath().replace("/document/","");

                pd = ProgressDialog.show(VideoActivity.this, "wait...", "混合音視頻...");
                muxthread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        muxvideo();
                    }
                });
                muxthread.start();
            } else {
                Toast.makeText(this, "無效的檔案路徑 !!", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void cutvideo(){

        File sdFile = android.os.Environment.getExternalStorageDirectory();
        String path = sdFile.getPath() + File.separator + "Video";

        File dirFile = new File(path);

        if (!dirFile.exists()) {//如果資料夾不存在

            dirFile.mkdir();//建立資料夾
        }
        String input=fileStringUrl;

        final String outputfile=path+"/"+"cut_"+fileStringUrl.substring(fileStringUrl.lastIndexOf("/") + 1);

        String[] command={"-i",input,"-ss",videoss,"-acodec","copy","-vcodec","copy","-to",videoduration,"-y",outputfile};
        final FFtask task = FFmpeg.getInstance(this).execute(command, new ExecuteBinaryResponseHandler() {


            @Override
            public void onFinish() {
                AlertDialogFinish();
                pd.dismiss();
                cutthread.interrupt();
                Log.e("message","on finish");
                videoView.setVideoPath(outputfile);
                //videoView.setVideoPath(fileStringUrl);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoView.start();
                    }
                });

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

    private void muxvideo(){
        File sdFile = android.os.Environment.getExternalStorageDirectory();
        String path = sdFile.getPath() + File.separator + "Video";

        File dirFile = new File(path);

        if (!dirFile.exists()) {//如果資料夾不存在

            dirFile.mkdir();//建立資料夾
        }
        String input1=fileStringUrl;
        String input2=muxFileStringUrl;
        final String outputfile=path+"/"+"mux_"+fileStringUrl.substring(fileStringUrl.lastIndexOf("/") + 1);

        String[] command={"-i",input1,"-i",input2,"-c:v","copy","-c:a","aac","-strict","experimental","-map","0:v:0","-map","1:a:0","-y",outputfile};
        final FFtask task = FFmpeg.getInstance(this).execute(command, new ExecuteBinaryResponseHandler() {


            @Override
            public void onFinish() {
                AlertDialogFinish();
                pd.dismiss();
                muxthread.interrupt();
                Log.e("message","on finish");
                videoView.setVideoPath(outputfile);
                //videoView.setVideoPath(fileStringUrl);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoView.start();
                    }
                });
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
    public void AlertDialogFinish(){
        LayoutInflater inflater=LayoutInflater.from(VideoActivity.this);
        final View v=inflater.inflate(R.layout.alertdialog_upload,null);
        new AlertDialog.Builder(VideoActivity.this)
                .setTitle("影片製作完成  ")
                .setView(v)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }




}


