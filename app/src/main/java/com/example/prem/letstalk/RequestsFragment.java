package com.example.prem.letstalk;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {
    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;





    public RequestsFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);


        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<Chats, RequestViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Chats, RequestViewHolder>(

                Chats.class,
                R.layout.single_friend_request_layout,
                RequestsFragment.RequestViewHolder.class,
                mFriendsDatabase


        ) {
            @Override
            protected void populateViewHolder(final RequestViewHolder requestViewHolder, Chats request, int i) {
                final String list_user_id=getRef(i).getKey();

                DatabaseReference get_type_ref=getRef(i).child("request_type").getRef();

                get_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Start Here
                        if(dataSnapshot.exists())
                        {
                            String request_type=dataSnapshot.getValue().toString();

                            if(request_type.equals("received"))
                            {
                                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String userStatus=dataSnapshot.child("status").getValue().toString();
                                        final String userName=dataSnapshot.child("name").getValue().toString();
                                        String userThumb=dataSnapshot.child("thumb_image").getValue().toString();

                                        requestViewHolder.setName(userName);
                                        requestViewHolder.setUsersStatus(userStatus);
                                        requestViewHolder.setUserImage(userThumb,getContext());

                                        requestViewHolder.ReqAccept();
                                        requestViewHolder.ReqReject();
                                        requestViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Intent profileintent=new Intent(getContext(),ProfileActivity.class);
                                                profileintent.putExtra("user_id",list_user_id);
                                                startActivity(profileintent);

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                            else if(request_type.equals("sent"))
                            {
                                Button req_sent_btn= (Button) requestViewHolder.mView.findViewById(R.id.req_accept_btn);
                                req_sent_btn.setText("Request Sent");

                                requestViewHolder.mView.findViewById(R.id.req_delete_btn).setVisibility(View.INVISIBLE);

                                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String userStatus=dataSnapshot.child("status").getValue().toString();
                                        final String userName=dataSnapshot.child("name").getValue().toString();
                                        String userThumb=dataSnapshot.child("thumb_image").getValue().toString();

                                        requestViewHolder.setName(userName);
                                        requestViewHolder.setUsersStatus(userStatus);
                                        requestViewHolder.setUserImage(userThumb,getContext());

                                        requestViewHolder.ReqAccept();
                                        requestViewHolder.ReqReject();
                                        requestViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Intent profileintent=new Intent(getContext(),ProfileActivity.class);
                                                profileintent.putExtra("user_id",list_user_id);
                                                startActivity(profileintent);

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //
            }

        };

        mFriendsList.setAdapter(friendsRecyclerViewAdapter);


    }


    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setName(String name){
            TextView mUserNameView= (TextView) mView.findViewById(R.id.user_single_req_name);
            mUserNameView.setText(name);
        }
        public void setUsersStatus(String status){
            TextView mUserNameView= (TextView) mView.findViewById(R.id.user_single_req_status);
            mUserNameView.setText(status);
        }
        public void setUserImage(String thumb_image,Context ctx){
            CircleImageView userImageView=(CircleImageView)mView.findViewById(R.id.user_single_req_image);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.mypic).into(userImageView);
        }

        public void ReqAccept() {

        }

        public void ReqReject() {
        }
    }

}

