

package org.androidtown.schedule;

import android.content.Intent;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Random;

import static android.R.id.text1;

public class SecondActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
    Toolbar toolbar = null;
    private ArrayList<String> arrayList_gids;
    private ArrayList<Schedule> arrayList_schedules;
    private ArrayList<Schedule> arrayList_group_schedules;
    int listen_count = 0;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> group_adapter;
    private ListView group_day_7_listView;
    private ListView my_day_7_listView;
    private View header;
    private TextView nav_header_text;
    private Button go_to_mycalendar_Button;
    private Button go_to_groupcalendar_Button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        arrayList_gids = new ArrayList<String>();

        arrayList_schedules = new ArrayList<Schedule>();
        arrayList_group_schedules = new ArrayList<Schedule>();

        uid = user.getUid() + "";
        userName = "user" + new Random().nextInt(10000);


        //파이어 베이스에 사용자 이름 넣었는지 검색. 없으면 자동 이름 설정. 있으면 기존 이름 사용.
        databaseReference.child("Users").child(uid).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //넣기전에 초기화
                if (dataSnapshot.getValue() == null) {

                    databaseReference.child("Users").child(uid).child("name").setValue(userName);
                } else {
                    userName = dataSnapshot.getValue() + "";
                }

                if (header == null) {
                    header = LayoutInflater.from(SecondActivity.this).inflate(R.layout.nav_header_second, null);

                    navigationView.addHeaderView(header);
                    nav_header_text = (TextView) header.findViewById(R.id.textView11);
                    nav_header_text.setText(userName);
                } else {
                    nav_header_text.setText(userName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        my_day_7_listView = (ListView) findViewById(R.id.day_7_schedule_listview);
        group_day_7_listView = (ListView) findViewById(R.id.group_7_schedule_listview);
        go_to_mycalendar_Button = (Button) findViewById( R.id.day7_go_myCalendar) ;
        go_to_groupcalendar_Button = (Button) findViewById( R.id.day7_go_groupCalendar) ;

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                text1);
        group_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                text1);
        my_day_7_listView.setAdapter(adapter);
        group_day_7_listView.setAdapter(group_adapter);

        go_to_mycalendar_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent m = new Intent(SecondActivity.this, My_Calendar_Activity.class);
                startActivity(m);

            }
        });
        go_to_groupcalendar_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent g = new Intent(SecondActivity.this, Show_groups_Activity.class);
                g.putExtra("uid", uid);
                startActivity(g);
            }
        });

        // 유저 일정 다 받아오기.
        databaseReference.child("Users").child(uid).child("schedule").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                arrayList_schedules.clear();

                //넣기전에 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Schedule temp_schedule = snapshot.getValue(Schedule.class);
                    arrayList_schedules.add(temp_schedule);
                 //   Toast.makeText(SecondActivity.this, "user uid schdule" + snapshot.getValue(Schedule.class).getTitle(), Toast.LENGTH_SHORT).show();
                }
                listen_count = listen_count + 1;
                //유저와 그룹 모두 들으면 날짜 별로 정렬후 adapter연결

                AscendingObj ascending = new AscendingObj();
                Collections.sort(arrayList_schedules, ascending);

                //날자 정보 받아옴
                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                String formated_date_String = dateFormat.format(gregorianCalendar.getTime());
             //   Toast.makeText(SecondActivity.this, formated_date_String + " ", Toast.LENGTH_LONG).show();

                // 지오메트리 정보 string 받은후  int 형태로 변환
                String string_dates[] = new String[3];
                string_dates[0] = formated_date_String.substring(0, 4);
                string_dates[1] = formated_date_String.substring(5, 7);
                string_dates[2] = formated_date_String.substring(8);
                int int_dates[] = new int[3];
                for (int i = 0; i < string_dates.length; i++) {
                    int_dates[i] = Integer.parseInt(string_dates[i]);
                }
                int_dates[1] = int_dates[1] - 1;


               // Toast.makeText(SecondActivity.this, "NOW!! Year: " + int_dates[0] + " month: " + int_dates[1] + " day: " + int_dates[2], Toast.LENGTH_LONG).show();

                for (Schedule schedules : arrayList_schedules)
                {
                  //  Toast.makeText(SecondActivity.this, "User Schedule Year: " + schedules.getYear() + " month: " + schedules.getMounth() + " day: " + schedules.getDay(), Toast.LENGTH_LONG).show();
                  //  Toast.makeText(SecondActivity.this, "NOW!! Year:         " + int_dates[0] + " month: " + int_dates[1] + " day: " + int_dates[2], Toast.LENGTH_LONG).show();
                    if (int_dates[0] == schedules.getYear() && int_dates[1] == schedules.getMounth() && (int_dates[2] <= schedules.getDay() && int_dates[2] >= schedules.getDay() - 7))
                        adapter.add("Title : " + schedules.getTitle() + "                   Date: " + schedules.getDay()+ "."+schedules.getMounth()+ "."+schedules.getYear());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //모든 그룹 gid 받아오기.
        databaseReference.child("Users").child(uid).child("groups").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList_gids.clear();
                //넣기전에 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    gid = snapshot.getKey() + "";
                    arrayList_gids.add(gid);
                   // Toast.makeText(getApplicationContext(), "get gid : " + gid, Toast.LENGTH_SHORT).show();
                }

                //속한 그룹마다 스케쥴 가져온다.
                for (int i = 0; i < arrayList_gids.size(); i++)
                {
                    databaseReference.child("Groups").child(arrayList_gids.get(i)).child("schedule").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                Schedule group_temp_schedule = snapshot.getValue(Schedule.class);  // schedule 데이터를 가져오고
                                if (group_temp_schedule == null) {
                                    break;
                                }
                                arrayList_group_schedules.add(group_temp_schedule);
                            }
                            listen_count = listen_count + 1;

                            //유저와 그룹 모두 들으면 날짜 별로 정렬후 adapter연결
                            if (listen_count == arrayList_gids.size() + 1) {
                                AscendingObj ascending = new AscendingObj();
                                Collections.sort(arrayList_group_schedules, ascending);


                                //날자 정보 받아옴
                                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                                String formated_date_String = dateFormat.format(gregorianCalendar.getTime());
                             //   Toast.makeText(SecondActivity.this, formated_date_String + " ", Toast.LENGTH_LONG).show();

                                // 지오메트리 정보 string 받은후  int 형태로 변환
                                String string_dates[] = new String[3];
                                string_dates[0] = formated_date_String.substring(0, 4);
                                string_dates[1] = formated_date_String.substring(5, 7);
                                string_dates[2] = formated_date_String.substring(8);
                                int int_dates[] = new int[3];
                                for (int i = 0; i < string_dates.length; i++) {
                                    int_dates[i] = Integer.parseInt(string_dates[i]);
                                }
                                int_dates[1] = int_dates[1] - 1;


                             //   Toast.makeText(SecondActivity.this, "NOW!! Year: " + int_dates[0] + " month: " + int_dates[1] + " day: " + int_dates[2], Toast.LENGTH_LONG).show();

                                for (Schedule schedules : arrayList_group_schedules) {
                             //       Toast.makeText(SecondActivity.this, "Schedule Year: " + schedules.getYear() + " month: " + schedules.getMounth() + " day: " + schedules.getDay(), Toast.LENGTH_LONG).show();

                                    if (int_dates[0] == schedules.getYear() && int_dates[1] == schedules.getMounth() && (int_dates[2] <= schedules.getDay() && int_dates[2] >= schedules.getDay() - 7))
                                        group_adapter.add("Title : " + schedules.getTitle() + "                   Date: " + schedules.getDay()+ "."+schedules.getMounth()+ "."+schedules.getYear());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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


    public void after_get_all_schedule_ascending() {
        // if(listen_count+1 == arrayList_gids.size())
        // {
        AscendingObj ascending = new AscendingObj();
        Collections.sort(arrayList_schedules, ascending);

        for (Schedule schedules : arrayList_schedules) {
            adapter.add(schedules.getTitle() + " : " + schedules.getBody());
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {

            case R.id.nav_home:
                Intent h = new Intent(SecondActivity.this, SecondActivity.class);
                startActivity(h);
                break;
            case R.id.nav_my_calendar:
                Intent m = new Intent(SecondActivity.this, My_Calendar_Activity.class);
                startActivity(m);
                break;
            case R.id.nav_group_calendar:
                Intent g = new Intent(SecondActivity.this, Show_groups_Activity.class);
                g.putExtra("uid", uid);
                startActivity(g);
                break;
            case R.id.nav_setting:
                Intent s = new Intent(SecondActivity.this, SettingActivity.class);
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
