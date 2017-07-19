package org.androidtown.schedule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Show_groups_Activity extends AppCompatActivity
{
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private String uid;
    private ArrayList<String> arrayList_gid;
    private ArrayAdapter<String> adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_groups_);
        listView = (ListView) findViewById(R.id.show_groups_listview);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        arrayList_gid= new ArrayList<String>();

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                Intent group_calendar_intent = new Intent(Show_groups_Activity.this, Group_Calendar_Activity.class);

                //리스트 뷰 클릭시 해당 스케쥴 상세 보임 액티비티로 이동

                group_calendar_intent.putExtra("uid", uid);
                group_calendar_intent.putExtra("gid", arrayList_gid.get(position));

                startActivity( group_calendar_intent);
            }
        });


        //자신이 속한 그룹을 모두 가져온다.
        databaseReference.child("Users").child(uid).child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //넣기전에 초기화
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String temp_gid = snapshot.getKey()+"";
                    arrayList_gid.add(temp_gid);
                    adapter.add(temp_gid);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


}
