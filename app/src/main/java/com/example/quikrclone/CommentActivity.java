package com.example.quikrclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    TextView tvTitle;
    RecyclerView recyclerView;
    ImageView imageView;
    CommentAdapter adapter;
    TextInputEditText textInputEditText;
    MaterialButton materialButton;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    String phone,userName;
    ArrayList<Comment> ap=new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        imageView = findViewById(R.id.imgBack);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Comments");

        textInputEditText = findViewById(R.id.comment);
        materialButton = findViewById(R.id.add);

        String id = getIntent().getStringExtra("id");

        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone =         mCurrentUser.getPhoneNumber();
                phone = phone.substring(3);
                firebaseFirestore.collection("Users").document(phone).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull  Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            userName = (String) document.get("name");
                            Map<String,Object> mp= new HashMap<>();
                            mp.put("name", userName);
                            mp.put("comment",textInputEditText.getText().toString().trim());
                            firebaseFirestore.collection("Posts").document(id).collection("Comments").add(mp).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful())
                                    {
                                        textInputEditText.setText("");
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new CommentAdapter(ap);

        LinearLayoutManager l = new LinearLayoutManager(CommentActivity.this);
        l.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(l);
        recyclerView.setAdapter(adapter);



        firebaseFirestore.collection("Posts").document(id).collection("Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentChange doc: value.getDocumentChanges())
                {

                    if(doc.getType()==DocumentChange.Type.ADDED)
                    {
                        String blogPostId= doc.getDocument().getId();
                        ap.add(doc.getDocument().toObject(Comment.class));

                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}