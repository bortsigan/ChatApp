package com.app.chat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.chat.MessageActivity;
import com.app.chat.Model.Chat;
import com.app.chat.Model.User;
import com.app.chat.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context context;
    private List<User> users;
    private boolean isChat;

    String theLastMessage;

    public UserAdapter(Context context, List<User> users, boolean isChat) {
        this.context    = context;
        this.users      = users;
        this.isChat     = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = users.get(position);
        int setImgOn    = View.GONE;
        int setImgOff   = View.VISIBLE;

        holder.username.setText(user.getUsername());

        if (user.getImageUrl().equalsIgnoreCase("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(user.getImageUrl()).into(holder.profile_image);
        }

        if (isChat) {
            lastMessage(user.getId(), holder.last_msg);
            if (user.getStatus().equalsIgnoreCase("online")) {
                setImgOff   = View.GONE;
                setImgOn    = View.VISIBLE;
            }
        } else {
            holder.last_msg.setVisibility(View.GONE);
            setImgOff    = View.GONE;
            setImgOn     = View.VISIBLE;
        }

        holder.img_on.setVisibility(setImgOn);
        holder.img_off.setVisibility(setImgOff);

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userId", user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username        = itemView.findViewById(R.id.username);
            profile_image   = itemView.findViewById(R.id.profile_image);
            img_on          = itemView.findViewById(R.id.img_on);
            img_off         = itemView.findViewById(R.id.img_off);
            last_msg        = itemView.findViewById(R.id.last_msg);
        }
    }

    private void lastMessage(final String userId, final TextView last_msg) {
        theLastMessage = "default";

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Chats");

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (
                            chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())
                        ) {
                        theLastMessage = chat.getMessage();
                    }
                }

                switch (theLastMessage) {
                    case "default"  :
                        last_msg.setText("No Message");
                        break;
                    default:
                        last_msg.setText(theLastMessage);
                        break;

                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
