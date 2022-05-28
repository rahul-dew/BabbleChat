package com.overlord.babblechat.selectFriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.overlord.babblechat.R;
import com.overlord.babblechat.common.Extras;
import com.overlord.babblechat.common.NodeNames;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SelectFriendActivity extends AppCompatActivity {

    private RecyclerView rvSelectFriend;
    private SelectFriendAdapter selectFriendAdapter;
    private List<SelectFriendModel> selectFriendModels;
    private View progressBar;

    private DatabaseReference databaseReferenceUsers, databaseReferenceChats;

    private FirebaseUser currentUser;
    private ValueEventListener valueEventListener;

    private String selectedMessage, selectedMessageId, selectedMessageType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);

        if(getIntent().hasExtra(Extras.MESSAGE)){
            selectedMessage = getIntent().getStringExtra(Extras.MESSAGE);
            selectedMessageId = getIntent().getStringExtra(Extras.MESSAGE_ID);
            selectedMessageType = getIntent().getStringExtra(Extras.MESSAGE_TYPE);
        }

        rvSelectFriend = findViewById(R.id.rvSelectFriend);
        progressBar = findViewById(R.id.progressBar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvSelectFriend.setLayoutManager(linearLayoutManager);

        selectFriendModels = new ArrayList<>();
        selectFriendAdapter = new SelectFriendAdapter(this, selectFriendModels);
        rvSelectFriend.setAdapter(selectFriendAdapter);

        progressBar.setVisibility(View.VISIBLE);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceChats = FirebaseDatabase.getInstance().getReference().child(NodeNames.CHATS).child(currentUser.getUid());
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    String userId = ds.getKey();
                    databaseReferenceUsers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            String userName = snapshot.child(NodeNames.NAME).getValue()!=null?snapshot.child(NodeNames.NAME).getValue().toString():"";

                            SelectFriendModel friendModel = new SelectFriendModel(userId, userName, userId+".jpg");
                            selectFriendModels.add(friendModel);
                            selectFriendAdapter.notifyDataSetChanged();

                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            Toast.makeText(SelectFriendActivity.this, getString(R.string.failed_to_fetch_friend_list, error.getMessage()), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(SelectFriendActivity.this, getString(R.string.failed_to_fetch_friend_list, error.getMessage()), Toast.LENGTH_SHORT).show();
            }
        };

        databaseReferenceChats.addValueEventListener(valueEventListener);
    }

    public void returnSelectedFriend(String userId, String userName, String photoName){
        databaseReferenceChats.removeEventListener(valueEventListener);
        Intent intent = new Intent();

        intent.putExtra(Extras.USER_KEY, userId);
        intent.putExtra(Extras.USER_NAME, userName);
        intent.putExtra(Extras.PHOTO_NAME, photoName);
        intent.putExtra(Extras.MESSAGE, selectedMessage);
        intent.putExtra(Extras.MESSAGE_ID, selectedMessageId);
        intent.putExtra(Extras.MESSAGE_TYPE, selectedMessageType);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}