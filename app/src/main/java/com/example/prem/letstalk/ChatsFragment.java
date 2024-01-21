package com.example.prem.letstalk;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prem.letstalk.Common.Common;
import com.example.prem.letstalk.Interface.ItemClickListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {
    private RecyclerView mFriendsList;

    private RecyclerView mConvList;

    private DatabaseReference mMessageDatabase;

    private DatabaseReference mConvDatabase;

    private DatabaseReference mFriendsDatabase,mUserDatabase;
    private DatabaseReference mDeleteChats;
    private DatabaseReference mDeleteMes;
    private DatabaseReference mUsersDatabase;

    private FirebaseUser mCurrent_user;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    Query conversationQuery;

    FirebaseRecyclerAdapter<Conv, ConvViewHolder> firebaseConvAdapter;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        mConvList = (RecyclerView) mMainView.findViewById(R.id.conv_list);
        mAuth = FirebaseAuth.getInstance();


        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mCurrent_user= FirebaseAuth.getInstance().getCurrentUser();


        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);


        mDeleteChats=FirebaseDatabase.getInstance().getReference().child("Chat");


        mDeleteMes=FirebaseDatabase.getInstance().getReference().child("messages");

        mConvDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);


        // Inflate the layout for this fragment
        return mMainView;
    }



    @Override
    public void onStart() {
        super.onStart();

        Query conversationQuery = mConvDatabase.orderByChild("timestamp");

        firebaseConvAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(
                Conv.class,
                R.layout.users_single_layout,
                ConvViewHolder.class,
                conversationQuery
        ) {
            @Override
            public  void populateViewHolder(final ConvViewHolder convViewHolder, final Conv conv, int i) {



                final String list_user_id = getRef(i).getKey();

                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String data = dataSnapshot.child("message").getValue().toString();
                        convViewHolder.setMessage(data, conv.isSeen());

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            convViewHolder.setUserOnline(userOnline);

                        }

                        convViewHolder.setName(userName);
                        convViewHolder.setUserImage(userThumb, getContext());

                        convViewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                if(!isLongClick)
                                {
                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("user_id", list_user_id);
                                    chatIntent.putExtra("user_name", userName);
                                    startActivity(chatIntent);
                                }
                                else
                                {

                                    CharSequence option[]=new CharSequence[]{"Delete Chats!","Delete Message!","Save Chat"};
                                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                    builder.setTitle("Select Options");
                                    builder.setItems(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {

                                            if(i==0){
                                                mDeleteChats.child(mCurrent_user.getUid()).child(list_user_id).removeValue();
                                                //mDeleteChats.child(list_user_id).child(mCurrent_user.getUid()).removeValue();
                                            }
                                            if(i==1) {
                                                mDeleteMes.child(mCurrent_user.getUid()).child(list_user_id).removeValue();
                                            }
                                            if(i==2){
                                                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                                DatabaseReference requestRef = rootRef.child("messages").child(mCurrent_user_id).child(list_user_id);
                                                ValueEventListener valueEventListener = new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                                            String msg = ds.child("message").getValue(String.class);
                                                            String frm=ds.child("from").getValue(String.class);

                                                            String s=msg;
                                                            try {
                                                                File myFile = new File("/sdcard/"+userName+".txt");
                                                                if(!myFile.exists())
                                                                    myFile.createNewFile();
                                                                if(mCurrent_user.getUid().equals(frm)) {
                                                                    FileWriter fOut = new FileWriter(myFile, true);
                                                                    BufferedWriter myOutWriter =
                                                                            new BufferedWriter(fOut);
                                                                    myOutWriter.write("You : " +s+"\n");
                                                                    myOutWriter.close();
                                                                    fOut.close();
                                                                }
                                                                else if(list_user_id.equals(frm)){
                                                                    FileWriter fOut = new FileWriter(myFile, true);
                                                                    BufferedWriter myOutWriter =
                                                                            new BufferedWriter(fOut);
                                                                    myOutWriter.write(userName+" : " +s+ "\n\n");
                                                                    myOutWriter.close();
                                                                    fOut.close();
                                                                }

                                                            } catch (Exception e) {
                                                                Toast.makeText(getContext(), e.getMessage(),
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {}
                                                };
                                                requestRef.addListenerForSingleValueEvent(valueEventListener);
                                                Toast.makeText(getContext(), "Your Chats Saved Successfully In Your SdCard as MyChat.txt", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                });
                                builder.show();

                                }
                            }
                        });

                        convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                               /* CharSequence option[]=new CharSequence[]{"Chat","Delete Chats"};
                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {

                                        if(i==0){*/
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("user_id", list_user_id);
                                            chatIntent.putExtra("user_name", userName);
                                            startActivity(chatIntent);
                                     /*   }
                                        if(i==1){
                                            mDeleteChats.child(mCurrent_user.getUid()).child(list_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mDeleteChats.child(list_user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    });
                                                }
                                            });
                                            mDeleteMes.child(mCurrent_user.getUid()).child(list_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mDeleteChats.child(list_user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    });
                                                }
                                            });
                                        }

                                    }
                                });
                                builder.show();*/




                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mConvList.setAdapter(firebaseConvAdapter);



    }

    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
        {

            Toast.makeText(getContext(), "Chat Deleted Successfully..!!", Toast.LENGTH_SHORT).show();
        }

        return super.onContextItemSelected(item);

    }


    public static class ConvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        View mView;
        private ItemClickListener itemClickListener;

        public ConvViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);

        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }





        public void setMessage(String message, boolean isSeen){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(message);

            if(!isSeen){
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
                userStatusView.setTextColor(Color.RED);

            } else {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);

            }

        }

        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.mypic).into(userImageView);


        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }


        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),true);
            return true;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }
    }



}


        /*final FirebaseRecyclerAdapter<Chats, ChatsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(

                Chats.class,
                R.layout.users_single_layout,
                ChatsViewHolder.class,
                mFriendsDatabase


        ) {
            @Override
            protected void populateViewHolder(final ChatsViewHolder chatsViewHolder, final Chats chats, int i) {

                final String list_user_id=getRef(i).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userStatus=dataSnapshot.child("status").getValue().toString();
                        final String userName=dataSnapshot.child("name").getValue().toString();
                        String userThumb=dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online")) {
                            String userOnline =dataSnapshot.child("online").getValue().toString();
                            chatsViewHolder.setUserOnline(userOnline);
                        }

                        chatsViewHolder.setName(userName);
                        chatsViewHolder.setUsersStatus(userStatus);
                        chatsViewHolder.setUserImage(userThumb,getContext());

                        chatsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                            Intent chatActivity=new Intent(getContext(),ChatActivity.class);
                                            chatActivity.putExtra("user_id",list_user_id);
                                            chatActivity.putExtra("user_name",userName);
                                            startActivity(chatActivity);
                                        }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        };

        mFriendsList.setAdapter(friendsRecyclerViewAdapter);


    }


    public static class ChatsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ChatsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setName(String name){
            TextView mUserNameView= (TextView) mView.findViewById(R.id.user_single_name);
            mUserNameView.setText(name);
        }
        public void setUsersStatus(String status){
            TextView mUserNameView= (TextView) mView.findViewById(R.id.user_single_status);
            mUserNameView.setText(status);
        }
        public void setUserImage(String thumb_image,Context ctx){
            CircleImageView userImageView=(CircleImageView)mView.findViewById(R.id.user_single_image);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.mypic).into(userImageView);
        }
        public void setUserOnline(String online_status){
            ImageView userOnlineView=(ImageView)mView.findViewById(R.id.user_single_online_icon);
            if(online_status.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            }else{
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }

}
*/

