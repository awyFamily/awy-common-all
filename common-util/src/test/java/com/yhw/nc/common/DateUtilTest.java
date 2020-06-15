package com.yhw.nc.common;


import com.yhw.nc.common.utils.DateJdK8Util;
import org.junit.Test;

public class DateUtilTest {

    @Test
    public void parse(){
        DateJdK8Util.parseLocalDateTime("2020-10-22 10:00:00");
    }
}
