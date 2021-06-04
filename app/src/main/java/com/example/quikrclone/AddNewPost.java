package com.example.quikrclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddNewPost extends AppCompatActivity {

    TextView tvTitle;
    private static final int PICK_IMAGE_REQUEST = 234;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference =storage.getReference();
    AutoCompleteTextView editTextFilledExposedDropdown;
    ImageView imageView,imageView2;

    //a Uri object to store file path
    private Uri filePath;


    private int status=0;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    String phone,userName;

    TextInputEditText title,desc;
    Button button;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);

        title = findViewById(R.id.title);
        desc = findViewById(R.id.desc);
        button = findViewById(R.id.new_post_bttn);
        imageView =findViewById(R.id.new_post_image);

        imageView2 = findViewById(R.id.imgBack);

        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("New Post");

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();


        String[] type = new String[] {"Books", "Electronics", "Vehicle", "Cosmetics"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        AddNewPost.this,
                        R.layout.list_item,
                        type);

         editTextFilledExposedDropdown =
                findViewById(R.id.category);
        editTextFilledExposedDropdown.setAdapter(adapter);


        firebaseFirestore = FirebaseFirestore.getInstance();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(status==0)
               {
                   Toast.makeText(AddNewPost.this,"PLease select image to upload",Toast.LENGTH_SHORT).show();
               }
               else
               {
                   uploadFile();
               }
            }
        });

    }
    private void showFileChooser() {
        String[] mimeTypes = {"image/*"};
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            status=1;
            Log.d("filepath", filePath.toString());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //this method will upload the file
    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            final  String random_name = UUID.randomUUID().toString();
            StorageReference riversRef =  storageReference.child("Posts").child(random_name+ ".jpg");;
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            //and displaying a success toast

                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    //update firestore
                                    phone =         mCurrentUser.getPhoneNumber();
                                    phone = phone.substring(3);
                                    firebaseFirestore.collection("Users").document(phone).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull  Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                               userName = (String) document.get("name");
                                                Map<String,Object> mp= new HashMap<>();
                                                mp.put("time_stamp", FieldValue.serverTimestamp());
                                                mp.put("title",title.getText().toString().trim());
                                                mp.put("desc",desc.getText().toString().trim());
                                                mp.put("imageUrl",downloadUrl.toString());
                                                mp.put("category",editTextFilledExposedDropdown.getText().toString().trim());
                                                mp.put("userName",userName);
                                                firebaseFirestore.collection("Posts").add(mp).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            Intent intent = new Intent(AddNewPost.this,MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });

                                }
                            });



                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            status=2;

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
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}