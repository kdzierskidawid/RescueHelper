package com.example.bright.RescueHelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.plus.model.people.Person;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class FindAllUsers extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button searchButton;
    private EditText searchEditText;
    private RecyclerView searchResultList;

    private DatabaseReference allUsersDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_all_users);


        allUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");

        searchResultList = (RecyclerView) findViewById(R.id.search_result_list);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));

        searchButton = (Button) findViewById(R.id.search_people_friends_button);
        searchEditText = (EditText) findViewById(R.id.search_box_input);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchBoxInput = searchEditText.getText().toString();


                SearchPeopleAndFriends(searchBoxInput);
            }
        });

    }

    private void SearchPeopleAndFriends(String searchBoxInput) {
        Toast.makeText(this, "Searching...", Toast.LENGTH_LONG);

        Query searchPeopleandFriendsQuery = allUsersDatabaseRef.orderByChild("email")
                .startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");


        FirebaseRecyclerAdapter<UserInformation, FindFriendsViewHolder > firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<UserInformation, FindFriendsViewHolder>(
                UserInformation.class,
                R.layout.all_users_display_layout,
                FindFriendsViewHolder.class,
                searchPeopleandFriendsQuery
        ) {
            @Override
            protected void populateViewHolder(FindFriendsViewHolder viewHolder, UserInformation model, final int position) {
                viewHolder.setFullname(model.getEmail());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String visit_user_id = getRef(position).getKey();

                        Intent profileIntent = new Intent(FindAllUsers.this, PersonProfileActivity.class);
                        profileIntent.putExtra("visit_user_id", visit_user_id);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        searchResultList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FindFriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFullname(String fullname)
        {
            TextView myName = (TextView) mView.findViewById(R.id.all_users_profile_full_name);
            myName.setText(fullname);
        }


    }
}
