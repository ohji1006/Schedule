package org.androidtown.schedule;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Show_schedule_activity extends AppCompatActivity {

    private TextView show_shedule_title;
    private TextView show_shedule_month;
    private TextView show_shedule_day;
    private TextView show_shedule_hour;
    private TextView show_shedule_minute;
    private TextView show_shedule_body;
    private Button vote_button;
    private Schedule schedule;
    private String uid,gid;
    private AlertDialog.Builder buider;
    private View dialogView;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private int from_where =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_schedule_activity);

        Intent intent = getIntent();

        uid = intent.getStringExtra("uid");
        gid = intent.getStringExtra("gid");
        schedule = (Schedule) intent.getSerializableExtra("schedule");
        from_where = intent.getIntExtra("from_where", 0);


        show_shedule_title = (TextView) findViewById(R.id.s_shedule_Title);
        show_shedule_month = (TextView) findViewById(R.id.s_shedule_Month);
        show_shedule_day = (TextView) findViewById(R.id.s_shedule_Day);
        show_shedule_hour = (TextView) findViewById(R.id.s_shedule_Hour);
        show_shedule_minute = (TextView) findViewById(R.id.s_shedule_Minute);
        show_shedule_body = (TextView) findViewById(R.id.s_shedule_Body);

        show_shedule_title.setText(schedule.getTitle());
        show_shedule_month.setText("Mounth : " + schedule.getMounth() + "");
        show_shedule_day.setText("Day : " + schedule.getDay() + "");
        show_shedule_hour.setText("Hour : " + schedule.getHour() + "");
        show_shedule_minute.setText("Minute : " + schedule.getMinute() + "");
        show_shedule_body.setText(schedule.getBody());
        vote_button = (Button) findViewById(R.id.vote_button);
        //스케쥴에서 왔을때만 활성화
        if (from_where == 1) {
            vote_button = (Button) findViewById(R.id.vote_button);

            vote_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    buider = new AlertDialog.Builder(Show_schedule_activity.this);
                    buider.setTitle("Vote Agree or Reject");
                    buider.setView(dialogView);
                    buider.setPositiveButton("Vote", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //  childUpdates.put("/Groups/" + get_groupId_text_toString + "/vote_Room/"+data[0]+"/agreement_member/" + iid, true);
                            // 같은 것 하지만 이거 사용
                            databaseReference.child("Groups").child(gid).child("vote_Room").child(show_shedule_title.getText().toString()).
                                    child("agreement_member").child(uid).setValue(true);    //   updateChildren(childUpdates);
                        }
                    });
                    buider.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            databaseReference.child("Groups").child(gid).child("vote_Room").child(show_shedule_title.getText().toString()).
                                    child("agreement_member").child(uid).setValue(false);    //   updateChildren(childUpdates);
                        }
                    });
                    AlertDialog dialog = buider.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            });
        }
        else
        {
            vote_button.setVisibility(View.INVISIBLE);
        }
    }
}
