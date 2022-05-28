package com.overlord.babblechat.selectFriend;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class SelectFriendAdapter extends RecyclerView.Adapter<SelectFriendAdapter.SelectFriendVIewHolder> {

    private Context context;
    private List<SelectFriendModel> friendModelList;

    public SelectFriendAdapter(Context context, List<SelectFriendModel> friendModelList) {
        this.context = context;
        this.friendModelList = friendModelList;
    }

    @NonNull
    @NotNull
    @Override
    public SelectFriendAdapter.SelectFriendVIewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.select_freind_layout, parent, false);
        return new SelectFriendVIewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SelectFriendAdapter.SelectFriendVIewHolder holder, int position) {
        SelectFriendModel friendModel = friendModelList.get(position);
        holder.tvFullName.setText(friendModel.getUserName());

        StorageReference photoRef = FirebaseStorage.getInstance().getReference().child(Constants.IMAGES_FOLDER + "/" + friendModel.getPhotoName());
        photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .into(holder.ivProfile);
            }
        });

        holder.llSelectFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(context instanceof SelectFriendActivity){
                    ((SelectFriendActivity)context).returnSelectedFriend(friendModel.getUserId(), friendModel.getUserName(),
                            friendModel.getUserId()+".jpg");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return friendModelList.size();
    }

    public class SelectFriendVIewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llSelectFriend;
        private ImageView ivProfile;
        private TextView tvFullName;
        public SelectFriendVIewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            llSelectFriend = itemView.findViewById(R.id.llSelectFriend);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvFullName = itemView.findViewById(R.id.tvFullName);
        }
    }
}
