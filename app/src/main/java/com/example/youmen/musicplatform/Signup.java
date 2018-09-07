package com.example.youmen.musicplatform;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by youmen on 2017/9/25.
 */
public class Signup extends Activity{

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private EditText edEmail,edPw,edUid;
    private String edUid_input,emailstr,estr,pwstr;
    private DatabaseReference mDatabase;
    private Thread mapthread=null;
    private Handler handler = new Handler();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //mDatabase = FirebaseDatabase.getInstance().getReference();

        edEmail = (EditText) findViewById(R.id.ed_email);
        edPw = (EditText) findViewById(R.id.ed_pw);
        edUid=(EditText)findViewById(R.id.ed_uid);
        edPw.setTransformationMethod(PasswordTransformationMethod.getInstance());
        //FirebaseAuth auth = FirebaseAuth.getInstance();
        //FirebaseUser user = auth.getCurrentUser();


        final Button btnSignup=(Button) findViewById(R.id.btn_signup);
       /* edUid.setOnKeyListener(new EditText.OnKeyListener(){
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                edUid_input=edUid.getText().toString();
                return false;
            }
        });*/
       // edUid_input=edUid.getText().toString();



        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailstr=edEmail.getText().toString();
                edUid_input=edUid.getText().toString();
                pwstr=edPw.getText().toString();
                AlertDialog();
            }
        });

    }

    public void AlertDialog(){

        LayoutInflater inflater=LayoutInflater.from(Signup.this);
        final View v=inflater.inflate(R.layout.alertdialog_upload,null);
        new AlertDialog.Builder(Signup.this)
                .setTitle("註冊  ")
                .setView(v)
                .setMessage("ID:"+edUid_input+"\n"+"Email:"+emailstr)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signup();
                    }
                }).show();

    }

    public void signup(){
        if(edUid_input!=null&&pwstr!=null&&emailstr!=null){
            auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(emailstr,pwstr)
                    .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Signup.this, "success!", Toast.LENGTH_SHORT).show();


                               /* Map<String, String> map = new HashMap<>();
                                map.put("Email",emailstr);
                                estr=emailstr.replaceAll("\\.","_");
                                mDatabase.child("User").child(estr).setValue(map);
                                */


                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        estr=emailstr.replaceAll("\\.","_");

                                        DatabaseReference myRef = FirebaseDatabase.getInstance()
                                                .getReferenceFromUrl("https://music-platform-18908.firebaseio.com/User");
                                        myRef.child(estr).child("User_ID").setValue(edUid_input);
                                        myRef.child(estr).child("Email").setValue(emailstr);

                                        intent();
                                    }
                                },1000);

                            } else {
                                Toast.makeText(Signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            Toast.makeText(Signup.this,"please input your info", Toast.LENGTH_SHORT).show();
        }
    }

    public void intent(){
        Intent intent=new Intent();
        intent.setClass(Signup.this,Login.class);
        startActivity(intent);
    }


}
