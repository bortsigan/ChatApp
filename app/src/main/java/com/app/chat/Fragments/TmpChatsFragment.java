package com.app.chat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.chat.Adapter.UserAdapter;
import com.app.chat.Model.Chat;
import com.app.chat.Model.Chatlist;
import com.app.chat.Model.User;
import com.app.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TmpChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    DatabaseReference dbReference;
    FirebaseUser fbUser;

    private List<Chatlist> usersList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);


        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        dbReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fbUser.getUid());
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        dbReference = FirebaseDatabase.getInstance().getReference("Chats");
//        dbReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                usersList.clear();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren())  {
//                    Chat chat = snapshot.getValue(Chat.class);
//
//                    if (chat.getSender().equalsIgnoreCase(fbUser.getUid())) {
//                        usersList.add(chat.getReceiver());
//                    }
//
//                    if (chat.getReceiver().equalsIgnoreCase(fbUser.getUid())) {
//                        usersList.add(chat.getSender());
//                    }
//                }
//
//                readChats();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        return view;

    }

    public void chatList() {
        mUsers = new ArrayList<>();
        dbReference = FirebaseDatabase.getInstance().getReference("Users");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User  user = snapshot.getValue(User.class);
                    for (Chatlist chatlist : usersList) {
                        if (user.getId().equals(chatlist.getId())) {
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter  = new UserAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    private void readChats()
//    {
//        mUsers = new ArrayList<>();
//        final ArrayList<String> list = new ArrayList<>(2);
//
//        dbReference = FirebaseDatabase.getInstance().getReference("Users");
//
//        dbReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mUsers.clear();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    User user = snapshot.getValue(User.class);
//
//                    for (String id : usersList) {
//                        /*
//                        if (user.getId() != null && !user.getId().equals(firebaseUser.getUid())) {
//                            users.add(user);
//                        }*/
//                        if ( user.getId().equalsIgnoreCase(id)) {
//                            if (mUsers.size() > 0) {
//                                for (User user1 : new ArrayList<User>(mUsers)) {
//                                    if (!user.getId().equalsIgnoreCase(user1.getId())) {
//                                        if (!list.contains(user.getId())) {
//                                            mUsers.add(user);
//                                        }
//                                        list.add(user.getId());
//                                    }
//                                }
//                            } else {
//                                if (!list.contains(user.getId())) {
//                                    mUsers.add(user);
//                                }
//                                list.add(user.getId());
//                            }
//                        }
//                    }
//                }
//
//                userAdapter = new UserAdapter(getContext(), mUsers, true);
//                recyclerView.setAdapter(userAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}