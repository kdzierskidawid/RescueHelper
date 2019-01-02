package com.example.bright.RescueHelper;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instabug.library.view.CircularImageView;

import org.w3c.dom.Text;

public class PersonProfileActivity extends AppCompatActivity {

    private CircularImageView userProfileImage;
    private TextView user_email, user_name;
    private Button btn_send_invite, btn_decline_invite;

    private DatabaseReference FriendRequestRef, UsersRef;
    private FirebaseAuth mAuth;
    private String senderUserId, receiverUserId, CURRENT_STATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();

        // nasze aktualne id
        senderUserId = mAuth.getCurrentUser().getUid();

        //czyjes id, po kliknieciu na profil to bedzie id tej osoby, nie nasze
        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");

        UsersRef = FirebaseDatabase.getInstance().getReference().child("users");

        InitFields();



        UsersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String user_email_string = dataSnapshot.child("email").getValue().toString();
                    String user_name_string = dataSnapshot.child("name").getValue().toString();

                    user_name.setText("Name: "+user_name_string);
                    user_email.setText("Email: "+ user_email_string);

                    MaintanceofButtons();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btn_decline_invite.setVisibility(View.INVISIBLE);
        btn_decline_invite.setEnabled(false);


        if(!senderUserId.equals(receiverUserId)){
            btn_send_invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btn_send_invite.setEnabled(false);

                    if(CURRENT_STATE.equals("not_friends")) {
                        SendFriendRequestToaPerson();
                    }

                    if(CURRENT_STATE.equals("request_sent")){
                        CancelFriendrequest();
                    }
                }
            });

        }
        else{
            btn_decline_invite.setVisibility(View.INVISIBLE);
            btn_send_invite.setVisibility(View.INVISIBLE);
        }

    }

    private void CancelFriendrequest() {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                btn_send_invite.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                btn_send_invite.setText("send group request");

                                                btn_decline_invite.setVisibility(View.INVISIBLE);
                                                btn_decline_invite.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void MaintanceofButtons() {
        FriendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // if child exists
                        if(dataSnapshot.hasChild(receiverUserId)){
                            String request_type = dataSnapshot.child(receiverUserId)
                                    .child("request_type").getValue().toString();

                            if(request_type.equals("sent")){
                                CURRENT_STATE = "request_sent";
                                btn_send_invite.setText("Cancel friend request");

                                btn_decline_invite.setVisibility(View.INVISIBLE);
                                btn_decline_invite.setEnabled(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void SendFriendRequestToaPerson() {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                btn_send_invite.setEnabled(true);
                                                CURRENT_STATE = "request_sent";
                                                btn_send_invite.setText("Cancel friend request");

                                                btn_decline_invite.setVisibility(View.INVISIBLE);
                                                btn_decline_invite.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    public void InitFields(){
        userProfileImage = (CircularImageView) findViewById(R.id.my_profile_pic);
        user_email = (TextView) findViewById(R.id.person_email);
        user_name = (TextView) findViewById(R.id.person_name);
        btn_send_invite = (Button) findViewById(R.id.btnSendInvite);
        btn_decline_invite = (Button) findViewById(R.id.btnDeclineInvite);
        CURRENT_STATE = "not_friends";
    }
}
