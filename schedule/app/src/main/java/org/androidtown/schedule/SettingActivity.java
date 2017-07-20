package org.androidtown.schedule;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    private String uid;
    private EditText groupId_text;
    private EditText shedule_text;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private int year, month, day, hour, minute;
    private DatePickerDialog datePickerDialog;
    private AlertDialog.Builder buider;
    private String shedule_title;
    private String shedule_body;
    private View dialogView;
    private String get_groupId_text_toString;
    private Button buttonSave,deleteUser,buttonJoin;
    private EditText editTextName, editTextAddress,editGroupName;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        //groupId_text = (EditText)findViewById(R.id.Group_ID_EditText);
        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextAddress = (EditText)findViewById(R.id.editTextAddress);
        editGroupName = (EditText)findViewById(R.id.groupJoin);
        buttonSave = (Button)findViewById(R.id.buttonSave) ;
        deleteUser = (Button)findViewById(R.id.deleteuser);
        buttonJoin = (Button)findViewById(R.id.join_button);

        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //그룹 아이디 임시 저장
                get_groupId_text_toString = editGroupName.getText().toString();
                //get_groupId_text_toString = gname.getText().toString();

                //파베에 동시 업로드 위해 Map에 일시저장
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/Users/" + uid + "/groups/" + get_groupId_text_toString , true);
                childUpdates.put("/Groups/" + get_groupId_text_toString + "/member/" + uid, true);
                databaseReference.updateChildren(childUpdates);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    private void saveUserInformation(){
        String name = editTextName.getText().toString().trim();
        String add = editTextAddress.getText().toString().trim();


        UserInformation userInformation = new UserInformation(name, add);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        //    UID = user.getUid()+"";
        databaseReference.child("Users").child(user.getUid()).setValue(userInformation);

        Toast.makeText(this, "정보 저장완료", Toast.LENGTH_LONG).show();
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int temp_year, int temp_monthOfYear, int temp_dayOfMonth) {

            year= temp_year;
            month = temp_monthOfYear;
            day = temp_dayOfMonth;

            Toast toast = Toast.makeText(SettingActivity.this, "year: " + year+ ", month: " + month+ " , day: "+ day,Toast.LENGTH_SHORT);
            toast.show();



            LayoutInflater inflater=getLayoutInflater();

            dialogView= inflater.inflate(R.layout.dialog_add_schedule, null);

            buider = new AlertDialog.Builder(SettingActivity.this);
            buider.setTitle("Let make Schedule");
            buider.setView(dialogView);
            buider.setPositiveButton("Registe", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                   /* Intent i2 = new Intent(SettingActivity.this, AlarmActivity.class);
                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),0,i2,0);
                    AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
                    am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+3000,pi);
*/

                    EditText shedule_title_edit = (EditText)dialogView.findViewById(R.id.schedule_name);
                    EditText shedule_body_edit = (EditText)dialogView.findViewById(R.id.schedule_body);

                    shedule_title = shedule_title_edit.getText().toString();
                    shedule_body = shedule_body_edit.getText().toString();

                    Toast toast2 = Toast.makeText(SettingActivity.this,"title: " + shedule_title + ", body: " + shedule_body ,Toast.LENGTH_SHORT);
                    toast2.show();

                    Schedule schedule = new Schedule(year,month, day, hour, minute ,shedule_title,shedule_body,"dssd","dsf" );
                  //  Schedule(int year, int mounth, int day, int hour, int minute, String title, String body)
                    databaseReference.child("Users").child(uid).child("schedule").child(shedule_body).setValue(schedule);

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

        switch (id){

            case R.id.nav_home:
                Intent h = new Intent(SettingActivity.this , SecondActivity.class);
                startActivity(h);
                break;
            case R.id.nav_my_calendar:
                Intent m = new Intent(SettingActivity.this , My_Calendar_Activity.class);
                startActivity(m);
                break;
            case R.id.nav_group_calendar:
                Intent g = new Intent(SettingActivity.this , Show_groups_Activity.class);
                startActivity(g);
                break;
            case R.id.nav_setting:
                Intent s = new Intent(SettingActivity.this , SettingActivity.class);
                startActivity(s);
                break;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(SettingActivity.this, LoginActivity2.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
