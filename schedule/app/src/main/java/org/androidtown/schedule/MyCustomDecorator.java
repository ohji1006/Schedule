package org.androidtown.schedule;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by ohji1 on 2017-07-13.
 */

public class MyCustomDecorator implements DayViewDecorator {

    private int color;
    //  private int position;
    private ArrayList<CalendarDay> dates;
    private int group_person_position;

    public MyCustomDecorator(int color,int group_person_position, Collection<CalendarDay> dates) {
        this.dates = new ArrayList<>(dates);
        //    this.position = position;
        this.color = color;
        this.group_person_position = group_person_position;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        //view.addSpan(new DotSpan());
        view.addSpan(new MyCustomSpan(8, color,group_person_position));
    }
}
