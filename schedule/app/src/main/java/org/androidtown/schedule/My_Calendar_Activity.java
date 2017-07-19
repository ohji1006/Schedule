package org.androidtown.schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Random;

public class My_Calendar_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<Schedule> schedule_ArrayList;
    private MaterialCalendarView materialCalendarView;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private String uid,name;
    private FloatingActionButton floatingActionButton1;
    private int year, month, day, hour, minute;
    private DatePickerDialog datePickerDialog;
    private View dialogView;
    private AlertDialog.Builder buider;
    private String shedule_title;
    private String shedule_body;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__calendar_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        schedule_ArrayList = new ArrayList<Schedule>();
        Intent intent = getIntent();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid()+"";
        //uid = intent.getStringExtra("id");
        name  = "user" + new Random().nextInt(10000);

        Toast.makeText(this, uid+"",Toast.LENGTH_SHORT).show();
        materialCalendarView = (MaterialCalendarView)findViewById(R.id.my_calander);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.add_schedule);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(My_Calendar_Activity.this,listener,2017,6,12);
                datePickerDialog.show();
            }
        });

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2014, 0, 1))
                .setMaximumDate(CalendarDay.from(2020, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        CalendarDay calendarDay = new CalendarDay(2017,6,13);
        CalendarDay calendarDay2 = new CalendarDay(2017,6,11);
        HashSet<CalendarDay> date = new HashSet<>();
        date.add(calendarDay);
        date.add(calendarDay2);

        //materialCalendarView.addDecorator(new MyCustomDecorator(Color.RED,4, date));
        //구현한 Decorator(색, 그룹에서 몇번째 사람, 점찍을 날짜)

        //달력 날자 선택시 스케쥴 화면 넘어감 (아래)
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Toast.makeText(My_Calendar_Activity.this, ""+date.getYear()+":"+date.getMonth()+":"+date.getDay(),Toast.LENGTH_SHORT).show();
                //테스트 하기위해

                Intent selected_Day_Shedule_intent = new Intent(My_Calendar_Activity.this, My_Calendar_Selected_Day_Shedule.class);

                selected_Day_Shedule_intent.putExtra("schedule_ArrayList", schedule_ArrayList );
                selected_Day_Shedule_intent.putExtra("uid",uid);
                selected_Day_Shedule_intent.putExtra("year",date.getYear());
                selected_Day_Shedule_intent.putExtra("month",date.getMonth());
                selected_Day_Shedule_intent.putExtra("day",date.getDay());

                startActivity(selected_Day_Shedule_intent);
                //년 월 일 넘겨 줘서 새 액티비티에서 검색하게 하자 (위)
            }
        });
        databaseReference.child("Users").child(uid).child("schedule").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashSet<CalendarDay> date = new HashSet<>();
                schedule_ArrayList.clear();
                //넣기전에 초기화
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Schedule temp_schedule = snapshot.getValue(Schedule.class);  // schedule 데이터를 가져오고
                    schedule_ArrayList.add(temp_schedule);
                    //달력 눌렀을때 모든 스케쥴 정보를 보내주기위해.

                    CalendarDay calendarDay2 = CalendarDay.from(temp_schedule.getYear(),
                                                                 temp_schedule.getMounth(),
                                                                 temp_schedule.getDay());
                    //getNew Instance (위)
                    // 캘린더 년, 달, 일 설정후 HashSet에 넣는다.
                   // schedule_ArrayList.add(temp_schedule);
                    date.add(calendarDay2);
                }
               // materialCalendarView.addDecorator(new MyCustomDecorator(Color.RED,1, date));
                //date에 넣어진 CalendarDay 를 기반으로 달력에 점을 그린다.
                materialCalendarView.addDecorator(new GroupCustomDecorator(Color.rgb(120,43,144), 1, date));

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //파이어 베이스에 사용자 이름 넣었는지 검색. 없으면 자동 이름 설정. 있으면 기존 이름 사용.
        databaseReference.child("Users").child(uid).child("name").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //넣기전에 초기화
                if(dataSnapshot.getValue() == null)
                {
                    Toast.makeText(My_Calendar_Activity.this, "there is no name: auto name get!",Toast.LENGTH_LONG).show();
                    databaseReference.child("Users").child(uid).child("name").setValue(name);
                }
                else
                {
                    name = dataSnapshot.getValue()+"";
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker datePicker, int temp_year, int temp_monthOfYear, int temp_dayOfMonth)
        {
            year= temp_year;
            month = temp_monthOfYear;
            day = temp_dayOfMonth;

            TimePickerDialog time_picker_dialog = new TimePickerDialog(My_Calendar_Activity.this, time_set_listener, 12, 00, false);
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

            buider = new AlertDialog.Builder(My_Calendar_Activity.this);
            buider.setTitle("Let make Schedule");
            buider.setView(dialogView);
            buider.setPositiveButton("Registe", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText shedule_title_edit = (EditText)dialogView.findViewById(R.id.schedule_name);
                    EditText shedule_body_edit = (EditText)dialogView.findViewById(R.id.schedule_body);

                    shedule_title = shedule_title_edit.getText().toString();
                    shedule_body = shedule_body_edit.getText().toString();

                    Toast toast2 = Toast.makeText(My_Calendar_Activity.this, "title: " + shedule_title + ", body: " + shedule_body ,Toast.LENGTH_SHORT);
                    toast2.show();

                  //  Schedule schedule = new Schedule(shedule_body, year,month, day);
                    Schedule schedule = new Schedule(year,month, day, hour, minute ,shedule_title,shedule_body,uid,name );
                    databaseReference.child("Users").child(uid).child("schedule").push().setValue(schedule);
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
                Intent h = new Intent(My_Calendar_Activity.this, SecondActivity.class);
                startActivity(h);
                break;
            case R.id.nav_my_calendar:
                Intent m = new Intent(My_Calendar_Activity.this, My_Calendar_Activity.class);
                startActivity(m);
                break;
            case R.id.nav_group_calendar:
                Intent g = new Intent(My_Calendar_Activity.this, Show_groups_Activity.class);
                g.putExtra("uid",uid);
                startActivity(g);
                break;
            case R.id.nav_setting:
                Intent s = new Intent(My_Calendar_Activity.this, SettingActivity.class);
                s.putExtra("uid", uid);
                startActivity(s);
                break;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(My_Calendar_Activity.this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
