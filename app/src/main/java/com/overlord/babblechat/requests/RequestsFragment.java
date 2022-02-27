package com.overlord.babblechat.requests;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.overlord.babblechat.R;
import com.overlord.babblechat.common.Constants;
import com.overlord.babblechat.common.NodeNames;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends Fragment {
    private RecyclerView rvRequests;
    private RequestAdapter adapter;
    private List<RequestModel> requestModelList;
    private TextView tvEmptyRequestList;

    private DatabaseReference databaseReferenceRequests, databaseReferenceUsers;
    private FirebaseUser currentUser;
    private View progressBar;

    public RequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvRequests = view.findViewById(R.id.rvRequests);
        tvEmptyRequestList = view.findViewById(R.id.tvEmptyFriendList);
        progressBar = view.findViewById(R.id.progressBar);

        rvRequests.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestModelList = new ArrayList<>();
        adapter = new RequestAdapter(getActivity(),requestModelList);
        rvRequests.setAdapter(adapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

        databaseReferenceRequests = FirebaseDatabase.getInstance().getReference().child(NodeNames.FRIEND_REQUESTS).child(currentUser.getUid());

        tvEmptyRequestList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        databaseReferenceRequests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                requestModelList.clear();

                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.exists()){
                        String requestType = ds.child(NodeNames.REQUEST_TYPE).getValue().toString();
                        if(requestType == Constants.REQUEST_STATUS_RECEIVED){
                            String userID = ds.getKey();
                            databaseReferenceUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    String userName = snapshot.child(NodeNames.NAME).getValue().toString();

                                    String photoName = "";
                                    if(snapshot.child(NodeNames.PHOTO).getValue()!=null){
                                        photoName =snapshot.child(NodeNames.PHOTO).getValue().toString();
                                    }
                                    RequestModel requestModel = new RequestModel(userID, userName, photoName);
                                    requestModelList.add(requestModel);
                                    adapter.notifyDataSetChanged();
                                    tvEmptyRequestList.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                    Toast.makeText(getActivity(),getActivity().getString(R.string.failed_to_fetch_friend_request,  error.getMessage()) , Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}