package com.example.bright.RescueHelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsActivity extends AppCompatActivity {
    public int x=0;
    private RecyclerView myFriendList;
    private DatabaseReference FriendsRef, UsersRef, LocRef;
    private FirebaseAuth mAuth;
    private String online_user_id;
    private String usersIDs;
    public String userLocation_latitude;
    public String userLocation_longitude;
    private Button show_friends_on_map;
    ArrayList<String> friends_id = new ArrayList<String>();
    Map<String, String> friends_id_loc = new HashMap<>();
    Map<String, FriendCoord> friendCoordList = new HashMap<>();
    FriendCoord friendcoord_object = new FriendCoord();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        show_friends_on_map = (Button) findViewById(R.id.btn_show_friends_on_map);
        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        LocRef = FirebaseDatabase.getInstance().getReference().child("Friends_localization").child(online_user_id);


        myFriendList = (RecyclerView) findViewById(R.id.friend_list);
        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();

    }

    private void DisplayAllFriends() {
        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                        Friends.class,
                        R.layout.all_users_display_layout,
                        FriendsViewHolder.class,
                        FriendsRef
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {

                viewHolder.setDate(model.getDate());
                // number of users
                usersIDs = getRef(position).getKey();
                // checking IDs under my id in "Friends" in firebase
                friends_id.add(usersIDs);


                UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       if(dataSnapshot.exists()){
                           final String userName = dataSnapshot.child("name").getValue().toString();
                           userLocation_latitude = dataSnapshot.child("latitude").getValue().toString();
                           userLocation_longitude = dataSnapshot.child("longitude").getValue().toString();
                           friendcoord_object = new FriendCoord(userLocation_latitude,userLocation_longitude);
                               //Log.d("ajajaj z geterami: " + friends_id.get(x), "");
                               friendCoordList.put(friends_id.get(x), friendcoord_object);
                               //id friendsa(x) + obiekt.latitude + obiekt.longitude
                               Log.d("ajajaj z geterami: " + friends_id.get(x) + "|"+ friendcoord_object.getLatitude(), " | " + friendcoord_object.getLongitude());
                              // Log.d("ajajaj element: ", " : " +  friendCoordList.get(friends_id.get(x)).latitude + " + " + friendCoordList.get(friends_id.get(x)).longitude);
                           x++;
                           //Log.d("ajajaj z geterami: " + friends_id.get(x) + "|"+ friendcoord_object.getLatitude(), " | " + friendcoord_object.getLongitude());

                           // HashMapa Map<String, FriendCoord> friendCoordList = new HashMap<>();
                           viewHolder.setFullname(userName);
                           viewHolder.setLatitude(userLocation_latitude);
                           viewHolder.setLongitude(userLocation_longitude);


                       }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                show_friends_on_map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Intent act2=new Intent(getApplicationContext(),MapsActivity.class);//"this" is  activity reference
                        act2.putExtra("latitude",userLocation_latitude);
                        act2.putExtra("longitude",userLocation_longitude);
                        startActivity(act2);*/
                        Log.d("kumple to: ", "" + friends_id);
                    }
                });
            }
        };
        myFriendList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFullname(String fullname) {
            TextView myName = (TextView) mView.findViewById(R.id.all_users_profile_full_name);
            myName.setText(fullname);
        }

        public void setDate(String date) {
            TextView friendsDate = (TextView) mView.findViewById(R.id.all_users_profile_status);
            friendsDate.setText("Friends Since: " + date);
        }

        public void setLatitude(String latitude) {
            TextView lat_textv = (TextView) mView.findViewById(R.id.all_users_profile_lat);
            lat_textv.setText("lat: " + latitude);

        }

        public void setLongitude(String longitude) {
            TextView lng_textv = (TextView) mView.findViewById(R.id.all_users_profile_lng);
            lng_textv.setText("lng: " + longitude);

        }


    }
}
