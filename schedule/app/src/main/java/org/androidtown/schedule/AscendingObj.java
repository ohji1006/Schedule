package org.androidtown.schedule;

import java.util.Comparator;

/**
 * Created by ohji1 on 2017-07-18.
 */

// 차례대로 배열하는 class
class AscendingObj implements Comparator<Schedule> {
    @Override
    public int compare(Schedule o1, Schedule o2) {
        if( o1.getHour() > o2.getHour())return 1;
        else if ( o1.getHour() < o2.getHour()) return -1;
        else {
            if( o1.getMinute() > o2.getMinute()) return 1;
            else if( o1.getMinute() > o2.getMinute()) return -1;
            else
                return 0;
        }
    }
}
