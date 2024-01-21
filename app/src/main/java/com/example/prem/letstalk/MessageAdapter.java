package com.example.prem.letstalk;



import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messaging_layout, parent, false);

        return new MessageViewHolder(v);

    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            mAuth=FirebaseAuth.getInstance();
            //displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage=(ImageView)view.findViewById(R.id.message_image_layout);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

         String current_user_id=mAuth.getCurrentUser().getUid();
        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type=c.getType();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

//                viewHolder.displayName.setText(name);

                Picasso.with(viewHolder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.mypic).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(from_user.equals(current_user_id)) {

            if (message_type.equals("text")) {
                RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                viewHolder.messageText.setLayoutParams(params);
                viewHolder.profileImage.setVisibility(View.GONE);
                viewHolder.messageText.setBackgroundColor(Color.WHITE);
                viewHolder.messageText.setTextColor(Color.BLACK);
                viewHolder.messageText.setText(c.getMessage());
                viewHolder.messageImage.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.messageText.setVisibility(View.INVISIBLE);
                Picasso.with(viewHolder.profileImage.getContext()).load(c.getMessage()).placeholder(R.drawable.mypic).into(viewHolder.messageImage);
            }

        }else{
            if (message_type.equals("text")) {
                RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                viewHolder.messageText.setLayoutParams(params);
                viewHolder.profileImage.setVisibility(View.GONE);
                viewHolder.messageText.setBackgroundColor(Color.BLUE);
                viewHolder.messageText.setTextColor(Color.WHITE);
                viewHolder.messageText.setText(c.getMessage());
                viewHolder.messageImage.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.messageText.setVisibility(View.INVISIBLE);
                Picasso.with(viewHolder.profileImage.getContext()).load(c.getMessage()).placeholder(R.drawable.mypic).into(viewHolder.messageImage);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}

        //-------------------------Custom Chat Layout---------------------------------

       /* if(from_user.equals(current_user_id)){

            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            viewHolder.messageText.setLayoutParams(params);
            viewHolder.profileImage.setVisibility(View.GONE);

            viewHolder.messageText.setBackgroundColor(Color.WHITE);
            viewHolder.messageText.setTextColor(Color.BLACK);

        }else{
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            viewHolder.messageText.setLayoutParams(params);
            viewHolder.profileImage.setVisibility(View.GONE);

            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
            viewHolder.messageText.setTextColor(Color.WHITE);
        }

        viewHolder.messageText.setText(c.getMessage());



    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

} */

