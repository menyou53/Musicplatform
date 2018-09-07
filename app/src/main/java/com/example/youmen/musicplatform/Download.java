package com.example.youmen.musicplatform;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Created by youmen on 2017/10/20.
 */

public class Download extends Activity {
    boolean musicRB=true;
    private RadioGroup radioGroup;
    private RadioButton rbMusic,rbUser;
    private String   itemValue,dlvalue,visibletype;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        RadioGroup radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        RadioButton rbMusic=(RadioButton)findViewById(R.id.rbMusic);
        RadioButton rbUser=(RadioButton)findViewById(R.id.rbUser);
        rbMusic.setChecked(true);
        rbMusic.setOnCheckedChangeListener(mOnCheckedChangeListener);
        rbUser.setOnCheckedChangeListener(mOnCheckedChangeListener);
        ListView list = (ListView) findViewById(R.id.listDownload);
        final EditText editText = (EditText) findViewById(R.id.edtSearch);
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        list.setAdapter(adapter);

        File rootPath = new File(Environment.getExternalStorageDirectory(), "MusicPlatform_download");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final ImageView imgSearch=(ImageView)findViewById(R.id.imgSearchFiLe);
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
                                Log.e("Firebase", "Value is:" + post.getMusic_name());
                                Log.e("Firebase", "Value is:" + post.getUser());
                                Log.e("Firebase", "Value is:" + post.getDownload_times());
                                Log.e("Firebase", "Value is:" + post.getType());
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
                                Log.e("Firebase", "Value is:" + post.getMusic_name());
                                Log.e("Firebase", "Value is:" + post.getUser());
                                Log.e("Firebase", "Value is:" + post.getDownload_times());
                                Log.e("Firebase", "Value is:" + post.getType());
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
                Log.e("dlvalue",dlvalue);
                Log.e("visibletype",visibletype);

            }});
        }


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

    public void AlertDialog(){

        LayoutInflater inflater=LayoutInflater.from(Download.this);
        final View v=inflater.inflate(R.layout.alertdialog_record_name,null);

        new AlertDialog.Builder(Download.this)
                .setTitle("是否下載"+dlvalue)
                .setPositiveButton("確定",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int whitch){
                        download();
                    }
                })
                .show();
    }



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

                // Data for "images/island.jpg" is returns, use this as needed
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

    public void AlertDialog2(){

        LayoutInflater inflater=LayoutInflater.from(Download.this);
        final View v=inflater.inflate(R.layout.alertdialog_upload,null);
        new AlertDialog.Builder(Download.this)
                .setTitle("下載完成  ")
                .setView(v)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      }
                }).show();

    }
}

