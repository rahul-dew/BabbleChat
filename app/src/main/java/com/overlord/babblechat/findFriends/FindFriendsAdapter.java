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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.overlord.babblechat.R;
import com.overlord.babblechat.common.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.FindFriendViewHolder> {

    private Context context;
    private List<FindFriendModel> findFriendModelList;

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
