package com.example.youmen.musicplatform;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

/**
 * Created by youmen on 2018/5/19.
 */

public class SplitScreenVideo extends Activity{
    private static final int PICK_FILE_REQUEST1 = 111;
    private static final int PICK_FILE_REQUEST2 = 222;
    private static final int PICK_FILE_REQUEST3 = 333;
    private static final int PICK_FILE_REQUEST4 = 444;
    private int flag=0;
    private String fileStringUrl1,fileStringUrl2,fileStringUrl3,fileStringUrl4,selectedFileName1,selectedFileName2,selectedFileName3,selectedFileName4,finishFile;
    private Thread splitthread=null;
    private ProgressDialog pd;
    private String uri=Environment.getExternalStorageDirectory().getPath();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splitscreen);

        if (FFmpeg.getInstance(this).isSupported()) {
            // ffmpeg is supported
            versionFFmpeg();
        }

        final ImageView split1=(ImageView)findViewById(R.id.splitview1);
        split1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=1;
                AlertDialog();
            }
        });
        final ImageView split2=(ImageView)findViewById(R.id.splitview2);
        split2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=2;
                AlertDialog();
            }
        });

        final ImageView split4=(ImageView)findViewById(R.id.splitview4);
        split4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=4;
                AlertDialog();
            }
        });

    }





    public void AlertDialog(){

        LayoutInflater inflater=LayoutInflater.from(SplitScreenVideo.this);
        final View v=inflater.inflate(R.layout.alertdialog_upload,null);
        new AlertDialog.Builder(SplitScreenVideo.this)
                .setTitle("Choose First Video  ")
                .setView(v)
                .setPositiveButton("choose", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showFileChooser();
                    }
                }).show();

    }
    public void AlertDialog2(){

        LayoutInflater inflater=LayoutInflater.from(SplitScreenVideo.this);
        final View v=inflater.inflate(R.layout.alertdialog_upload,null);
        new AlertDialog.Builder(SplitScreenVideo.this)
                .setTitle("Choose Second Video  ")
                .setView(v)
                .setMessage("1st Video:"+selectedFileName1)
                .setPositiveButton("Choose 2nd Video", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showFileChooser2();
                    }
                }).show();

    }
    public void AlertDialog3(){

        LayoutInflater inflater=LayoutInflater.from(SplitScreenVideo.this);
        final View v=inflater.inflate(R.layout.alertdialog_upload,null);
        new AlertDialog.Builder(SplitScreenVideo.this)
                .setTitle("Choose Third Video  ")
                .setView(v)
                .setMessage("1st Video:"+selectedFileName1+"\n"+"2nd Video:"+selectedFileName2)
                .setPositiveButton("Choose 3rd Video", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showFileChooser3();
                    }
                }).show();

    }
    public void AlertDialog4(){

        LayoutInflater inflater=LayoutInflater.from(SplitScreenVideo.this);
        final View v=inflater.inflate(R.layout.alertdialog_upload,null);
        new AlertDialog.Builder(SplitScreenVideo.this)
                .setTitle("Choose Fourth Video  ")
                .setView(v)
                .setMessage("1st Video:"+selectedFileName1+"\n"+"2nd Video:"+selectedFileName2+"\n"+"3rd Video:"+selectedFileName3)
                .setPositiveButton("Choose 4th Video", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showFileChooser4();
                    }
                }).show();

    }
    public void AlertDialogFinal(){

        LayoutInflater inflater=LayoutInflater.from(SplitScreenVideo.this);
        final View v=inflater.inflate(R.layout.alertdialog_upload,null);
        if(flag==1) {
            new AlertDialog.Builder(SplitScreenVideo.this)
                    .setTitle("製作分割影片  ")
                    .setView(v)
                    .setMessage("1st Video:"+selectedFileName1+"\n"+"2nd Video:"+selectedFileName2)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pd = ProgressDialog.show(SplitScreenVideo.this, "wait...", "影片製作中...");
                            splitthread =new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    split1();
                                }
                            });
                            splitthread.start();
                        }
                    }).show();
        }if(flag==2) {
            new AlertDialog.Builder(SplitScreenVideo.this)
                    .setTitle("製作分割影片  ")
                    .setView(v)
                    .setMessage("1st Video:"+selectedFileName1+"\n"+"2nd Video:"+selectedFileName2+"\n"+"3rd Video:"+selectedFileName3)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pd = ProgressDialog.show(SplitScreenVideo.this, "wait...", "影片製作中...");
                            splitthread =new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    split2();
                                }
                            });
                            splitthread.start();
                        }
                    }).show();

        }if(flag==4) {
            new AlertDialog.Builder(SplitScreenVideo.this)
                    .setTitle("製作分割影片  ")
                    .setView(v)
                    .setMessage("1st Video:"+selectedFileName1+"\n"+"2nd Video:"+selectedFileName2+"\n"+"3rd Video:"+selectedFileName3+"\n"+"4th Video:"+selectedFileName4)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pd = ProgressDialog.show(SplitScreenVideo.this, "wait...", "影片製作中...");
                            splitthread =new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    split4();
                                }
                            });
                            splitthread.start();
                        }
                    }).show();
        }
    }
    public void AlertDialogFinish(){
        LayoutInflater inflater=LayoutInflater.from(SplitScreenVideo.this);
        final View v=inflater.inflate(R.layout.alertdialog_upload,null);
        new AlertDialog.Builder(SplitScreenVideo.this)
                .setTitle("影片製作完成  ")
                .setView(v)
                .setMessage(finishFile)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent();
                        intent.setClass(SplitScreenVideo.this,VideoActivity.class);
                        startActivity(intent);
                    }
                }).show();
    }




    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
       /* Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "Video");
        intent.setDataAndType(uri,"video/*");*/
        intent.setType("video/*");


        startActivityForResult(Intent.createChooser(intent, "result"), PICK_FILE_REQUEST1);
    }
    private void showFileChooser2() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
       /* Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "Video");
        intent.setDataAndType(uri,"video/*");*/
        intent.setType("video/*");

        startActivityForResult(Intent.createChooser(intent, "result"), PICK_FILE_REQUEST2);
    }
    private void showFileChooser3() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        /*Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "Video");
        intent.setDataAndType(uri,"video/*");*/
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "result"), PICK_FILE_REQUEST3);
    }
    private void showFileChooser4() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
       /* Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "Video");
        intent.setDataAndType(uri,"video/*");*/
        intent.setType("video/*");

        startActivityForResult(Intent.createChooser(intent, "result"), PICK_FILE_REQUEST4);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        // 有選擇檔案
        if ( resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_FILE_REQUEST1) {
                // 取得檔案的 Uri
                Uri selectedFileUri = data.getData();
                if (selectedFileUri != null) {
                    //fileStringUrl1=selectedFileUri.getPath().replace("/document/","");
                    fileStringUrl1 = FilePath.getPath(this, selectedFileUri);

                    if (fileStringUrl1 != null && !fileStringUrl1.equals("")) {
                        Toast.makeText(this, "fileStringUrl"+fileStringUrl1, Toast.LENGTH_SHORT).show();
                        selectedFileName1 = fileStringUrl1.substring(fileStringUrl1.lastIndexOf("/") + 1);
                    }
                    AlertDialog2();
                } else {
                    Toast.makeText(this, "無效的檔案路徑 !!", Toast.LENGTH_SHORT).show();
                }
            }
        }if ( resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_FILE_REQUEST2) {
                // 取得檔案的 Uri
                Uri selectedFileUri = data.getData();
                if (selectedFileUri != null) {
                    //fileStringUrl2=selectedFileUri.getPath().replace("/document/","");;
                   fileStringUrl2 = FilePath.getPath(this, selectedFileUri);

                    if (fileStringUrl2 != null && !fileStringUrl2.equals("")) {
                        selectedFileName2 = fileStringUrl2.substring(fileStringUrl2.lastIndexOf("/") + 1);
                    }
                    if(flag==1){
                        AlertDialogFinal();
                    }else{
                        AlertDialog3();
                    }
                } else {
                    Toast.makeText(this, "無效的檔案路徑 !!", Toast.LENGTH_SHORT).show();
                }
            }
        }if ( resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_FILE_REQUEST3) {
                // 取得檔案的 Uri
                Uri selectedFileUri = data.getData();
                if (selectedFileUri != null) {
                   // fileStringUrl3=selectedFileUri.getPath().replace("/document/","");;

                   fileStringUrl3 = FilePath.getPath(this, selectedFileUri);

                    if (fileStringUrl3 != null && !fileStringUrl3.equals("")) {
                        selectedFileName3 = fileStringUrl3.substring(fileStringUrl3.lastIndexOf("/") + 1);
                    }
                    if(flag==2||flag==3){
                        AlertDialogFinal();
                    }else{
                        AlertDialog4();
                    }
                } else {
                    Toast.makeText(this, "無效的檔案路徑 !!", Toast.LENGTH_SHORT).show();
                }
            }
        }if ( resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_FILE_REQUEST4) {
                // 取得檔案的 Uri
                Uri selectedFileUri = data.getData();
                if (selectedFileUri != null) {
                   // fileStringUrl4=selectedFileUri.getPath().replace("/document/","");;

                    fileStringUrl4 = FilePath.getPath(this, selectedFileUri);

                    if (fileStringUrl4 != null && !fileStringUrl4.equals("")) {
                        selectedFileName4 = fileStringUrl4.substring(fileStringUrl4.lastIndexOf("/") + 1);
                    }
                    AlertDialogFinal();
                } else {
                    Toast.makeText(this, "無效的檔案路徑 !!", Toast.LENGTH_SHORT).show();
                }
            }
        }
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

    public void split1() {
        File sdFile = android.os.Environment.getExternalStorageDirectory();
        String path = sdFile.getPath() + File.separator + "Video";
        String input1=fileStringUrl1;
        String input2=fileStringUrl2;


        String outputfile;
        outputfile = "Split"+System.currentTimeMillis();
        finishFile=outputfile;

        String output=path+"/"+outputfile+".mp4";
        String[] command={"-i",input1,"-i",input2,"-filter_complex","nullsrc=size=320x720 [base]; [0:v] setpts=PTS-STARTPTS, scale=320x360 [left]; [1:v] setpts=PTS-STARTPTS, scale=320x360 [right]; [base][left] overlay=shortest=1 [tmp1];[tmp1][right] overlay=shortest=1:y=360;[0:a][1:a]amix=inputs=2","-c:v","libx264",output};
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        // to execute "ffmpeg -version" command you just need to pass "-version"
        ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {

            @Override
            public void onFinish() {

                splitthread.interrupt();
                pd.dismiss();
                flag=0;
                Log.e("message", "on finish");
                AlertDialogFinish();

            }

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
    public void split2() {
        File sdFile = android.os.Environment.getExternalStorageDirectory();
        String path = sdFile.getPath() + File.separator + "Video";
        String input1=fileStringUrl1;
        String input2=fileStringUrl2;
        String input3=fileStringUrl3;

        String outputfile;
        outputfile = "Split"+System.currentTimeMillis();
        finishFile=outputfile;


        String output=path+"/"+outputfile+".mp4";
        String[] command={"-i",input1,"-i",input2,"-i",input3,"-filter_complex","nullsrc=size=480x960 [base];[0:v] setpts=PTS-STARTPTS, scale=240x320 [upperleft];[1:v] setpts=PTS-STARTPTS, scale=240x320 [lowerleft];[2:v] setpts=PTS-STARTPTS, scale=480x640 [right];[base][upperleft] overlay=shortest=1 [temp1]; [temp1][lowerleft] overlay=shortest=1:x=240[temp2];[temp2][right] overlay=shortest=1:y=320;[0:a][1:a][2:a]amix=inputs=3","-c:v","libx264",output};
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        // to execute "ffmpeg -version" command you just need to pass "-version"
        ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {

            @Override
            public void onFinish() {

                splitthread.interrupt();
                pd.dismiss();
                flag=0;
                Log.e("message", "on finish");
                AlertDialogFinish();

            }

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
    public void split4() {
        File sdFile = android.os.Environment.getExternalStorageDirectory();
        String path = sdFile.getPath() + File.separator + "Video";
        String input1=fileStringUrl1;
        String input2=fileStringUrl2;
        String input3=fileStringUrl3;
        String input4=fileStringUrl4;

        String outputfile;
        outputfile = "Split"+System.currentTimeMillis();
        finishFile=outputfile;

        String output=path+"/"+outputfile+".mp4";
        String[] command={"-i",input1,"-i",input2,"-i",input3,"-i",input4,"-filter_complex","nullsrc=size=480x640 [base]; [0:v] setpts=PTS-STARTPTS, scale=240x320 [upperleft]; [1:v] setpts=PTS-STARTPTS, scale=240x320 [upperright]; [2:v] setpts=PTS-STARTPTS, scale=240x320 [lowerleft]; [3:v] setpts=PTS-STARTPTS, scale=240x320 [lowerright]; [base][upperleft] overlay=shortest=1 [tmp1]; [tmp1][upperright] overlay=shortest=1:y=320 [tmp2]; [tmp2][lowerleft] overlay=shortest=1:x=240 [tmp3]; [tmp3][lowerright] overlay=shortest=1:y=320:x=240;[0:a][1:a][2:a][3:a]amix=inputs=4","-c:v","libx264",output};
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        // to execute "ffmpeg -version" command you just need to pass "-version"
        ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {

            @Override
            public void onFinish() {

                splitthread.interrupt();
                pd.dismiss();
                flag=0;
                Log.e("message", "on finish");
                AlertDialogFinish();
            }

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
