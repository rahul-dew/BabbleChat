package com.overlord.babblechat.findFriends;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.overlord.babblechat.R;
import com.overlord.babblechat.common.Constants;
import com.overlord.babblechat.common.NodeNames;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.overlord.babblechat.R.string.request_sent_successful;

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.FindFriendViewHolder> {

    private Context context;
    private List<FindFriendModel> findFriendModelList;

    private DatabaseReference findFriendDatabase;
    private FirebaseUser currentUser;
    private String userID;


    public FindFriendsAdapter(Context context, List<FindFriendModel> findFriendModelList) {
        this.context = context;
        this.findFriendModelList = findFriendModelList;
    }

    @NonNull
    @NotNull
    @Override
    public FindFriendsAdapter.FindFriendViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.find_friend_layout, parent, false);
        return new FindFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FindFriendsAdapter.FindFriendViewHolder holder, int position) {
        FindFriendModel friendModel = findFriendModelList.get(position);
        holder.tvFullName.setText(friendModel.getUserName());
        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(Constants.IMAGES_FOLDER + "/" + friendModel.getPhotoName());
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .into(holder.ivProfile);
            }
        });

        findFriendDatabase = FirebaseDatabase.getInstance().getReference().child(NodeNames.FRIEND_REQUESTS);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(friendModel.getRequestSent()){
            holder.btSendRequest.setVisibility(View.GONE);
            holder.btCancelRequest.setVisibility(View.VISIBLE);
        }

        else{
            holder.btSendRequest.setVisibility(View.VISIBLE);
            holder.btCancelRequest.setVisibility(View.GONE);
        }

        holder.btSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btSendRequest.setVisibility(View.GONE);
                holder.pbRequest.setVisibility(View.VISIBLE);

                userID = friendModel.getUserId();
                findFriendDatabase.child(currentUser.getUid()).child(userID).child(NodeNames.REQUEST_TYPE)
                        .setValue(Constants.REQUEST_STATUS_SENT).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            findFriendDatabase.child(userID).child(currentUser.getUid()).child(NodeNames.REQUEST_TYPE)
                                    .setValue(Constants.REQUEST_STATUS_RECEIVED).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(context, request_sent_successful, Toast.LENGTH_SHORT).show();
                                        holder.pbRequest.setVisibility(View.GONE);
                                        holder.btCancelRequest.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        Toast.makeText(context,
                                                context.getString (R.string.request_send_failed , task.getException()),
                                                Toast.LENGTH_SHORT).show();
                                        holder.pbRequest.setVisibility(View.GONE);
                                        holder.btSendRequest.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        holder.btCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btCancelRequest.setVisibility(View.GONE);
                holder.pbRequest.setVisibility(View.VISIBLE);

                userID = friendModel.getUserId();
                findFriendDatabase.child(currentUser.getUid()).child(userID).child(NodeNames.REQUEST_TYPE)
                        .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            findFriendDatabase.child(userID).child(currentUser.getUid()).child(NodeNames.REQUEST_TYPE)
                                    .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(context, R.string.request_cancelled_successful, Toast.LENGTH_SHORT).show();
                                        holder.pbRequest.setVisibility(View.GONE);
                                        holder.btSendRequest.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        Toast.makeText(context,
                                                context.getString (R.string.request_cancel_failed , task.getException()),
                                                Toast.LENGTH_SHORT).show();
                                        holder.pbRequest.setVisibility(View.GONE);
                                        holder.btCancelRequest.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return findFriendModelList.size();
    }

    public class FindFriendViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivProfile;
        private TextView tvFullName;
        private Button btSendRequest, btCancelRequest;
        private ProgressBar pbRequest;

        public FindFriendViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            btSendRequest = itemView.findViewById(R.id.btSendRequest);
            btCancelRequest = itemView.findViewById(R.id.btCancelRequest);
            pbRequest = itemView.findViewById(R.id.pbRequest);
        }
    }
}
