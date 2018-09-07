package com.example.youmen.musicplatform;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class UploadFile extends AppCompatActivity {
    //a constant to track the file chooser intent
    private static final int PICK_IMAGE_REQUEST = 234;

    //Buttons
    private Button buttonChoose;
    private Button buttonUpload;

    //ImageView
    private ImageView imageView;
    private TextView textView;
    private EditText editText;
    //a Uri object to store file path
    private Uri filePath;
    private Thread namethread=null;
    private String fileString;
    private String fileStringUrl;
    private String fileName,input,userEmail,filetype,type;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadfile);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userEmail=user.getEmail();

        //getting views from layout
        final EditText editText=(EditText) this.findViewById(R.id.editText);

        input=editText.getText().toString();

        /*editText.setOnKeyListener(new EditText.OnKeyListener(){
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                input=editText.getText().toString();
                return false;
            }
        });*/


        final ImageView imgChooseFile=(ImageView)findViewById(R.id.img_chooseFile);
        imgChooseFile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showFileChooser();

            }}
        );

        final ImageView imgUploadFile=(ImageView)findViewById(R.id.img_uploadFile);

        imgUploadFile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                input=editText.getText().toString();
                AlertDialog();
            }}
        );





        //buttonUpload.setOnClickListener(this);
    }



    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        intent.setType("*/*");

        startActivityForResult(Intent.createChooser(intent, "result"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
           // fileStringUrl =filePath.toString();
            fileStringUrl=filePath.getPath().replace("/document/","");
            filetype=fileStringUrl.substring(fileStringUrl.length()-3);
            File myfile=new File(fileStringUrl);
            fileString = myfile.getAbsolutePath();
            fileName=myfile.getName();

            /*if(filetype=="mp3"){
                type="audio";
            }
            if(filetype=="mp4"){
                type="video";
            }*/
            Log.e("file",fileString);
            Log.e("filetype",filetype);
        }
    }

    //this method will upload the file
    private void uploadFile() {
        GlobalVariable globalVariable = (GlobalVariable) getApplicationContext();
        String UserID = globalVariable.UserID;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
                StorageReference riversRef = storageReference.child("audio/" + input + "_" + UserID);
                riversRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //if the upload is successfull
                                //hiding the progress dialog
                                progressDialog.dismiss();
                                //and displaying a success toast
                                Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //if the upload is not successfull
                                //hiding the progress dialog
                                progressDialog.dismiss();

                                //and displaying error message
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                //calculating progress percentage
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                //displaying percentage in progress dialog
                                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            }
                        });


                    String dl_url = input + "_" + UserID;
                    Music_info music_info = new Music_info(0, input, UserID, dl_url, filetype);   //(downloadtimes,music_name,userID)
                    mDatabase.child("music").child(input + "_" +filetype+"_"+ UserID).setValue(music_info);


        }
            //if there is not any file
            else {
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
                //you can display an error toast

        }
    }


    public void AlertDialog(){

        LayoutInflater inflater=LayoutInflater.from(UploadFile.this);
        final View v=inflater.inflate(R.layout.alertdialog_record_name,null);

        new AlertDialog.Builder(UploadFile.this)
                .setTitle("是否上傳"+input)
                .setPositiveButton("確定",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int whitch){
                        if(input!=null){
                            uploadFile();
                        }else{
                            Toast.makeText(getApplicationContext(), "please name the file", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

}