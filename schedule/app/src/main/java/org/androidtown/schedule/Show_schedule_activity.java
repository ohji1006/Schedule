package org.androidtown.schedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Show_schedule_activity extends Activity {
/*    private TextView show_shedule_title;
    private TextView show_shedule_desc;
    private TextView show_shedule_time;
    private TextView show_shedule_group;*/

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.7f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_show_schedule_activity);

        Intent intent = getIntent();

        uid = intent.getStringExtra("uid");
        gid = intent.getStringExtra("gid");
        schedule = (Schedule) intent.getSerializableExtra("schedule");
        from_where = intent.getIntExtra("from_where", 0);

        TextView show_shedule_title = (TextView) findViewById(R.id.Title);
        TextView show_schedule_desc = (TextView) findViewById(R.id.s_shedule_desc);
        TextView show_schedule_time = (TextView) findViewById(R.id.s_shedule_time);
        TextView show_schedule_group = (TextView) findViewById(R.id.s_shedule_group);
        ImageView image1 = (ImageView) findViewById(R.id.Image_desc);
        ImageView image2 = (ImageView) findViewById(R.id.Image_date);
        ImageView image3 = (ImageView) findViewById(R.id.Image_group);

        show_shedule_title.setText(schedule.getTitle());
        show_schedule_desc.setText(schedule.getBody());
        show_schedule_group.setText( schedule.getName());

        image1.setImageResource(R.drawable.content);
        image2.setImageResource(R.drawable.chronometer);
        image3.setImageResource(R.drawable.group);
        String timeStr = String.format("%d.%d.%d  %d:%2d", schedule.getDay(), schedule.getMounth()+1,
                schedule.getYear(), schedule.getHour(), schedule.getMinute());
        show_schedule_time.setText( timeStr);
    }
}