package org.androidtown.schedule;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.R.id.text1;

public class Test_Activity extends AppCompatActivity
{


    private FirebaseAuth firebaseAuth;
    //private DatabaseReference databaseReference;

    private String id;

    private String userNames ;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
   // private FirebaseDatabase firebaseDatabasetwo = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    //private DatabaseReference databaseReferencetwo = firebaseDatabase.getReference();
    private ArrayAdapter<String> adapter;
   // private ArrayList<VoteData> listItems = new ArrayList<VoteData>();
    private EditText editText;
    private TextView chat_conversation;

    private Button sendButton, voteButton, votelistButton;
    private int year, month, day, hour, minute;
    private DatePickerDialog datePickerDialog;
    private AlertDialog.Builder buider;
    private String shedule_title;
    private String shedule_body;
    private View dialogView;
    private String get_groupId_text_toString;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

      //  ListView listView = (ListView) findViewById(R.id.listv);


        Intent intent = getIntent();
        get_groupId_text_toString = intent.getStringExtra("get_groupId_text_toString");
        id = intent.getStringExtra("id");
        Toast.makeText(this, "get : " + id, Toast.LENGTH_SHORT).show();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        //userName=databaseReference.child("Users").child(id).child("name").get;
                //user.getEmail();

        ListView listView = (ListView) findViewById(R.id.test_listView);
        editText = (EditText) findViewById(R.id.test_editText);
        sendButton = (Button) findViewById(R.id.test_button);
        voteButton = (Button) findViewById(R.id.vote_button);
        votelistButton = (Button) findViewById(R.id.votelist_button);
        chat_conversation = (TextView) findViewById(R.id.textView);

      //  userName =
        //userName = "user" + new Random().nextInt(10000);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                text1);

        listView.setAdapter( adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
       // listView.setOnItemClickListener(new ListViewItemClickListener());

        databaseReference.child("Users").child(id).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String userNicnkname = ""+dataSnapshot.getValue();
                userNames = userNicnkname;


                Toast.makeText(Test_Activity.this,"this is user name: " + userNames,Toast.LENGTH_LONG);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        databaseReference.child("Groups").child(get_groupId_text_toString).child("chat_Room").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    ChatData chatData = snapshot.getValue(ChatData.class);
                    adapter.add(chatData.getUserName()+":"+chatData.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
/*
        databaseReference.child("Users").child(id).addValueEventListener(new EventListener() {  // message는 child의 이벤트를 수신합니다.

        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            //ChatData chatData = dataSnapshot.getValue(ChatData.class);  // chatData를 가져오고

            for(DataSnapshot snapshot : dataSnapshot.getChildren())
            {
                if(dataSnapshot.getKey()+"" == "name")
                {
                    String userNicnkname = ""+dataSnapshot.getValue();
                    userNames = userNicnkname;
                }
            }
            Toast.makeText(Test_Activity.this,"this is user name: " + userNames,Toast.LENGTH_LONG);

            //adapter.add(chatData.getUserName()+":"+chatData.getMessage());
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

*/


        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ChatData chatData = new ChatData(userNames , editText.getText().toString());  // 유저 이름과 메세지로 chatData 만들기

               // User userData = new User();


               // Schedule schedule = new Schedule("fad");
              //  Groups groups = new Groups(true);
              //  User user = new User("idd",groups,schedule);
               // ArrayList<User> users_Arrya = new ArrayList<User>();
             //   users_Arrya.add(user);

               // Users users = new Users(users_Arrya);

                databaseReference.child("Groups").child(get_groupId_text_toString).child("chat_Room").push().setValue(chatData);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기
              //  databaseReference.child("message").child("TTTTTT").setValue(users);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기

                editText.setText("");
            }
        });

        voteButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view)
            {
                //datePickerDialog = new DatePickerDialog(SettingActivity.this, SettingActivity.this, 2013, 10, 23);
                // datePickerDialog.show();
                datePickerDialog = new DatePickerDialog(Test_Activity.this,listener,2017,6,12);
                datePickerDialog.show();



/*
                Intent vote_activity_intend = new Intent(Test_Activity.this,VoteActivity.class);
                vote_activity_intend.putExtra("year",year);
                //groupchat_activity_intend.putExtra("get_groupId_text_toString",get_groupId_text_toString);
                vote_activity_intend.putExtra("month",month);
                vote_activity_intend.putExtra("day",day);
                startActivity(vote_activity_intend);
*/
            }



        });
        votelistButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

               // startActivity(new Intent(getApplicationContext(),VoteActivity.class));

                Intent vote_activity_intend = new Intent(Test_Activity.this,VoteActivity.class);
                vote_activity_intend.putExtra("get_groupId_text_toString",get_groupId_text_toString);
               // //groupchat_activity_intend.putExtra("get_groupId_text_toString",get_groupId_text_toString);
                vote_activity_intend.putExtra("id",id);
                //vote_activity_intend.putExtra("day",day);
                startActivity(vote_activity_intend);


            }
        });
    /*
        databaseReference.child("message").addChildEventListener(new ChildEventListener() {  // message는 child의 이벤트를 수신합니다.
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot) {
                ChatData chatData = dataSnapshot.getValue(ChatData.class);  // chatData를 가져오고
                adapter.add(chatData.getUserName()+":"+chatData.getMessage());
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
    */



    }



    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int temp_year, int temp_monthOfYear, int temp_dayOfMonth) {

            year= temp_year;
            month = temp_monthOfYear;
            day = temp_dayOfMonth;

            Toast toast = Toast.makeText(Test_Activity.this, "year: " + year+ ", month: " + month+ " , day: "+ day,Toast.LENGTH_SHORT);
            toast.show();



            LayoutInflater inflater=getLayoutInflater();

            dialogView= inflater.inflate(R.layout.dialog_add_schedule, null);

            buider = new AlertDialog.Builder(Test_Activity.this);
            buider.setTitle("Let make Schedule Vote");
            buider.setView(dialogView);
            buider.setPositiveButton("Vote", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    EditText shedule_title_edit = (EditText)dialogView.findViewById(R.id.schedule_name);
                    EditText shedule_body_edit = (EditText)dialogView.findViewById(R.id.schedule_body);

                    shedule_title = shedule_title_edit.getText().toString();
                    shedule_body = shedule_body_edit.getText().toString();

                    Toast toast2 = Toast.makeText(Test_Activity.this,"title: " + shedule_title + ", body: " + shedule_body ,Toast.LENGTH_SHORT);
                    toast2.show();

               //     VoteData voteData = new VoteData(shedule_title , shedule_body, day,month,year);  // 유저 이름과 메세지로 chatData 만들기



                 //   databaseReference.child("Groups").child(get_groupId_text_toString).child("vote_Room").child(shedule_title).setValue(voteData);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기
                    //  databaseReference.child("message").child("TTTTTT").setValue(users);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기

                    editText.setText("");



                    //Schedule schedule = new Schedule(shedule_body, year,month, day);
                    //databaseReference.child("Groups").child(get_groupId_text_toString).child("vote_Room").child(shedule_title).setValue(schedule);

                }
            });
            buider.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            AlertDialog dialog = buider.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }
    };



}
