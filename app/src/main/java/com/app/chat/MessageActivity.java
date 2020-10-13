  package com.app.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.chat.Adapter.MessageAdapter;
import com.app.chat.Model.Chat;
import com.app.chat.Model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

  public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference dbReference;

    Intent intent;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> chat;

    RecyclerView recyclerView;

    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image   = findViewById(R.id.profile_image);
        username        = findViewById(R.id.username);
        btn_send        = findViewById(R.id.btn_send);
        text_send       = findViewById(R.id.text_send);

        intent = getIntent();
        final String userId = intent.getStringExtra("userId");

        btn_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String message = text_send.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessage(firebaseUser.getUid(), userId, message);
                } else {
                    Toast.makeText(MessageActivity.this, "Unable to send empty message", Toast.LENGTH_SHORT).show();
                }

                text_send.setText(null);
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());

                if (user.getImageUrl().equalsIgnoreCase("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);
                }

                readMessages(firebaseUser.getUid(), userId, user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userId);
    }

    private void sendMessage(String sender, String receiver, String message) {

        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("seen", false);
        dbReference.child("Chats").push().setValue(hashMap);

        intent = getIntent();
        final String userId = intent.getStringExtra("userId");

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenMessage(final String userId) {
          dbReference = FirebaseDatabase.getInstance().getReference("Chats");
          seenListener = dbReference.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                      Chat chat = snapshot.getValue(Chat.class);

                      if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)) {
                          HashMap<String, Object> hashMap = new HashMap<>();
                          hashMap.put("seen", true);
                          snapshot.getRef().updateChildren(hashMap);
                      }
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });
      }

    private void readMessages(final String id, final String userId, final String imageUrl) {
        chat = new ArrayList<>();

        dbReference = FirebaseDatabase.getInstance().getReference("Chats");

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat userChat = snapshot.getValue(Chat.class);
                    if (
                            userChat.getReceiver().equals(id) && userChat.getSender().equals(userId) ||
                                    userChat.getReceiver().equals(userId) && userChat.getSender().equals(id)
                    ) {
                        chat.add(userChat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, chat, imageUrl);
                    recyclerView.setAdapter(messageAdapter) ;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void status(String status)
    {
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("status", status);
        dbReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        dbReference.removeEventListener(seenListener);
        status("offline");
    }

}