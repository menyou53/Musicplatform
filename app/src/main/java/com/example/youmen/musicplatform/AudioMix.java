package com.example.youmen.musicplatform;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.musicg.wave.Wave;
import com.musicg.wave.WaveHeader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by youmen on 2017/12/25.
 */

public class AudioMix extends Activity {
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_BPP = 16;
    private static final int PICK_IMAGE_REQUEST = 234;
    private static final int PICK_IMAGE_REQUEST2 = 345;
    private Uri filePath1,filePath2;
    private String fileStringUrl1,fileStringUrl2;
    private String fileName1,fileName2,fileString1,fileString2,temFile,selectedFileName1,selectedFileName2;
    private TextView textSong1,textSong2,textMix;
    private int bufferSize = 0;
    private File recPATH;
    private Button playSong1,playSong2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiomix);

        File sdFile = android.os.Environment.getExternalStorageDirectory();
        String path = sdFile.getPath() + File.separator + "MixFile";

        File dirFile = new File(path);
        recPATH = dirFile;

        if (!dirFile.exists()) {//如果資料夾不存在

            dirFile.mkdir();//建立資料夾
            temFile = dirFile.getAbsolutePath();
        }



        bufferSize = AudioRecord.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        final Button btnSong1 =(Button)findViewById(R.id.song1Btn);
        textSong1=(TextView) findViewById(R.id.song1Text);
        btnSong1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser1();
            }
        });
        playSong1=(Button)findViewById(R.id.btnPlay1);
        playSong1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic(fileStringUrl1);
            }
        });
        final Button btnSong2 =(Button)findViewById(R.id.song2Btn);
        textSong2=(TextView) findViewById(R.id.song2Text);
        btnSong2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser2();
            }
        });
        playSong2=(Button)findViewById(R.id.btnPlay2);
        playSong2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic(fileStringUrl2);
            }
        });
        final Button btnMix=(Button)findViewById(R.id.mixBtn);
        textMix=(TextView)findViewById(R.id.mixText) ;
        btnMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Calendar calendar = new GregorianCalendar();
                    Date nowtime = new Date();
                    calendar.setTime(nowtime);
                    temFile = "Mix" + add0(calendar.get(Calendar.YEAR)) + add0(calendar.get(Calendar.MONTH) + 1) + add0(calendar.get(Calendar.DATE)) + add0(calendar.get(Calendar.HOUR)) + add0(calendar.get(Calendar.MINUTE)) + add0(calendar.get(Calendar.SECOND));
                    textMix.setText(temFile);
                    mixSoundHigh();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void playMusic(String fileStringUrl) {

        try {
            MediaPlayer mp = new MediaPlayer();
            // Listeners
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub

                }
            }); // Important

            mp.reset();

            mp.setDataSource(fileStringUrl);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mp.prepare();
            mp.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //method to show file chooser
    private void showFileChooser1() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "AudioRecorder");
        intent.setDataAndType(uri,"audio/*");

        startActivityForResult(Intent.createChooser(intent, "result"), PICK_IMAGE_REQUEST);
    }


    //method to show file chooser
    private void showFileChooser2() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "AudioRecorder");
        intent.setDataAndType(uri,"audio/*");

        startActivityForResult(Intent.createChooser(intent, "result"), PICK_IMAGE_REQUEST2);
    }
/*
    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath1 = data.getData();
            fileStringUrl1 =filePath1.toString();
            File myfile1=new File(fileStringUrl1);
            fileString1 = myfile1.getAbsolutePath();
            fileName1=myfile1.getName();
            textSong1.setText(filePath1.toString());
        }
        else if (requestCode == PICK_IMAGE_REQUEST2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath2 = data.getData();
            fileStringUrl2 =filePath2.toString();
            File myfile2=new File(fileStringUrl2);
            fileString2 = myfile2.getAbsolutePath();
            fileName2=myfile2.getName();
            textSong2.setText(fileName2);
        }
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        // 有選擇檔案
        if ( resultCode == Activity.RESULT_OK ) {
            if(requestCode == PICK_IMAGE_REQUEST) {
                // 取得檔案的 Uri
                Uri selectedFileUri = data.getData();
                if( selectedFileUri != null )
                {
                    fileStringUrl1 = FilePath.getPath(this,selectedFileUri);

                    if(fileStringUrl1 != null && !fileStringUrl1.equals("")){
                        selectedFileName1 = fileStringUrl1.substring(fileStringUrl1.lastIndexOf("/")+1);
                        textSong1.setText(selectedFileName1);
                    }
                }
                else {
                    Toast.makeText(this,"無效的檔案路徑 !!",Toast.LENGTH_SHORT).show();
                }
            }else if(requestCode == PICK_IMAGE_REQUEST2) {
                // 取得檔案的 Uri
                Uri selectedFileUri = data.getData();
                if (selectedFileUri != null) {
                    fileStringUrl2 = FilePath.getPath(this, selectedFileUri);

                    if (fileStringUrl2 != null && !fileStringUrl2.equals("")) {
                        selectedFileName2 = fileStringUrl2.substring(fileStringUrl2.lastIndexOf("/") + 1);
                        textSong2.setText(selectedFileName2);
                    }
                }
            }
                else {
                    Toast.makeText(this,"無效的檔案路徑 !!",Toast.LENGTH_SHORT).show();
                }
        } else {
            Toast.makeText(this, "取消選擇檔案 !!", Toast.LENGTH_SHORT).show();
        }
    }

   /* private void mixSound() throws IOException {

        //File file1=new File("/storage/emulated/0/AudioRecorder/1514200163232.wav");
        //File file2=new File("/storage/emulated/0/AudioRecorder/1514200177789.wav");
        //InputStream in1 = new FileInputStream(file1);
        //InputStream in2 = new FileInputStream(file2);

         InputStream in1 = getResources().openRawResource(R.raw.rec1_mono_high);
         InputStream in2 = getResources().openRawResource(R.raw.music1_mono_high);

        Wave w1 = new Wave(in1);
        short[] music1 = w1.getSampleAmplitudes();

        in1.close();


        Wave w2 = new Wave(in2);
        short[] music2 = w2.getSampleAmplitudes();
        in2.close();

        byte[] output = new byte[(music1.length > music2.length) ? music2.length
                : music1.length];

        for (int i = 0; i < output.length; i++) {

            float samplef1 = (music1[i] / 128.0f)*1.2f; // 2^7=128
            float samplef2 = (music2[i] / 128.0f)*1.2f;

            float mixed = (samplef1 + samplef2)/2;
            // reduce the volume a bit:
//			mixed *= 0.8;
            // hard clipping
//			if (mixed > 1.0f)
//				mixed = 1.0f;
//
//			if (mixed < -1.0f)
//				mixed = -1.0f;

            byte outputSample = (byte) (mixed * 128.0f);
            output[i] = outputSample;

        } // for loop


        File f = new File("/storage/emulated/0/AudioRecorder/12345.wav");
        if (f.exists()) {
            f.delete();
        }

        FileOutputStream fo = new FileOutputStream(f);

        WaveHeader wh = w2.getWaveHeader();
        MyWaveHeader mwh = new MyWaveHeader((short) wh.getAudioFormat(),
                (short) wh.getChannels(), wh.getSampleRate(),
                (short) wh.getBitsPerSample(), output.length);
        mwh.write(fo);

        fo.write(output);
        fo.flush();
        fo.close();

        // MusicPlayer
        MediaPlayer mp = new MediaPlayer();
        // Listeners
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub

            }
        }); // Important

        mp.reset();

        mp.setDataSource("/storage/emulated/0/AudioRecorder/12345.wav");
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mp.prepare();
        mp.start();

        // AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
        // 11025, AudioFormat.CHANNEL_IN_DEFAULT,
        // AudioFormat.ENCODING_PCM_16BIT, output.length,
        // AudioTrack.MODE_STREAM);
        // audioTrack.play();
        // audioTrack.write(output, 0, output.length);

    }*/
    protected String add0(int n) {
        if (n < 10) return ("0" + n);
        else return ("" + n);
    }


    private void mixSoundHigh() throws IOException {


       // File file1=new File("/storage/emulated/0/AudioRecorder/1514224098953.wav");
        //File file2=new File("/storage/emulated/0/AudioRecorder/1514224173318.wav");
        //File file1=new File("/mnt/sdcard/bird.wav");
        //File file2=new File("/mnt/sdcard/bell.wav");
       // InputStream in1 = getResources().openRawResource(R.raw.piano);
        //InputStream in2 = getResources().openRawResource(R.raw.testwav1);
        File file1=new File(fileStringUrl1);
        File file2=new File(fileStringUrl2);
        InputStream in1 = new FileInputStream(file1);
        InputStream in2 = new FileInputStream(file2);

        Wave w1 = new Wave(in1);
        short[] music1 = w1.getSampleAmplitudes();

        in1.close();


        Wave w2 = new Wave(in2);
        short[] music2 = w2.getSampleAmplitudes();
        in2.close();

        byte[] output = new byte[(music1.length > music2.length) ? music2.length
                : music1.length];

        for (int i = 0; i < output.length; i++) {

            float samplef1 = music1[i] / 32768.0f; // 2^7=128
            float samplef2 = music2[i] / 32768.0f;

            float mixed = (samplef1 + samplef2)/2;
//			 reduce the volume a bit:
			mixed *= 0.8;
//			 hard clipping
            if (mixed > 1.0f)
                mixed = 1.0f;

            if (mixed < -1.0f)
                mixed = -1.0f;

            byte outputSample = (byte) (mixed * 32768.0f);

            output[i] = outputSample;

        } // for loop

        //File f = new File("/storage/emulated/0/AudioRecorder/"+temFile+".wav");
        File f=new File(recPATH + "/" + temFile + ".wav");
        if (f.exists()) {
            f.delete();
        }

        FileOutputStream fo = new FileOutputStream(f);

        WaveHeader wh = w2.getWaveHeader();
        MyWaveHeader mwh = new MyWaveHeader((short) wh.getAudioFormat(),
                (short) wh.getChannels(), wh.getSampleRate(),
                (short) wh.getBitsPerSample(), output.length);
        mwh.write(fo);

        fo.write(output);
        fo.flush();
        fo.close();

        // MusicPlayer
        MediaPlayer mp = new MediaPlayer();
        // Listeners
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub

            }
        }); // Important

        mp.reset();

        //mp.setDataSource("/storage/emulated/0/AudioRecorder/"+temFile+".wav");
        mp.setDataSource(recPATH + "/" + temFile + ".wav");
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mp.prepare();
        mp.start();

        // AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
        // 11025, AudioFormat.CHANNEL_IN_DEFAULT,
        // AudioFormat.ENCODING_PCM_16BIT, output.length,
        // AudioTrack.MODE_STREAM);
        // audioTrack.play();
        // audioTrack.write(output, 0, output.length);

    }

    private void CombineWaveFile(String file1, String file2) {
        FileInputStream in1 = null, in2 = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

        byte[] data = new byte[bufferSize];


        try {

            in1 = new FileInputStream(file1);
            in2 = new FileInputStream(file2);

            File f = new File("/storage/emulated/0/AudioRecorder/mixData1.wav");
            if (f.exists()) {
                f.delete();
            }

            out = new FileOutputStream(f);

            totalAudioLen = in1.getChannel().size() + in2.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while (in1.read(data) != -1) {

                out.write(data);

            }
            while (in2.read(data) != -1) {

                out.write(data);
            }

            out.close();
            in1.close();
            in2.close();

            Toast.makeText(this, "Done!!", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte)(totalDataLen & 0xff);
        header[5] = (byte)((totalDataLen >> 8) & 0xff);
        header[6] = (byte)((totalDataLen >> 16) & 0xff);
        header[7] = (byte)((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte)(longSampleRate & 0xff);
        header[25] = (byte)((longSampleRate >> 8) & 0xff);
        header[26] = (byte)((longSampleRate >> 16) & 0xff);
        header[27] = (byte)((longSampleRate >> 24) & 0xff);
        header[28] = (byte)(byteRate & 0xff);
        header[29] = (byte)((byteRate >> 8) & 0xff);
        header[30] = (byte)((byteRate >> 16) & 0xff);
        header[31] = (byte)((byteRate >> 24) & 0xff);
        header[32] = (byte)(2 * 16 / 8);
        header[33] = 0;
        header[34] = RECORDER_BPP;
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte)(totalAudioLen & 0xff);
        header[41] = (byte)((totalAudioLen >> 8) & 0xff);
        header[42] = (byte)((totalAudioLen >> 16) & 0xff);
        header[43] = (byte)((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

}
