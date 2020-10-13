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

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private List<Chat> chats;
    private String imageUrl;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chat> chats, String imageUrl) {
        this.context    = context;
        this.chats      = chats;
        this.imageUrl   = imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.chat_item_left;

        if (viewType == MSG_TYPE_RIGHT) {
            layout = R.layout.chat_item_right;
        }

        View view = LayoutInflater.from(context).inflate(layout, parent, false);
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.message.setText(chat.getMessage());
        System.out.println("Chat : " + (chat.getMessage()));
        System.out.println("Chat IS SEEN : " + (chat.isSeen()));

        if (imageUrl.equalsIgnoreCase("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(imageUrl).into(holder.profile_image);
        }

        if (position == chats.size() -1) {

           if (chat.isSeen()) {
               holder.txt_seen.setVisibility(View.VISIBLE);
               holder.txt_seen.setText("Seen");
           } else {
               holder.txt_seen.setText("Delivered");
           }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView message;
        public ImageView profile_image;
        public TextView txt_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            message         = itemView.findViewById(R.id.show_message);
            profile_image   = itemView.findViewById(R.id.profile_image);
            txt_seen        = itemView.findViewById(R.id.txt_seen);
        }
    }

    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chats.get(position).getSender().equalsIgnoreCase(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        }
        return MSG_TYPE_LEFT;
    }
}