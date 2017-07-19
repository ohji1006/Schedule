package org.androidtown.schedule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Group_chat_room_activity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private String userName ;
    private ArrayAdapter<String> adapter;
    private String uid,gid;
    private ListView listView;
    private EditText chat_editText;
    private Button send_button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_room_activity);

        listView = (ListView) findViewById(R.id.group_chating_listView);
        send_button = (Button) findViewById(R.id.chat_send_button);
        chat_editText = (EditText)findViewById(R.id.group_chat_enter_editText) ;

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        gid = intent.getStringExtra("gid");

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1);
        listView.setAdapter( adapter);

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(userName != null)
                {
                    ChatData chatData = new ChatData(userName, chat_editText.getText().toString());
                    databaseReference.child("Groups").child(gid).child("chat_Room").push().setValue(chatData);
                    chat_editText.setText("");
                }
            }
        });

        databaseReference.child("Groups").child(gid).child("chat_Room").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                adapter.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    ChatData chatData = snapshot.getValue(ChatData.class);
                    if(chatData !=null) {
                        adapter.add(chatData.getUserName() + " : " + chatData.getMessage());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("Users").child(uid).child("name").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue()+"";
                Toast.makeText(Group_chat_room_activity.this, "userName is ::::" + userName,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
