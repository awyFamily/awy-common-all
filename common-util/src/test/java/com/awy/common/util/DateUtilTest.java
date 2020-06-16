package com.awy.common.util;


import com.awy.common.util.utils.DateJdK8Util;
import org.junit.Test;

public class DateUtilTest {

    @Test
    public void parse(){
        DateJdK8Util.parseLocalDateTime("2020-10-22 10:00:00");
    }
}
