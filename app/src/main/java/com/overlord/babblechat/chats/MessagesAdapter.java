package com.overlord.babblechat.chats;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.overlord.babblechat.R;
import com.overlord.babblechat.common.Constants;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>{

    private Context context;
    private List<MessageModel> messageList;
    private FirebaseAuth firebaseAuth;

    public MessagesAdapter(Context context, List<MessageModel> messageList){
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @NotNull
    @Override
    public MessagesAdapter.MessageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_layout,parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessagesAdapter.MessageViewHolder holder, int position) {
        MessageModel message = messageList.get(position);
        firebaseAuth = FirebaseAuth.getInstance();
        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String fromUserId = message.getMessageFrom();

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String dateTime = sfd.format(new Date(message.getMessageTime()));
        String [] splitString = dateTime.split(" ");
        String messageTime = splitString[1];

        if(fromUserId.equals(currentUserId)){

            if(message.getMessageType().equals(Constants.MESSAGE_TYPE_TEXT)){
                holder.llSent.setVisibility(View.VISIBLE);
                holder.llSentImage.setVisibility(View.GONE);
            }
            else{
                holder.llSent.setVisibility(View.GONE);
                holder.llSentImage.setVisibility(View.VISIBLE);
            }

            holder.llReceived.setVisibility(View.GONE);
            holder.llReceivedImage.setVisibility(View.GONE);

            holder.tvSentMessage.setText(message.getMessage());
            holder.tvSentMessageTime.setText(messageTime);
            holder.tvImageSentTime.setText(messageTime);
            Glide.with(context)
                    .load(message.getMessage())
                    .placeholder(R.drawable.ic_image)
                    .into(holder.ivSent);
        }
        else{
            if(message.getMessageType().equals(Constants.MESSAGE_TYPE_TEXT)){
                holder.llReceived.setVisibility(View.VISIBLE);
                holder.llReceivedImage.setVisibility(View.GONE);
            }
            else{
                holder.llReceived.setVisibility(View.GONE);
                holder.llReceivedImage.setVisibility(View.VISIBLE);
            }
            holder.llSent.setVisibility(View.GONE);
            holder.llSentImage.setVisibility(View.GONE);

            holder.tvReceivedMessage.setText(message.getMessage());
            holder.tvReceivedMessageTime.setText(messageTime);
            holder.tvImageReceivedTime.setText(messageTime);
            Glide.with(context)
                    .load(message.getMessage())
                    .placeholder(R.drawable.ic_image)
                    .into(holder.ivReceived);
        }

        holder.clMessage.setTag(R.id.TAG_MESSAGE, message.getMessage());
        holder.clMessage.setTag(R.id.TAG_MESSAGE_ID, message.getMessageId());
        holder.clMessage.setTag(R.id.TAG_MESSAGE_TYPE, message.getMessageType());

        holder.clMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageType = view.getTag(R.id.TAG_MESSAGE_TYPE).toString();
                Uri uri = Uri.parse(view.getTag(R.id.TAG_MESSAGE).toString();
                if(messageType.equals(Constants.MESSAGE_TYPE_VIDEO)){
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setDataAndType(uri, "video/mp4");
                    context.startActivity(intent);
                }
                else if(messageType.equals(Constants.MESSAGE_TYPE_IMAGE){
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setDataAndType(uri, "image/jpg");
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llSent, llReceived, llSentImage, llReceivedImage;
        private TextView tvSentMessage, tvSentMessageTime, tvReceivedMessage, tvReceivedMessageTime;
        private ImageView ivSent, ivReceived;
        private TextView tvImageSentTime, tvImageReceivedTime;
        private ConstraintLayout clMessage;


        public MessageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            llSent = itemView.findViewById(R.id.llSent);
            llReceived = itemView.findViewById(R.id.llReceived);
            tvSentMessage = itemView.findViewById(R.id.tvSentMessage);
            tvSentMessageTime = itemView.findViewById(R.id.tvSentMessageTime);
            tvReceivedMessage = itemView.findViewById(R.id.tvReceivedMessage);
            tvReceivedMessageTime = itemView.findViewById(R.id.tvReceivedMessageTime);
            llSentImage = itemView.findViewById(R.id.llSentImage);
            llReceivedImage = itemView.findViewById(R.id.llReceivedImage);
            ivSent = itemView.findViewById(R.id.ivSent);
            ivReceived = itemView.findViewById(R.id.ivReceived);
            tvImageSentTime = itemView.findViewById(R.id.tvSentImageTime);
            tvImageReceivedTime = itemView.findViewById(R.id.tvReceivedImageTime);







            clMessage = itemView.findViewById(R.id.clMessage);
        }
    }
}
