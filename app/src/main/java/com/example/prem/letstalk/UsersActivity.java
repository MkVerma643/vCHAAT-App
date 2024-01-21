package com.example.prem.letstalk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    private RecyclerView mUserList;

    private DatabaseReference mUsersDatabase;

    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;

    MaterialSearchView materialSearchBar;

    private EditText SearchInputText;
    private ImageButton SearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar=(Toolbar)findViewById(R.id.users_Appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        mToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SearchInputText=(EditText)findViewById(R.id.search_input_text);
        SearchButton=(ImageButton)findViewById(R.id.search_people_button);

        materialSearchBar=(MaterialSearchView)findViewById(R.id.search_view);


        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());


        mUserList=(RecyclerView)findViewById(R.id.users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));



        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchUserName=SearchInputText.getText().toString();
                searchUserName.toLowerCase();
                if(TextUtils.isEmpty(searchUserName))
                {
                    Toast.makeText(UsersActivity.this, "Please write user name to Search..", Toast.LENGTH_SHORT).show();
                }
                SearchForPeopleAndFriends(searchUserName);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users,UsersViewsHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, UsersViewsHolder>(
                Users.class,
                R.layout.users_single_layout,
                UsersViewsHolder.class,
                mUsersDatabase
        ) {
            @Override
            protected void populateViewHolder(UsersViewsHolder viewHolder, Users model, int position) {
                viewHolder.setDisplayName(model.getName());
                viewHolder.setUsersStatus(model.getStatus());
                viewHolder.setUserImage(model.getThumb_image(),getApplicationContext());


                final String user_id=getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profileintent=new Intent(UsersActivity.this,ProfileActivity.class);
                        profileintent.putExtra("user_id",user_id);
                        startActivity(profileintent);
                    }
                });
            }
        };
        mUserList.setAdapter(firebaseRecyclerAdapter);
    }

    private void SearchForPeopleAndFriends(String searchUserName) {
        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();

        Query searchPeopleAndFriends=mUsersDatabase.orderByChild("lowername")
                .startAt(searchUserName).endAt(searchUserName+"\uf8ff");

        FirebaseRecyclerAdapter<Users,UsersViewsHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, UsersViewsHolder>(
                Users.class,
                R.layout.users_single_layout,
                UsersViewsHolder.class,
                searchPeopleAndFriends
        ) {
            @Override
            protected void populateViewHolder(UsersViewsHolder viewHolder, Users model, int position) {
                viewHolder.setDisplayName(model.getName());
                viewHolder.setUsersStatus(model.getStatus());
                viewHolder.setUserImage(model.getThumb_image(),getApplicationContext());


                final String user_id=getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profileintent=new Intent(UsersActivity.this,ProfileActivity.class);
                        profileintent.putExtra("user_id",user_id);
                        startActivity(profileintent);
                    }
                });
            }
        };
        mUserList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class UsersViewsHolder extends RecyclerView.ViewHolder{
        View mView;
        public UsersViewsHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setDisplayName(String name){
            TextView mUserNameView= (TextView) mView.findViewById(R.id.user_single_name);
            mUserNameView.setText(name);
        }
        public void setUsersStatus(String status){
            TextView mUserNameView= (TextView) mView.findViewById(R.id.user_single_status);
            mUserNameView.setText(status);
        }
        public void setUserImage(final String thumb_image, final Context ctx){
            final CircleImageView userImageView=(CircleImageView)mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.mypic).into(userImageView);
            final ImagePopup imagePopup=new ImagePopup(ctx);
            imagePopup.initiatePopup(userImageView.getDrawable());
            userImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                            //imagePopup.initiatePopupWithPicasso(thumb_image);
                            //imagePopup.setWindowHeight(900);
                            //imagePopup.setWindowWidth(700);
                            //imagePopup.setFullScreen(true);
                            //imagePopup.setHideCloseIcon(true);
                            //imagePopup.setBackgroundColor(Color.BLACK);
                            //imagePopup.setImageOnClickClose(true);
                            //imagePopup.viewPopup();

                        }
                    });
        }
    }

}
