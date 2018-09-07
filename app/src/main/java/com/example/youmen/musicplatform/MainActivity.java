package com.example.youmen.musicplatform;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;
import nl.bravobit.ffmpeg.FFtask;

public class MainActivity extends AppCompatActivity {
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String TEMP_AUDIO_FOLDER = "TempAudio";
    private static final int PICK_AUDIO_REQUEST = 123;
    private static final int PICK_AUDIO_REQUEST2 = 234;
    private static final int PICK_AUDIO_REQUEST3 = 345;
    private static final int PICK_AUDIO_REQUEST4 = 456;

    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private Thread mp3thread=null;
    private Thread mixthread=null;
    private Thread movethread=null;
    private Thread convertthread=null;

    private boolean isRecording = false;
    private ToggleButton record2;
    private String mixmusic1,mixmusic2,selectedFileName1,movefileName,inputFolder,inputVideo,inputVideoName;
    int bpmdelay,bpms;
    int bpmplaying=0;
    private SoundPool mSoundPool = null;
    private HashMap<Integer, Integer> soundID = new HashMap<Integer, Integer>();

    private EditText bpm,delay;
    private ImageView imgImport, imgStop, imgImportVideo, imgEnd;
    private ListView lstRec;
    private TextView txtRec;
    private Button toRemix;
    private MediaPlayer mediaplayer;
    private MediaRecorder mediarecorder;
    private String temFile, filename,movefilePath;
    private File recFile, recPATH;
    private List<String> lstArray = new ArrayList<String>();
    private int cListItem = 0;
    public String localUID = "test1234";
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog pd;
    private Handler handler = new Handler();
    private Thread thread=new Thread();
    private Timer mTimer;
    private MyTimerTask mTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        bpm = (EditText) findViewById(R.id.bpm);
        delay = (EditText)findViewById(R.id.delay);
        record2 = (ToggleButton) findViewById(R.id.record2);
        imgImport = (ImageView) findViewById(R.id.imgImport);
        imgImportVideo = (ImageView) findViewById(R.id.imgImportVideo);
        imgStop = (ImageView) findViewById(R.id.imgStop);
        imgEnd = (ImageView) findViewById(R.id.imgEnd);
        lstRec = (ListView) findViewById(R.id.lstRec);
        txtRec = (TextView) findViewById(R.id.txtRec);
        imgStop.setOnClickListener(listener);
        imgEnd.setOnClickListener(listener);
        lstRec.setOnItemClickListener(lstListener);
        recPATH = Environment.getExternalStorageDirectory();
        mediaplayer = new MediaPlayer();
        imgDisable(imgStop);
        requestStoragePermission();
        lstRec.setOnItemLongClickListener(lstlistener);


        bufferSize = AudioRecord.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        //AlertDialog();
        mTimer = new Timer(true);



        try {
            initSP();
        } catch (Exception e) {
            e.printStackTrace();
        }

        recList();
        File sdFile = android.os.Environment.getExternalStorageDirectory();
        String path = sdFile.getPath() + File.separator + "AudioRecorder";

        File dirFile = new File(path);
        recPATH = dirFile;

        if (!dirFile.exists()) {//如果資料夾不存在
            dirFile.mkdir();//建立資料夾
            temFile = dirFile.getAbsolutePath();
        }

        if (FFmpeg.getInstance(this).isSupported()) {
            // ffmpeg is supported
            versionFFmpeg();
        }

        final ImageView imgMix=(ImageView)findViewById(R.id.img_mix);
        imgMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog();

            }
        });



        record2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (record2.isChecked()) {
                    Thread thread = new Thread(gorecord);
                    thread.start();
                    //startRecording();
                } else {
                    pd = ProgressDialog.show(MainActivity.this, "wait...", "錄音儲存中...");
                    stopRecording();
                }
            }
        });


        final ImageView imgMetronome=(ImageView)findViewById(R.id.img_metronome);
        imgMetronome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(bpmplaying == 0){
                    try {
                        initSP();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bpms = Integer.parseInt(bpm.getText().toString());
                    StartLockWindowTimer();
                    imgMetronome.setImageResource(R.drawable.ic_metronome_red);
                    bpmplaying =1;
                }else{
                    bpmplaying =0;
                    imgMetronome.setImageResource(R.drawable.ic_metronome);
                    mSoundPool.release();
                }
            }
        });




        imgImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser3();
            }
        });
        imgImportVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser4();
            }
        });
    }


    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int hasPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
        }
        recList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  //按 允許 鈕
                recList();
            } else {
                Toast.makeText(this, "未取得權限！", Toast.LENGTH_SHORT).show();
                finish();  //結束應用程式
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private ImageView.OnClickListener listener = new ImageView.OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgImport:
                    try {
                        Calendar calendar = new GregorianCalendar();
                        Date nowtime = new Date();
                        calendar.setTime(nowtime);
                        temFile = "R" + add0(calendar.get(Calendar.YEAR)) + add0(calendar.get(Calendar.MONTH) + 1) + add0(calendar.get(Calendar.DATE)) + add0(calendar.get(Calendar.HOUR)) + add0(calendar.get(Calendar.MINUTE)) + add0(calendar.get(Calendar.SECOND));
                        recFile = new File(recPATH + "/" + temFile + ".amr");
                        mediarecorder = new MediaRecorder();
                        mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                        mediarecorder.setOutputFile(recFile.getAbsolutePath());
                        mediarecorder.prepare();
                        mediarecorder.start();
                        txtRec.setText("正在錄音.........");
                        imgEnable(imgStop);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.imgImportVideo:
                    playSong(recPATH + "/" + lstArray.get(cListItem).toString());
                    break;
                case R.id.imgStop:
                    if (mediaplayer.isPlaying()) {
                        mediaplayer.reset();
                    } else if (recFile != null) {
                        mediarecorder.stop();
                        mediarecorder.release();
                        mediarecorder = null;
                        txtRec.setText("停止" + recFile.getName() + "錄音!");
                        recList();

                    }
                    imgDisable(imgStop);
                    break;
                case R.id.imgEnd:
                    finish();
                    break;

            }
        }
    };

    private ListView.OnItemClickListener lstListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            cListItem = position; //取得點選位置
            playSong(recPATH + "/" + lstArray.get(cListItem).toString()); //播放錄音
        }
    };

    private ListView.OnItemLongClickListener lstlistener = new ListView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
            String filepath = Environment.getExternalStorageDirectory().getPath();
            final File recfile = new File(filepath, AUDIO_RECORDER_FOLDER);
            cListItem = position;
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("刪除檔案")
                    .setMessage("確定刪除檔案嗎？")
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //File file = new File(recPATH + "/" + lstArray.get(cListItem).toString());
                            File file = new File(recfile + "/" + lstArray.get(cListItem).toString());
                            if (file.exists()) {
                                boolean delete = file.delete();
                                lstArray.remove(cListItem);
                                Toast.makeText(getApplicationContext(), "File deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "File doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                            recList();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
            return true;
        }
    };

    private void playSong(String path) {
        try {
            mediaplayer.reset();
            mediaplayer.setDataSource(path); //播放錄音路徑
            mediaplayer.prepare();
            mediaplayer.start(); //開始播放
            txtRec.setText("播放：" + lstArray.get(cListItem).toString());
            imgEnable(imgStop);
            mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    txtRec.setText(lstArray.get(cListItem).toString() + "播完！");
                    imgDisable(imgStop);
                }
            });
        } catch (IOException e) {
        }
    }


    Runnable gorecord = new Runnable() {
        volatile boolean running = true;
        @Override
        public void run() {
            startRecording();
            if (!running) return;
        }
    };

    public void recList() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File recfile = new File(filepath, AUDIO_RECORDER_FOLDER);

        lstArray.clear();
        for (File file : recfile.listFiles()) {
            if (file.getName().toLowerCase().endsWith("mp3")) {
                lstArray.add(file.getName());
            }
        }
        if (lstArray.size() > 0) {
            ArrayAdapter<String> adaRec = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstArray);
            lstRec.setAdapter(adaRec);
        } else {
            ArrayAdapter<String> adaRec = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstArray);
            adaRec.add("no RecFile");
            lstRec.setAdapter(adaRec);

        }
    }

    private void imgEnable(ImageView image) {
        image.setEnabled(true);
        image.setImageAlpha(255);
    }

    private void imgDisable(ImageView image) {
        image.setEnabled(false);
        image.setImageAlpha(50);
    }

    private void btnDisable(Button btn) {
        btn.setEnabled(false);
        btn.setTextColor(0xd0d0d0);
    }

    private void btnEnable(Button btn) {
        btn.setEnabled(true);
        btn.setTextColor(0xff000000);
    }

    protected String add0(int n) {
        if (n < 10) return ("0" + n);
        else return ("" + n);
    }

    private void enableButton(int id, boolean isEnable) {
        ((Button) findViewById(id)).setEnabled(isEnable);
    }

    private void enableButtons(boolean isRecording) {
        enableButton(R.id.imgImport, !isRecording);
        enableButton(R.id.imgStop, isRecording);
    }


    private String getTempFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);

        if (tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }


    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, TEMP_AUDIO_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }
        Calendar calendar = new GregorianCalendar();
        Date nowtime = new Date();
        calendar.setTime(nowtime);
        temFile = "Rec" + add0(calendar.get(Calendar.YEAR)) + add0(calendar.get(Calendar.MONTH) + 1) + add0(calendar.get(Calendar.DATE)) + add0(calendar.get(Calendar.HOUR)) + add0(calendar.get(Calendar.MINUTE)) + add0(calendar.get(Calendar.SECOND));
        return (file.getAbsolutePath() + "/" + temFile + AUDIO_RECORDER_FILE_EXT_WAV);
    }


    private void startRecording() {

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);

        int i = recorder.getState();
        if (i == 1)
            recorder.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");

        recordingThread.start();
    }

    private void writeAudioDataToFile() {
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

        int read = 0;

        if (null != os) {
            while (isRecording) {
                read = recorder.read(data, 0, bufferSize);

                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void stopRecording() {
        if (null != recorder) {
            isRecording = false;
            int i = recorder.getState();
            if (i == 1)
                recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
        filename = getFilename();
        copyWaveFile(getTempFilename(), filename);
        deleteTempFile();
         mp3thread = new Thread(new Runnable() {
             @Override
             public void run() {
                 tomp3();
             }
         }) ;



         handler.postDelayed(new Runnable() {
             @Override
             public void run() {
                mp3thread.start();
             }
         },1000);



    }





    private void deleteTempFile() {
        File file = new File(getTempFilename());

        file.delete();
    }


    private void tomp3() {
        String base = Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/";
        String input = filename;
        String outputfilename = base + filename.substring(filename.lastIndexOf("/") + 1);
        String output = outputfilename.replace(".wav", ".mp3");
        String[] command = {"-i", input, "-acodec", "libmp3lame", output};
        final FFtask task = FFmpeg.getInstance(this).execute(command, new ExecuteBinaryResponseHandler() {


            @Override
            public void onFinish() {
                recList();
               mp3thread.interrupt();
                pd.dismiss();

                Log.e("message", "on finish");

            }

            @Override
            public void onSuccess(String message) {
                Log.e("message2", message);

            }

            @Override
            public void onProgress(String message) {
                Log.e("message", message);
            }


        });


    }


    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 1;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;
        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            AppLog.logString("File size: " + totalDataLen);
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while (in.read(data) != -1) {
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }




    private void versionFFmpeg() {

        FFmpeg.getInstance(this).execute(new String[]{"-version"}, new ExecuteBinaryResponseHandler() {
            @Override
            public void onSuccess(String message) {
                Log.e("", message);
            }

            @Override
            public void onProgress(String message) {
                Log.e("", message);
            }
        });

    }

    public void AlertDialog(){

        LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
        final View v=inflater.inflate(R.layout.alertdialog_upload,null);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Choose First Music  ")
                .setView(v)
                .setPositiveButton("choose", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showFileChooser();
                    }
                }).show();

    }
    public void AlertDialog2(){

        LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
        final View v=inflater.inflate(R.layout.alertdialog_upload,null);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Choose Second Music  ")
                .setView(v)
                .setMessage("First Video:"+selectedFileName1)
                .setPositiveButton("choose", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showFileChooser2();
                    }
                }).show();

    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        /*Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "AudioRecorder");
        intent.setDataAndType(uri,"audio/*");*/
        intent.setType("audio/*");


        startActivityForResult(Intent.createChooser(intent, "result"), PICK_AUDIO_REQUEST);
    }


    private void showFileChooser2() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
       /* Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "AudioRecorder");
        intent.setDataAndType(uri,"audio/*");*/
        intent.setType("audio/*");

        startActivityForResult(Intent.createChooser(intent, "result"), PICK_AUDIO_REQUEST2);
    }
    private void showFileChooser3() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
       /* Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "AudioRecorder");
        intent.setDataAndType(uri,"audio/*");*/
        intent.setType("audio/*");

        startActivityForResult(Intent.createChooser(intent, "result"), PICK_AUDIO_REQUEST3);
    }
    private void showFileChooser4() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        /*Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "AudioRecorder");
        intent.setDataAndType(uri,"video/*");*/
        intent.setType("video/*");

        startActivityForResult(Intent.createChooser(intent, "result"), PICK_AUDIO_REQUEST4);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filePath = data.getData();
          //  mixmusic1=filePath.getPath().replace("/document/","");
            mixmusic1 =FilePath.getPath(this,filePath);
            if (mixmusic1 != null && !mixmusic1.equals("")) {
                selectedFileName1 = mixmusic1.substring(mixmusic1.lastIndexOf("/") + 1);
            }
            Log.e("Uri",mixmusic1);
            AlertDialog2();
        }if(requestCode == PICK_AUDIO_REQUEST2 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filePath = data.getData();
           // mixmusic2=filePath.getPath().replace("/document/","");
            mixmusic2 =FilePath.getPath(this,filePath);
            Log.e("Uri",mixmusic2);
            pd = ProgressDialog.show(MainActivity.this, "wait...", "混音中...");
            mixthread=new Thread(new Runnable() {
                @Override
                public void run() {
                    mixaudio();
                }
            });
            mixthread.start();
        }if(requestCode == PICK_AUDIO_REQUEST3 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filePath = data.getData();
            //movefilePath=filePath.getPath().replace("/document/","");
            movefilePath = FilePath.getPath(this, filePath);
            Log.e("Uri",movefilePath);
            if (movefilePath != null && !movefilePath.equals("")) {
                movefileName =movefilePath.substring(movefilePath.lastIndexOf("/") + 1);
                inputFolder=movefilePath.replace(movefileName,"");

            }
            Log.e("movefileName",movefileName);
            Log.e("inputFolder",inputFolder);
            Log.e("movefilePath",movefilePath);

            movethread=new Thread(new Runnable() {
                @Override
                public void run() {
                 moveFile(inputFolder,movefileName,Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/");
                }
            });
            movethread.start();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recList();
                }
            },1000);
        }if(requestCode == PICK_AUDIO_REQUEST4 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            inputVideo = FilePath.getPath(this, filePath);
            inputVideoName =inputVideo.substring(inputVideo.lastIndexOf("/") + 1);
            Log.e("inputVideo",inputVideo);
            pd = ProgressDialog.show(MainActivity.this, "wait...", "匯入中...");
            convertthread = new Thread(new Runnable() {
                @Override
                public void run() {
                    convertVideo();
                }
            });
            convertthread.start();
        }
    }



    private void mixaudio(){

        String base = Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/";
        String input1=mixmusic1;
        String input2=mixmusic2;
        String outputfile;
        Calendar calendar = new GregorianCalendar();
        Date nowtime = new Date();
        calendar.setTime(nowtime);
        outputfile = "Mix" + add0(calendar.get(Calendar.YEAR)) + add0(calendar.get(Calendar.MONTH) + 1) + add0(calendar.get(Calendar.DATE)) + add0(calendar.get(Calendar.HOUR)) + add0(calendar.get(Calendar.MINUTE)) + add0(calendar.get(Calendar.SECOND));

        String output=base+outputfile+".mp3";
        String[] command={"-i",input1,"-i",input2,"-filter_complex","[0:0][1:0] amix=inputs=2:duration=longest","-c:a","libmp3lame",output};
        final FFtask task = FFmpeg.getInstance(this).execute(command, new ExecuteBinaryResponseHandler() {


            @Override
            public void onFinish() {

                recList();
                pd.dismiss();
                mixthread.interrupt();

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

    private void initSP() throws Exception{
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 1);
        soundID.put(1, mSoundPool.load(this, R.raw.bpm, 1));
    }

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            mSoundPool.play(soundID.get(1), 1, 1, 0, 0, 1);
        }
    };



    public void StartLockWindowTimer(){
        if (mTimer != null){
            if (mTimerTask != null){
                mTimerTask.cancel();
            }

            mTimerTask = new MyTimerTask();
            mTimer.schedule(mTimerTask, bpmdelay*1000,60000/bpms);
        }
    }


    class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            mSoundPool.play(soundID.get(1), 1, 1, 0, 0, 1);
        }
    }


    private void moveFile(String inputPath, String inputFile, String outputPath) {



        InputStream in = null;
        OutputStream out = null;
        try {
            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();
        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }


    private void convertVideo(){

        String base = Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/";
        String input1=inputVideo;
        String outputfile=inputVideoName.replace(".mp4","");

        String output=base+outputfile+".mp3";
        String[] command={"-i",input1,output};
        final FFtask task = FFmpeg.getInstance(this).execute(command, new ExecuteBinaryResponseHandler() {

            @Override
            public void onFinish() {
                pd.dismiss();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recList();
                    }
                },1000);
                convertthread.interrupt();
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



}