package org.androidtown.schedule;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.Map;

import static android.R.id.text1;
//import android.content.Intent;

//import android.content.Intent;

/**
 * Created by Ei Seok on 2017-07-14.
 */

public class VoteActivity extends AppCompatActivity
{
    private FirebaseAuth firebaseAuth;
    private String Message;
    private String[] data;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private ArrayAdapter<String> adapter;
    private AlertDialog.Builder buider;
    private String uid;
    private String gid;
    private ListView listView;
    private ArrayList<Schedule> arraylist_schedule;
    private View dialogView;
    private int position;
    private int member_count =0;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        Intent intent = getIntent();
        gid = intent.getStringExtra("gid");
        uid = intent.getStringExtra("uid");

        Toast.makeText(this, "gid: " + gid , Toast.LENGTH_SHORT).show();;
        listView = (ListView) findViewById(R.id.vote_listView);
        //listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        arraylist_schedule = new ArrayList<Schedule>();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                text1);
        listView.setAdapter( adapter);
        listView.setOnItemClickListener(itemClickListenerOfLanguageList);
        listView.setOnItemLongClickListener(itemLongClickListener);

        //그룹안에있는 투표 리스트를 모두 받아옴.
        databaseReference.child("Groups").child(gid).child("vote_Room").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                adapter.clear();
                arraylist_schedule.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Schedule schedule_Data = snapshot.getValue( Schedule.class);

                    arraylist_schedule.add(schedule_Data);
                    adapter.add(schedule_Data.getTitle()+":"+schedule_Data.getBody());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //그룹의 멤버수 받아오기
        databaseReference.child("Groups").child(gid).child("member").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                member_count = (int)dataSnapshot.getChildrenCount();
                Toast.makeText(VoteActivity.this, "Member_cout : "+ member_count,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    // 클릭 리스너를 통해 상세 보기 들어감.
    private AdapterView.OnItemClickListener itemClickListenerOfLanguageList = new AdapterView.OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> adapterView, View clickedView, int pos, long id)
        {
            Toast.makeText(VoteActivity.this, "position: "+position+", id:"+ id,Toast.LENGTH_SHORT).show();

            Intent show_schedule_intent = new Intent(VoteActivity.this, Show_schedule_activity.class);

            show_schedule_intent.putExtra("schedule",  arraylist_schedule.get(position) );
            startActivity(show_schedule_intent);
        }
    };
    // 롱클릭 리스너를 통해 투표 실시.
    private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {

            position = pos;
            //final String vo = (String)adapterView.getAdapter().getItem(0);

            buider = new AlertDialog.Builder(VoteActivity.this);
            buider.setTitle("Vote Agree or Reject");
           // buider.setView(dialogView);
            buider.setPositiveButton("Vote", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    //  childUpdates.put("/Groups/" + get_groupId_text_toString + "/vote_Room/"+data[0]+"/agreement_member/" + iid, true);
                    // 같은 것 하지만 이거 사용
                    databaseReference.child("Groups").child(gid).child("vote_Progress").child(arraylist_schedule.get( position).getTitle()).
                           child(uid).setValue(true);    //   updateChildren(childUpdates);

                    add_vote_progress_listner();
                }
            });
            buider.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    databaseReference.child("Groups").child(gid).child("vote_Progress").child(arraylist_schedule.get( position).getTitle()).
                            child(uid).setValue(false);    //   updateChildren(childUpdates);
                }
            });
            AlertDialog dialog = buider.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();



            return true;
        }
    };

    //vote를 보냈을때 투표 진행을 확인하고 찬반 수를 카운트 하기 위해 리스너 실행!!
    private void add_vote_progress_listner()
    {
        databaseReference.child("Groups").child(gid).child("vote_Progress").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String name = snapshot.getKey()+"";
                    Toast.makeText(VoteActivity.this, "Name is : "+name, Toast.LENGTH_SHORT).show();

                    //디비 돌다가 선택한 Title 과 같으면 ,  해당 Title에 찬성한 인원 카운트
                    if(name.equals(arraylist_schedule.get(position).getTitle()) )
                    {
                        Toast.makeText(VoteActivity.this,"Title match!!!", Toast.LENGTH_SHORT).show();
                        //해당 Title에 찬성한 인원 카운트
                        int agree_count = 0;
                        //투표한 사람의 인원
                        int voted_all_member_count=0;

                        //Groups - gid - vote_Progress - same_Name - agreement_member
                        //DataSnapshot agreement_member_datasnapshot = (DataSnapshot) snapshot.getChildren();

                        for(DataSnapshot child_snapshot : snapshot.getChildren())
                        {
                            voted_all_member_count = voted_all_member_count+1;
                            if((Boolean)child_snapshot.getValue() == true)
                            {
                                agree_count = agree_count+1;
                            }
                        }

                        Toast.makeText(VoteActivity.this,"Agree count: " + agree_count, Toast.LENGTH_SHORT).show();
                        //그룹의 멤버수와 찬성수 비교 전원 찬성시 그룹 일정으로 들어감.
                        if(agree_count == member_count)
                        {
                            //스케줄 가져오고
                            Schedule schedule =  arraylist_schedule.get(position);
                            //그룹 스케쥴에 넣고
                            databaseReference.child("Groups").child(gid).child("schedule").push().setValue(schedule);
                            //투표진행에서 삭제
                            databaseReference.child("Groups").child(gid).child("vote_Progress").
                                    child(arraylist_schedule.get(position).getTitle()).setValue(null);
                            //투표 내용 삭제
                            databaseReference.child("Groups").child(gid).child("vote_Room").
                                    child(arraylist_schedule.get(position).getTitle()).setValue(null);
                        }// 만장일치가 아닌 경우
                        else if( voted_all_member_count == member_count)
                        {
                            databaseReference.child("Groups").child(gid).child("vote_Progress").
                                    child(arraylist_schedule.get(position).getTitle()).setValue(null);
                            //투표 내용 삭제
                            databaseReference.child("Groups").child(gid).child("vote_Room").
                                    child(arraylist_schedule.get(position).getTitle()).setValue(null);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
