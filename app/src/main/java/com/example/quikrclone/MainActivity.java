package com.example.quikrclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PostAdapter adapter;
    ArrayList<Post> ap=new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;

    TextView tvTitle;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        if(mCurrentUser == null){
            sendUserToLogin();
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.inflateMenu(R.menu.options_menu);

        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Quikr");
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddNewPost.class);
                startActivity(intent);
            }
        });
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new PostAdapter(ap);

        LinearLayoutManager l = new LinearLayoutManager(MainActivity.this);
        l.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(l);
        recyclerView.setAdapter(adapter);

        firebaseFirestore.collection("Posts").orderBy("time_stamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable  QuerySnapshot value, @Nullable  FirebaseFirestoreException error) {
                for(DocumentChange doc: value.getDocumentChanges())
                {

                    if(doc.getType()==DocumentChange.Type.ADDED)
                    {
                        String blogPostId= doc.getDocument().getId();
                        ap.add(doc.getDocument().toObject(Post.class).withId(blogPostId));

                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }
    private void sendUserToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.log_out)
        {
            mAuth.signOut();
            sendUserToLogin();
        }
        return true;
    }
}