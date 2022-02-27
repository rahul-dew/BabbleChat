package com.overlord.babblechat.findFriends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.overlord.babblechat.R;
import com.overlord.babblechat.common.Constants;
import com.overlord.babblechat.common.NodeNames;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class FindFriendFragment extends Fragment {

    private RecyclerView rvFindFriends;
    private FindFriendsAdapter findFriendsAdapter;
    private List<FindFriendModel> findFriendModelList;
    private TextView tvEmptyFriendsList;

    private DatabaseReference databaseReference, databaseReferenceFriendRequests;
    private FirebaseUser currentUser;

    private View progressBar;

    public FindFriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFindFriends = view.findViewById(R.id.rvFindFriend);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyFriendsList = view.findViewById(R.id.tvEmptyFriendList);

        rvFindFriends.setLayoutManager(new LinearLayoutManager(getActivity()));
        findFriendModelList = new ArrayList<>();
        findFriendsAdapter = new FindFriendsAdapter(getActivity(), findFriendModelList);
        rvFindFriends.setAdapter(findFriendsAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReferenceFriendRequests = FirebaseDatabase.getInstance().getReference()
                .child(NodeNames.FRIEND_REQUESTS).child(currentUser.getUid());

        tvEmptyFriendsList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        Query query = databaseReference.orderByChild(NodeNames.NAME);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                findFriendModelList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    String userID = ds.getKey();
                    if(userID.equals(currentUser))
                        return;
                    if(ds.child(NodeNames.NAME).getValue() != null){
                        String fullName = ds.child(NodeNames.NAME).getValue().toString();
                        String photoName = ds.child(NodeNames.PHOTO).getValue().toString();

                        databaseReferenceFriendRequests.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String requestType = snapshot.child(NodeNames.REQUEST_TYPE).getValue().toString();
                                    if(requestType.equals(Constants.REQUEST_STATUS_SENT)){
                                        findFriendModelList.add(new FindFriendModel(fullName, photoName, userID, true));
                                        findFriendsAdapter.notifyDataSetChanged();
                                    }
                                }
                                else{
                                    findFriendModelList.add(new FindFriendModel(fullName, photoName, userID, false));
                                    findFriendsAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                        tvEmptyFriendsList.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(),
                        getString (R.string.failed_to_fetch_friends, error.getMessage()),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }
}