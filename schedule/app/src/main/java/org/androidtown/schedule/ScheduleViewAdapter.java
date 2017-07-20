package org.androidtown.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ohji1 on 2017-07-18.
 */

public class ScheduleViewAdapter extends BaseAdapter {
    private ArrayList<Schedule> ScheduleViewItemList = new ArrayList<Schedule>();
    private boolean is_group = false;
    public ScheduleViewAdapter(){}
    public ScheduleViewAdapter(boolean groupflag){
        is_group = groupflag;
    }
    @Override
    public int getCount(){
        return ScheduleViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if( is_group)
                convertView = inflater.inflate(R.layout.group_schedule_item, parent, false);
            else
                convertView = inflater.inflate(R.layout.schedule_item, parent, false);
        }

        TextView nameTextView = (TextView) convertView.findViewById(R.id.sch_name);
        TextView descTextView = (TextView) convertView.findViewById(R.id.sch_cont);
        TextView startTextView = (TextView) convertView.findViewById(R.id.sch_start);
        // TextView stdayTextView = (TextView) convertView.findViewById(R.id.sch_stday);
        // 시간

        Schedule schedule = ScheduleViewItemList.get(position);

        nameTextView.setText( schedule.getTitle());
        descTextView.setText( "   " + schedule.getBody());
        // stdayTextView.setText("   "+ schedule.getDay() + ". " + schedule.getMounth() + ". " + schedule.getYear());

        if( is_group) {
            TextView userTextView = (TextView) convertView.findViewById(R.id.sch_user);
            userTextView.setText( " " + schedule.getName());
        }

        String timestr = "   " + schedule.getHour() + " : " + schedule.getMinute();
        if( schedule.getMinute() == 0) timestr += "0";
        startTextView.setText(timestr);

        return convertView;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public Object getItem(int position){
        return ScheduleViewItemList.get(position);
    }

    public void addItem(Schedule item){
        ScheduleViewItemList.add(item);
    }
}
