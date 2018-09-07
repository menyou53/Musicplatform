# Musicplatform

API:26   CPU/ABI:x86


-------------------search class name for code detailed description------------------


Login 

啟動app時判斷是否已登入

FirebaseUser user = auth.getCurrentUser();
取得登入中的user.
if (user != null) {
    emailstr = user.getEmail().toString();
    estr = emailstr.replaceAll("\\.", "_");
    GlobalVariable globalVariable = (GlobalVariable) getApplicationContext();
    globalVariable.Estr = estr;
}
如果正在登入中,將email存到GlobalVariable.
if (user != null) {
    Intent intent = new Intent();
    intent.setClass(Login.this, Welcome.class);
    startActivity(intent);
}
如果是登入狀態,進入Welcome.

private void handleFacebookAccessToken(AccessToken token) {
    Log.d(TAG, "handleFacebookAccessToken:" + token);
    AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI();
                        if (user != null) {
                            Intent intent = new Intent();
                            intent.setClass(Login.this, Welcome.class);
                            startActivity(intent);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(Login.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
}
使用facebook登入,登入成功取得user資料,呼叫updateUI(),更新料庫,進入Welcome.失敗跳出"Authentication failed.”訊息

——————————Welcome——————————		
mDatabase=FirebaseDatabase.getInstance().getReference("User");
final DatabaseReference uidRef= mDatabase.child(estr);
mDatabase.orderByChild("Email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final User_info_connect user_info_connect = dataSnapshot.getValue(User_info_connect.class);
        Log.e("Data snapshot","test:"+dataSnapshot);
        uidRef.child("User_ID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UID=dataSnapshot.getValue().toString();
                textset();
                Log.e("Data snapshot uid","test uid:"+User_ID);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
利用登入中user的email比對資料庫,找到User_ID欄位底下的資料

public void textset(){
    GlobalVariable globalVariable = (GlobalVariable)getApplicationContext();
    globalVariable.UserID=UID;
    User_ID=globalVariable.UserID;

    welcomeText.setText("Welcome!"+User_ID);
}
將User_ID存到GlobalVariable,並在畫面上顯示"Welcome!”+User_ID.

MainActivity(music）
錄音
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
按第一次按鈕啟動執行緒gorecord,確定目前沒有在錄製,執行startRecording()『設定錄音的參數』,執行writeAudioDataToFile()『錄製wav檔案』.
第二次按按鈕則stopRecording(),釋放錄音執行緒,執行copyWaveFile(getTempFilename(), filename)並輸入wav所需的在header資訊（聲道,頻率....）,把錄音檔存成wav,deleteTempFile()刪除暫存錄音檔,設定延遲1秒後tomp3(),利用ffmpeg將wav轉檔成mp3


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
設定節拍器點擊事件,第一次點擊取得輸入的bpm值並將圖片從黑色變更紅色節拍器,執行StartLockWindowTimer(),依循輸入的bpm值產生delay並重複發出聲音.
第二次點擊停止街拍器,將圖片變回黑色節拍器.

imgMix.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        AlertDialog();
    }
});
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
 if(requestCode == PICK_AUDIO_REQUEST2 && resultCode == RESULT_OK && data != null && data.getData() != null){
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
設定點擊混音圖片事件,點擊後彈出選擇第一首音樂的視窗,選擇後彈出選擇第二首音樂的視窗,選完後建立mix的執行緒執行mixaudio(),利用ffmpeg command執行混音
,結束後執行recList(),更新listview音樂列表,檢查資料夾內的mp3檔案新增到listview,完成後關閉執行緒.
 

imgImportVideo.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showFileChooser4();
        }
    });
}
設定匯入影片變成mp3的事件,同混音流程,彈出視窗,檔案選擇器,建立轉檔執行緒,利用ffmpeg command進行轉檔,結束後執行recList(),更新listview音樂列表,檢查資料夾內的mp3檔案新增到listview,結束執行緒.
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
}
設定長按音樂列表事件（刪除音樂）,彈出警告視窗（確定/取消）,確定刪除->取得檔案路徑,刪除檔案,並從列表移除lstArray.remove(cListItem).

———————————VideoActivity———————————
final ImageView btnChooseVideo=(ImageView) findViewById(R.id.btn_chooseVideo);
btnChooseVideo.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        showFileChooser();
    }
});
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
設定影片選擇事件,進入檔案選擇器,選擇完後獲得影片路徑,開啟mediaplayer,在videoview裡播放影片.

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
設定裁切影片的事件,點擊按鈕後判斷是否有已選擇正在播放的影片,如果有,暫停影片,取得影片目前播放時間,建立執行緒,利用ffmpeg command裁切影片,裁切完後將處理完的影片位置傳給videoview繼續播放.

final ImageView btnMux=(ImageView) findViewById(R.id.btn_mux);
btnMux.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        showFileChooserMux();
        videoView.stopPlayback();
    }
});
設定影音混合事件,點擊按鈕暫停影片,進入檔案選則器選擇要與影片混合的音樂,選擇後利用ffmpeg command做mux,完成後將處理完的影片位置傳給videoview繼續播放.

Camera2VideoImageActivity

final ImageView musicvplay=(ImageView)findViewById(R.id.btn_musicvplay);
musicvplay.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        showFileChooser();
    }
});
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // TODO Auto-generated method stub
    super.onActivityResult(requestCode, resultCode, data);
if ( resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
    if (requestCode == PICK_FILE_REQUEST) {
        // 取得檔案的 Uri
        Uri selectedFileUri = data.getData();
        if (selectedFileUri != null) {
            fileStringUrl = FilePath.getPath(this, selectedFileUri);

            if (fileStringUrl != null && !fileStringUrl.equals("")) {
                selectedFileName = fileStringUrl.substring(fileStringUrl.lastIndexOf("/") + 1);
                musicname.setText(selectedFileName);
            }

        } else {
            Toast.makeText(this, "無效的檔案路徑 !!", Toast.LENGTH_SHORT).show();
        }
    }
}
}
點擊選歌按鈕開啟檔案選擇器,更改textview成歌名.


    mRecordImageButton = (ImageButton) findViewById(R.id.videoOnlineImageButton);
    mRecordImageButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mIsRecording || mIsTimelapse) {
                mChronometer.stop();
                mChronometer.setVisibility(View.INVISIBLE);
                mIsRecording = false;
                mIsTimelapse = false;
                mRecordImageButton.setImageResource(R.mipmap.btn_video_online);
                // Starting the preview prior to stopping recording which should hopefully
                // resolve issues being seen in Samsung devices.
                startPreview();
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                Intent mediaStoreUpdateIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaStoreUpdateIntent.setData(Uri.fromFile(new File(mVideoFileName)));
                sendBroadcast(mediaStoreUpdateIntent);
                if(mediaplayer.isPlaying()){
                    mediaplayer.reset();
                }

            } else {
                mIsRecording = true;
                mRecordImageButton.setImageResource(R.mipmap.btn_video_busy);
                checkWriteStoragePermission();
                if(fileStringUrl!=null){
                    playmusic();
                }
            }
        }
    });
    mRecordImageButton.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            mIsTimelapse =true;
            mRecordImageButton.setImageResource(R.mipmap.btn_timelapse);
            checkWriteStoragePermission();
            return true;
        }
    });
}
設定點擊錄影按鈕事件,錄影部分用git上面android camera2的code,額外：開始錄影同時播放選擇的音樂,並顯示錄影時間.

SplitScreenVideo


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

分別設定3種類型的分割影片點擊事件,給個一種flag標記,差別在檔案選擇器選擇完兩個影片時如果flag=1就直接跳出「是否製作影片」的視窗,如果flag＝2則會跳出「選擇影片3
」的視窗,選擇完才跳出「是否製作影片」的視窗,flag=4同理.還有在「是否製作影片」的視窗如果flag=1會顯示video1=____,video2=____,如果flag=2則顯示video1=____,video2=____,video3＝_____,flag=4同理.


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
分割影片一樣開執行緒使用ffmpeg command,開始執行跳出「請等待」的提示視窗,結束時關閉視窗及執行緒.


UploadFile
選擇檔案->輸入上傳名稱->上傳（alertdialog(是否上傳））->上傳檔案,資料庫新增上傳檔案資料->顯示上傳完成百分比

Download 
選擇收尋條件（使用者/檔案名稱）->輸入關鍵字->listview顯示搜尋結果->點擊listview內容（aletdialog（是否下載））->取得資料庫該檔案資料->資料庫更新下載次數＋1->顯示下載完成百分比
private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener(){
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()){
            case R.id.rbMusic:
                musicRB=false;
                break;
            case R.id.rbUser:
                musicRB=true;
                break;
        }
    }
};
設定checkbutton(選擇一個另一個就取消）
imgSearch.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(final View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("music");
        adapter.clear();

        if(musicRB==true){
            myRef.orderByChild("music_name").equalTo(editText.getText().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Music_info post = postSnapshot.getValue(Music_info.class);

                        adapter.add(post.getType()+"          "+post.getMusic_name()+"_"+post.getUser());
                        visibletype=post.getType();
                        String DlUrl=post.getMusic_name()+post.getUser();

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });
        }
        if(musicRB==false){
            myRef.orderByChild("user").equalTo(editText.getText().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Music_info post = postSnapshot.getValue(Music_info.class);
                        adapter.add(post.getType()+"          "+post.getMusic_name()+"_"+post.getUser());
                        visibletype=post.getType();
                        String DlUrl=post.getMusic_name()+post.getUser();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
});
設定兩種條件的搜尋,將取得的資料以「檔案類型         檔案名稱＿使用者名稱」的形式放進listview adapter裡,同時建立dowmload的url.

list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
          itemValue    = (String) arg0.getItemAtPosition(position);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               AlertDialog();
            }
        },500);
           Log.e("itemValue",itemValue);
        dlvalue=itemValue.replaceAll(visibletype+"          ","");
    }});
}
設定listview item點擊事件->dlvalue值設為 itemValue值去掉「visibletype+"          ",””」＝>>「檔案名稱＿使用者名稱」(與檔案在firebase中設定的儲存的路徑相同),設定延遲0.5秒後跳出alertdialog.

public void download(){
    final ProgressDialog progressDialog = new ProgressDialog(this);
    progressDialog.setTitle("Downloading");
    progressDialog.show();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference pathReference = storageRef.child("audio/"+dlvalue);

    final long ONE_MEGABYTE = 1024 * 1024 * 5;
    pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
        @Override
        public void onSuccess(byte[] bytes) {
            Log.e("sussess:","download sussess");

        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.e("error:","download error");
            // Handle any errors
        }
    });

    File rootPath = new File(Environment.getExternalStorageDirectory(), "MusicPlatform_download");
    if(!rootPath.exists()) {
        rootPath.mkdirs();
    }

    final File localFile = new File(rootPath,dlvalue+"."+visibletype);

    pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
            Log.e("firebase ",";local tem file created  created " +localFile.toString());
            progressDialog.dismiss();
            AlertDialog2();
            //  updateDb(timestamp,localFile.toString(),position);
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.e("firebase ",";local tem file not created  created " +exception.toString());
        }
    }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
        @Override
        public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                //calculating progress percentage
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                //displaying percentage in progress dialog
                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
        }
    });
}
檔案下載相關設定（local儲存路徑/firebase downloadUrl）,
（在local端建立一個暫時的檔案,名稱為點擊listview item時設的「dlvalue值.檔案類型」,將要下載的檔案存在該位置）,
顯示下載完成百分比,download成功->跳出alertdialog(告知下載完成）


   
