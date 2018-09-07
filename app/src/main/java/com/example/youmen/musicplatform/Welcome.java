package com.example.youmen.musicplatform;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by youmen on 2017/9/25.
 */

public class Welcome extends Activity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference mDatabase;
    private ArrayList<User_info> list=new ArrayList<>();
    private String userId,userEmail;
    private String temFile,User_ID,UID;
    private File recFile, recPATH;
    private Handler handler = new Handler();
    private Thread namethread=null;
    private DataSnapshot dataSnapshot;
    ArrayList<User_info> users=new ArrayList<>();
    private TextView welcomeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeText=(TextView) findViewById(R.id.welcome_title);

        recPATH = Environment.getExternalStorageDirectory();
        File sdFile = android.os.Environment.getExternalStorageDirectory();
        String path = sdFile.getPath() + File.separator + "AudioRecorder";

        File dirFile = new File(path);
        recPATH = dirFile;

        if (!dirFile.exists()) {//如果資料夾不存在

            dirFile.mkdir();//建立資料夾
            temFile = dirFile.getAbsolutePath();
        }
        GlobalVariable globalVariable = (GlobalVariable)getApplicationContext();
        String estr=globalVariable.Estr;
        Log.e("Get",estr);

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(user!=null) {
            userEmail = user.getEmail().toString();
        }



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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        final ImageView imgMusic=(ImageView)findViewById(R.id.imgMusic);
        imgMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(Welcome.this,MainActivity.class);
                startActivity(intent);
            }
        });


        final ImageView imgVideo=(ImageView)findViewById(R.id.imgVideo);
        imgVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(Welcome.this,VideoActivity.class);
                startActivity(intent);
            }
        });

        final ImageView imgSearch=(ImageView)findViewById(R.id.imgSearch);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(Welcome.this,Download.class);
                startActivity(intent);
            }
        });


        final ImageView imgUpload=(ImageView)findViewById(R.id.imgUpload);
        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(Welcome.this,UploadFile.class);
                startActivity(intent);
            }
        });



        final ImageView imgLogout=(ImageView) findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent();
                intent.setClass(Welcome.this,Login.class);
                startActivity(intent);
            }
        });




    }

    public void textset(){
        GlobalVariable globalVariable = (GlobalVariable)getApplicationContext();
        globalVariable.UserID=UID;
        User_ID=globalVariable.UserID;

        welcomeText.setText("Welcome!"+User_ID);
    }


}
