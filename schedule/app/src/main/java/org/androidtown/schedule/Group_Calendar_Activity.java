package org.androidtown.schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.HashSet;

public class Group_Calendar_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    ArrayList<String> group_members_uids = new ArrayList<>();
    MaterialCalendarView materialCalendarView;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String uid;
    private String gid;
    private FloatingActionButton fab_add_chat;
    private FloatingActionButton fab_add_vote;
    private FloatingActionButton fab_vote_list;
    private DatePickerDialog datePickerDialog;
    private ArrayList<Schedule> array_schedule;
    private ArrayList<Schedule> array_group_schedule;
    private ArrayList< ArrayList<CalendarDay>> array_hash_set;
    private int year, month, day, hour, minute;
    private View dialogView;
    private AlertDialog.Builder buider;
    private String shedule_title;
    private String shedule_body;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group__calendar_);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        gid = intent.getStringExtra("gid");
        uid = intent.getStringExtra("uid");

        Toast.makeText(this, uid + ":::!!!!!!!!!!!!!!!!!::: " + gid, Toast.LENGTH_LONG).show();

        setTitle(gid +" Calendar");
        materialCalendarView = (MaterialCalendarView) findViewById(R.id.group_calander);

        fab_add_chat = (FloatingActionButton) findViewById(R.id.add_chat);
        fab_add_vote = (FloatingActionButton) findViewById(R.id.add_vote);
        fab_vote_list = (FloatingActionButton) findViewById(R.id.vote_list);

        fab_vote_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent vote_list_intent = new Intent(Group_Calendar_Activity.this, VoteActivity.class);

                vote_list_intent.putExtra("uid", uid );
                vote_list_intent.putExtra("gid", gid );

                startActivity(vote_list_intent);
            }
        });

        fab_add_chat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

                Intent group_chat_room_intent = new Intent(Group_Calendar_Activity.this, Group_chat_room_activity.class);

                group_chat_room_intent.putExtra("uid", uid );
                group_chat_room_intent.putExtra("gid", gid );

                startActivity(group_chat_room_intent);
            }
        });
        fab_add_vote.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //시간과 일정을 입력 받는다.
                datePickerDialog = new DatePickerDialog(Group_Calendar_Activity.this,listener,2017,6,12);
                datePickerDialog.show();
            }
        });
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2014, 0, 1))
                .setMaximumDate(CalendarDay.from(2020, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        CalendarDay calendarDay = new CalendarDay(2017, 6, 13);
        CalendarDay calendarDay2 = new CalendarDay(2017, 6, 11);
        HashSet<CalendarDay> date = new HashSet<>();
        date.add(calendarDay);
        date.add(calendarDay2);

        // materialCalendarView.addDecorator(new MyCustomDecorator(Color.RED,4, date));
        //구현한 Decorator(색, 그룹에서 몇번째 사람, 점찍을 날짜)

        //달력 날자 선택시 스케쥴 화면 넘어감 (아래)
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Toast.makeText(Group_Calendar_Activity.this, "" + date.getYear() + ":" + date.getMonth() + ":" + date.getDay(), Toast.LENGTH_SHORT).show();
                //테스트 하기위해

                Intent selected_Day_Shedule_intent = new Intent(Group_Calendar_Activity.this, Group_Calendar_Selected_Day_Shedule_Activity.class);

                selected_Day_Shedule_intent.putExtra("schedule_ArrayList", array_schedule);
                selected_Day_Shedule_intent.putExtra("uid", uid);
                selected_Day_Shedule_intent.putExtra("year", date.getYear());
                selected_Day_Shedule_intent.putExtra("month", date.getMonth());
                selected_Day_Shedule_intent.putExtra("day", date.getDay());
                startActivity(selected_Day_Shedule_intent);
                //년 월 일 넘겨 줘서 새 액티비티에서 검색하게 하자 (위)
            }
        });
        //gid로  group안의 멤버 검색
        databaseReference.child("Groups").child(gid).child("member").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                group_members_uids.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String temp_id = snapshot.getKey() + "";
                    group_members_uids.add(temp_id);
                }
                array_hash_set = new ArrayList<ArrayList<CalendarDay>>();
                array_schedule = new ArrayList<Schedule>();

                for(int i =0; i< group_members_uids.size(); i++)
                {
                    //  Toast.makeText(Group_Calendar_Activity.this, "group_members_uids: " + group_members_uids.get(i), Toast.LENGTH_SHORT).show();

                    databaseReference.child("Users").child(group_members_uids.get(i)).child("schedule").addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            ArrayList<CalendarDay> date = new ArrayList<>();
                            for(DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                Schedule temp_schedule = snapshot.getValue(Schedule.class);  // schedule 데이터를 가져오고
                                if(temp_schedule == null)
                                {
                                    break;
                                }
                                //달력 버튼을 눌렀을때 화면에 날자에 해당하는 리스트를 띄우기 위해 저장.
                                array_schedule.add(temp_schedule);
                                CalendarDay calendarDay2 = CalendarDay.from(temp_schedule.getYear(),
                                        temp_schedule.getMounth(),
                                        temp_schedule.getDay());
                                //가져온 데이터로 날자를 지정하고
                                date.add(calendarDay2);
                                //어레이 리스트에 저장.
                            }
                            array_hash_set.add(date);
                            //어레이 리스트의 어레이 리스트에 날자 저장

                            if(array_hash_set.size() == group_members_uids.size())
                            {
                                //사람 수랑 받은 데이터 수가 일치할때 그린다.
                                for(int i = 0; i< array_hash_set.size() ; i++)
                                {
                                    //    Toast.makeText(Group_Calendar_Activity.this, "i : "+ i +": "+array_hash_set.size() + " : " + group_members_uids.size(),Toast.LENGTH_LONG).show();
                                    //저장된 날자들로 점을 그린다!.
                                    switch (i)
                                    {
                                        case 0:
                                            materialCalendarView.addDecorator(new MyCustomDecorator(Color.RED, 1, array_hash_set.get(0)));
                                            break;
                                        case 1:
                                            materialCalendarView.addDecorator(new MyCustomDecorator(Color.BLUE, 2,  array_hash_set.get(1)));
                                            break;
                                        case 2:
                                            materialCalendarView.addDecorator(new MyCustomDecorator(Color.GREEN, 3,  array_hash_set.get(2)));
                                            break;
                                        case 3:
                                            materialCalendarView.addDecorator(new MyCustomDecorator(Color.YELLOW, 4,  array_hash_set.get(3)));
                                            break;
                                        case 4:
                                            materialCalendarView.addDecorator(new MyCustomDecorator(Color.MAGENTA, 5,  array_hash_set.get(4)));
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                //그룹 스케쥴 받아오고 그린다.
                databaseReference.child("Groups").child(gid).child("schedule").addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        ArrayList<CalendarDay> group_date = new ArrayList<>();
                        for(DataSnapshot snapshot: dataSnapshot.getChildren())
                        {
                            Schedule group_temp_schedule = snapshot.getValue(Schedule.class);  // schedule 데이터를 가져오고
                            if(group_temp_schedule == null)
                            {
                                break;
                            }
                            //달력 버튼을 눌렀을때 화면에 날자에 해당하는 리스트를 띄우기 위해 저장.
                            array_schedule.add(group_temp_schedule);

                            CalendarDay calendarDay2 = CalendarDay.from(group_temp_schedule.getYear(),
                                    group_temp_schedule.getMounth(),
                                    group_temp_schedule.getDay());
                            //가져온 데이터로 날자를 지정하고
                            group_date.add(calendarDay2);
                        }
                        materialCalendarView.addDecorator(new GroupCustomDecorator(Color.rgb(120,43,144), 1, group_date));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker datePicker, int temp_year, int temp_monthOfYear, int temp_dayOfMonth)
        {
            year= temp_year;
            month = temp_monthOfYear;
            day = temp_dayOfMonth;

            TimePickerDialog time_picker_dialog = new TimePickerDialog(Group_Calendar_Activity.this, time_set_listener, 12, 00, false);
            time_picker_dialog.show();

        }
    };
    private TimePickerDialog.OnTimeSetListener time_set_listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minuteOfhour)
        {
            hour = hourOfDay;
            minute = minuteOfhour;

            LayoutInflater inflater=getLayoutInflater();
            dialogView= inflater.inflate(R.layout.dialog_add_schedule, null);

            buider = new AlertDialog.Builder(Group_Calendar_Activity.this);
            buider.setTitle("Let make Schedule");
            buider.setView(dialogView);
            buider.setPositiveButton("Registe", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText shedule_title_edit = (EditText)dialogView.findViewById(R.id.schedule_name);
                    EditText shedule_body_edit = (EditText)dialogView.findViewById(R.id.schedule_body);

                    shedule_title = shedule_title_edit.getText().toString();
                    shedule_body = shedule_body_edit.getText().toString();

                    Toast toast2 = Toast.makeText(Group_Calendar_Activity.this, "title: " + shedule_title + ", body: " + shedule_body ,Toast.LENGTH_SHORT);
                    toast2.show();

                    Schedule schedule = new Schedule(year,month, day, hour, minute ,shedule_title,shedule_body,uid,gid );//name대신 gid 넣음
                    databaseReference.child("Groups").child(gid).child("vote_Room").child(shedule_title).setValue(schedule);
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
                Intent h = new Intent(Group_Calendar_Activity.this, SecondActivity.class);
                startActivity(h);
                break;
            case R.id.nav_my_calendar:
                Intent m = new Intent(Group_Calendar_Activity.this, My_Calendar_Activity.class);
                startActivity(m);
                break;
            case R.id.nav_group_calendar:
                Intent g = new Intent(Group_Calendar_Activity.this, Show_groups_Activity.class);
                startActivity(g);
                break;
            case R.id.nav_setting:
                Intent s = new Intent(Group_Calendar_Activity.this, ProfileActivity.class);
                s.putExtra("uid", uid);
                startActivity(s);
                break;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(Group_Calendar_Activity.this, LoginActivity2.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
