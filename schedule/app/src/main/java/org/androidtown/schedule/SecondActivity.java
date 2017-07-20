

package org.androidtown.schedule;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static android.R.id.text1;

public class SecondActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private String uid;
    private String userName;
    private String gid;
    private Button myCalandal_button;
    private Button group_Calandal_button;
    private Button test_button;
    private Button id_setting_button;


    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar=null;
    private ArrayList<String> arrayList_gids;
    private ArrayList<Schedule> arrayList_schedules;
    int listen_count =0;
    private ArrayAdapter<String> adapter;
    private ListView     m_ListView;
    private ListView day_7_listView;
    private View  header;
    private TextView nav_header_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        arrayList_gids = new ArrayList<String>();
        arrayList_schedules = new ArrayList<Schedule>();

        uid = user.getUid()+"";

        userName = "user" + new Random().nextInt(10000);


        //파이어 베이스에 사용자 이름 넣었는지 검색. 없으면 자동 이름 설정. 있으면 기존 이름 사용.
        databaseReference.child("Users").child(uid).child("name").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //넣기전에 초기화
                if(dataSnapshot.getValue() == null)
                {

                    databaseReference.child("Users").child(uid).child("name").setValue( userName );
                }
                else
                {
                    userName = dataSnapshot.getValue()+"";
                }

                if(header == null) {
                    header = LayoutInflater.from(SecondActivity.this).inflate(R.layout.nav_header_second, null);

                    navigationView.addHeaderView(header);
                    nav_header_text = (TextView) header.findViewById(R.id.textView11);
                    nav_header_text.setText(userName);
                }
                else
                {
                    nav_header_text.setText(userName);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        myCalandal_button= (Button) findViewById(R.id.mycalandal_button);
        test_button = (Button) findViewById(R.id.test_button);
        id_setting_button= (Button) findViewById(R.id.id_setting_button);
        group_Calandal_button = (Button) findViewById(R.id.Group_Calendar_Button);
        day_7_listView = (ListView) findViewById(R.id.day_7_schedule_listview);

        myCalandal_button.setText(uid);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                text1);
        day_7_listView.setAdapter( adapter);


        group_Calandal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(gid != null) {
                    Intent show_groups_activity_intent = new Intent(SecondActivity.this, Show_groups_Activity.class);
                    show_groups_activity_intent.putExtra("uid", uid);
                    //show_groups_activity_intent.putExtra("gid", gid);
                    startActivity(show_groups_activity_intent);
                }
            }
        });

        myCalandal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent my_calendar_intent = new Intent(SecondActivity.this,My_Calendar_Activity.class); ;
                my_calendar_intent.putExtra("id",uid);
                startActivity(my_calendar_intent);
            }
        });
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //지움
                Intent test_activity_intent = new Intent(SecondActivity.this,Test_Activity.class); ;
                test_activity_intent.putExtra("id",uid);
                startActivity(test_activity_intent);
            }
        });
        id_setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent setting_activity_intent = new Intent(SecondActivity.this,SettingActivity.class);
                setting_activity_intent.putExtra("id",uid);
                startActivity(setting_activity_intent);
            }
        });


        // 유저 일정 다 받아오기.
        databaseReference.child("Users").child(uid).child("schedule").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                arrayList_schedules.clear();
                adapter.clear();
                //넣기전에 초기화
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Schedule temp_schedule = snapshot.getValue(Schedule.class);

                    arrayList_schedules.add(temp_schedule);
                    Toast.makeText(SecondActivity.this, "user uid schdule"+ snapshot.getValue(Schedule.class).getTitle(),Toast.LENGTH_SHORT).show();
                  //  m_Adapter.add("user uid schdule"+ snapshot.getValue(Schedule.class).getTitle() );
                    adapter.add( temp_schedule.getTitle()+" : " + temp_schedule.getBody() );
                }
                listen_count = listen_count +1;

                after_get_all_schedule_ascending();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //모든 그룹 gid 받아오기.
        databaseReference.child("Users").child(uid).child("groups").addValueEventListener(new ValueEventListener()
        {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    arrayList_gids.clear();
                    //넣기전에 초기화
                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        gid = snapshot.getKey()+"";
                        arrayList_gids.add(gid);
                        Toast.makeText(getApplicationContext(), "get gid : "+ gid ,Toast.LENGTH_SHORT).show();
                    }

                    //속한 그룹마다 스케쥴 가져온다.
                    for(int i =0; i< arrayList_gids.size() ; i++)
                    {
                        databaseReference.child("Groups").child(arrayList_gids.get(i)).child("schedule").addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                                {
                                    Schedule group_temp_schedule = snapshot.getValue(Schedule.class);  // schedule 데이터를 가져오고
                                    if(group_temp_schedule == null)
                                    {
                                        break;
                                    }
                                    arrayList_schedules.add(group_temp_schedule);
                                }
                                listen_count = listen_count +1;

                                after_get_all_schedule_ascending();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {

                            }
                        });
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                }
        });



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    public void after_get_all_schedule_ascending()
    {
       // if(listen_count+1 == arrayList_gids.size())
       // {
            AscendingObj ascending = new AscendingObj();
            Collections.sort( arrayList_schedules, ascending);

            for(Schedule schedules :arrayList_schedules )
            {
                adapter.add(schedules.getTitle()+" : " + schedules.getBody() );
            }
    //    }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){

            case R.id.nav_home:
                Intent h = new Intent(SecondActivity.this , SecondActivity.class);
                startActivity(h);
                break;
            case R.id.nav_my_calendar:
                Intent m = new Intent(SecondActivity.this , My_Calendar_Activity.class);
                startActivity(m);
                break;
            case R.id.nav_group_calendar:
                Intent g = new Intent(SecondActivity.this , Show_groups_Activity.class);
                g.putExtra("uid", uid);
                startActivity(g);
                break;
            case R.id.nav_setting:
                Intent s = new Intent(SecondActivity.this ,SettingActivity.class);
                s.putExtra("uid", uid);
                startActivity(s);
                break;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(SecondActivity.this, LoginActivity2.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
